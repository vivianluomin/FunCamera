package com.example.asus1.funcamera.RecordVideo.Encoder;

import android.media.AudioFormat;
import android.media.AudioRecord;
import android.media.MediaCodec;
import android.media.MediaCodecInfo;
import android.media.MediaFormat;
import android.media.MediaRecorder;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Looper;
import android.support.annotation.IntRange;
import android.support.annotation.NonNull;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.util.Log;

import com.example.asus1.funcamera.RecordVideo.RecordUtil.AudioRecordEncode;

import java.io.IOException;
import java.io.PipedReader;
import java.nio.ByteBuffer;
import java.util.LinkedList;

public class AudioEncoder implements Runnable {

    private MediaCodec mAudioCodec;
    private MediaFormat mAudioFormat;
    private static final String AUDIO_MIME = "audio/mp4a-latm";
    private static final int SAMPLE_RATE = 44100;
    private static final int CHANNEL_COUNT = AudioFormat.CHANNEL_IN_MONO;
    private static final int ENCODING_FORMAT = AudioFormat.ENCODING_PCM_16BIT;
    private boolean isRecoding = false;
    LinkedList<AudioFrame> audioFrames = new LinkedList<>();
    private Object mLock = new Object();
    private boolean mEnd = false;
    private AudioRecoderThread mAudioRecord;

    private VideoMuxer mMuxer;

    private Handler mAudioHandler;
    private HandlerThread mHandlerThread;
    private int audioCount = 0;

    private static final String TAG = "AudioEncoder";

    public AudioEncoder(VideoMuxer muxer) {
        mMuxer = muxer;
        try {
            mAudioHandler = createHandler(true);
            mAudioCodec = MediaCodec.createEncoderByType(AUDIO_MIME);
            mAudioFormat = MediaFormat.createAudioFormat(AUDIO_MIME,
                    44100, AudioFormat.CHANNEL_IN_STEREO);
            mAudioFormat.setInteger(MediaFormat.KEY_AAC_PROFILE,
                    MediaCodecInfo.CodecProfileLevel.AACObjectLC);
            mAudioFormat.setInteger(MediaFormat.KEY_CHANNEL_MASK, AudioFormat.CHANNEL_IN_MONO);
            mAudioFormat.setInteger(MediaFormat.KEY_BIT_RATE, 64000);
            mAudioFormat.setInteger(MediaFormat.KEY_CHANNEL_COUNT, 1);
            mAudioCodec.setCallback(mCallBack, mAudioHandler);
            mAudioCodec.configure(mAudioFormat, null, null, MediaCodec.CONFIGURE_FLAG_ENCODE);
            mAudioRecord = new AudioRecoderThread();

        } catch (IOException e) {

        }

    }

    private android.os.Handler createHandler(boolean async) {
        if (async) {
            try {
                if (mHandlerThread != null) {
                    mHandlerThread.quit();
                }
                mHandlerThread = new HandlerThread(TAG);
                mHandlerThread.start();
                return new android.os.Handler(mHandlerThread.getLooper());
            } catch (Exception e) {

            }
        }
        return new android.os.Handler(Looper.myLooper() != null ? Looper.myLooper() : Looper.getMainLooper());
    }

    @Override
    public void run() {
        mAudioRecord.startRecoding();
        mAudioCodec.start();
    }

    public void startRecoding() {
        isRecoding = true;
        new Thread(this).start();
    }

    public void stopRecoding() {
        isRecoding = false;
        //mLock.notifyAll();
    }

    private void clear() {
        Log.d(TAG, "clear: ");
        mAudioCodec.stop();
        mAudioRecord.stopRecording();
        mAudioCodec.release();
        mAudioCodec = null;
    }

    private MediaCodec.Callback mCallBack = new MediaCodec.Callback() {
        @Override
        public void onInputBufferAvailable(@NonNull MediaCodec codec, int index) {
            while (true) {
                if (audioCount > 0) {
                    synchronized (mLock) {
                        ByteBuffer byteBuffer = codec.getInputBuffer(index);
                        byteBuffer.clear();
                        AudioFrame frame = audioFrames.removeFirst();
                        --audioCount;
                        if (frame.size <= 0) {
                            Log.d(TAG, "onInputBufferAvailable: end of stream");
                            codec.queueInputBuffer(index, 0, 0, frame.pts, MediaCodec.BUFFER_FLAG_END_OF_STREAM);

                        } else {
                            byteBuffer.put(frame.data);
                            codec.queueInputBuffer(index, 0, frame.size, frame.pts, 0);
                        }
                    }

                    break;

                } else if(isRecoding){
                    synchronized (mLock) {
                        try {
                            mLock.wait();
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                }else {
                    break;
                }

            }


        }

        @Override
        public void onOutputBufferAvailable(@NonNull MediaCodec codec, int index, @NonNull MediaCodec.BufferInfo info) {
            if (info.presentationTimeUs != 0) {
                ByteBuffer byteBuffer = codec.getOutputBuffer(index);
                info.presentationTimeUs = getPTS();
                if(info.presentationTimeUs == -1){
                    codec.releaseOutputBuffer(index, false);
                    return;
                }
                mMuxer.addData(1, byteBuffer,info.presentationTimeUs,info.size,info.flags);
                codec.releaseOutputBuffer(index, false);
                if (info.flags == MediaCodec.BUFFER_FLAG_END_OF_STREAM) {
                    mEnd = true;
                    Log.d(TAG, "onOutputBufferAvailable: audio end of stream");
                    mMuxer.clear();
                    clear();
                }
            } else {
                codec.releaseOutputBuffer(index, false);
            }

        }

        @Override
        public void onError(@NonNull MediaCodec codec, @NonNull MediaCodec.CodecException e) {
            Log.d(TAG, "onError: " + e.getMessage());

        }

        @Override
        public void onOutputFormatChanged(@NonNull MediaCodec codec, @NonNull MediaFormat format) {

        }
    };

    public void onAudioAvaliable() {
        synchronized (mLock) {
            audioCount++;
            mLock.notifyAll();
        }
    }


    private class AudioRecoderThread extends Thread {

        AudioRecord mAudioRecod;
        int mBufferSize;
        byte[] buffer = new byte[1024];

        AudioRecoderThread() {
            mBufferSize = AudioRecord.getMinBufferSize(SAMPLE_RATE, CHANNEL_COUNT, ENCODING_FORMAT);
            mAudioRecod = new AudioRecord(MediaRecorder.AudioSource.MIC,
                    SAMPLE_RATE, CHANNEL_COUNT, ENCODING_FORMAT, mBufferSize);

        }

        public void stopRecording() {
            mAudioRecod.stop();
            mAudioRecod.release();
            mAudioRecod = null;
        }

        public void startRecoding() {
            mAudioRecod.startRecording();
            this.start();
        }

        @Override
        public void run() {
            while (isRecoding) {
                AudioFrame audioFrame = new AudioFrame();
                audioFrame.data = ByteBuffer.allocateDirect(1024);
                int readSize = mAudioRecod.read(audioFrame.data, 1024);
                if (readSize < 0) {
                    Log.d(TAG, "run: " + readSize);
                    continue;
                }
                audioFrame.data.position(readSize);
                audioFrame.data.flip();
                long pts = getPTS();
                if (pts != -1) {
                    audioFrame.pts = pts;
                    audioFrame.size = readSize;
                    Log.d(TAG, "run: add");
                    synchronized (mLock){
                        audioFrames.add(audioFrame);
                    }
                    onAudioAvaliable();
                }

            }

            AudioFrame audioFrame = new AudioFrame();
            audioFrame.data = null;
            long pts = getPTS();
            audioFrame.pts = pts;
            audioFrame.size = 0;
            audioFrames.add(audioFrame);
            onAudioAvaliable();

        }
    }

    private long prePTS = 0;

    public long getPTS() {
        long result = System.nanoTime() / 1000L;
        if (result < prePTS) {
            return -1;
        }
        prePTS = result;
        return result;
    }

    private class AudioFrame {
        ByteBuffer data;
        int size;
        long pts;
    }
}
