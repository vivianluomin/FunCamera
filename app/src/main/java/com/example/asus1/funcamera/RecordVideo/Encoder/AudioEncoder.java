package com.example.asus1.funcamera.RecordVideo.Encoder;

import android.media.AudioFormat;
import android.media.AudioRecord;
import android.media.MediaCodec;
import android.media.MediaCodecInfo;
import android.media.MediaFormat;
import android.media.MediaRecorder;
import android.support.annotation.NonNull;
import android.support.v7.widget.StaggeredGridLayoutManager;

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

    private VideoMuxer mMuxer;

    public AudioEncoder(VideoMuxer muxer){
        mMuxer = muxer;
        try {
            mAudioCodec = MediaCodec.createByCodecName(AUDIO_MIME);
            mAudioFormat = MediaFormat.createAudioFormat(AUDIO_MIME,
                    44100, AudioFormat.CHANNEL_IN_STEREO);
            mAudioFormat.setInteger(MediaFormat.KEY_AAC_PROFILE,
                    MediaCodecInfo.CodecProfileLevel.AACObjectLC);
            mAudioFormat.setInteger(MediaFormat.KEY_CHANNEL_MASK, AudioFormat.CHANNEL_IN_MONO);
            mAudioFormat.setInteger(MediaFormat.KEY_BIT_RATE, 64000);
            mAudioFormat.setInteger(MediaFormat.KEY_CHANNEL_COUNT, 1);
            mAudioCodec.configure(mAudioFormat,null,null,MediaCodec.CONFIGURE_FLAG_ENCODE);
            mAudioCodec.setCallback(mCallBack);

        }catch (IOException e){

        }

    }

    @Override
    public void run() {
        mAudioCodec.start();
    }

    public void startRecoding(){

    }

    public void stopRecoding(){
        isRecoding = false;
        try {
            mLock.wait();
        }catch (InterruptedException e){
            e.printStackTrace();
        }

        mAudioCodec.stop();
        mAudioCodec.release();
    }

    private MediaCodec.Callback mCallBack = new MediaCodec.Callback() {
        @Override
        public void onInputBufferAvailable(@NonNull MediaCodec codec, int index) {

            if(audioFrames.size()>0){
                synchronized (mLock){
                   ByteBuffer byteBuffer =  codec.getInputBuffer(index);
                   byteBuffer.clear();
                   AudioFrame frame = audioFrames.removeFirst();
                   byteBuffer.put(frame.data);
                   if(audioFrames.size() <=0){
                       codec.queueInputBuffer(index,0,frame.size,frame.pts,MediaCodec.BUFFER_FLAG_END_OF_STREAM);
                       mEnd = true;
                       mLock.notifyAll();
                   }else {
                       codec.queueInputBuffer(index,0,frame.size,frame.pts,0);
                   }

                }
            }
        }

        @Override
        public void onOutputBufferAvailable(@NonNull MediaCodec codec, int index, @NonNull MediaCodec.BufferInfo info) {
            ByteBuffer byteBuffer = codec.getOutputBuffer(index);
            mMuxer.addData(1,byteBuffer,info.presentationTimeUs);

        }

        @Override
        public void onError(@NonNull MediaCodec codec, @NonNull MediaCodec.CodecException e) {

        }

        @Override
        public void onOutputFormatChanged(@NonNull MediaCodec codec, @NonNull MediaFormat format) {

        }
    };


    private class AudioRecoderThread extends Thread{

        AudioRecord mAudioRecod;
        int mBufferSize;
        byte[] buffer = new byte[1024];

        AudioRecoderThread(){
            mBufferSize = AudioRecord.getMinBufferSize(SAMPLE_RATE,CHANNEL_COUNT,ENCODING_FORMAT);
            mAudioRecod = new AudioRecord(MediaRecorder.AudioSource.MIC,
                    SAMPLE_RATE,CHANNEL_COUNT,ENCODING_FORMAT,mBufferSize);

        }

        public void stopRecording(){
            mAudioRecod.stop();
            mAudioRecod.release();
            mAudioRecod = null;
        }

        @Override
        public void run() {
            mAudioRecod.startRecording();
            while (isRecoding){
                AudioFrame audioFrame = new AudioFrame();
                audioFrame.data = new byte[1024];
                int readSize = mAudioRecod.read(audioFrame.data,0,1024);
                long pts = getPTS();
                if(pts!=-1){
                    audioFrame.pts = pts;
                    audioFrame.size = readSize;
                    audioFrames.add(audioFrame);
                }

            }

            AudioFrame audioFrame = new AudioFrame();
            audioFrame.data = new byte[1024];
            long pts = getPTS();
            audioFrame.pts = pts;
            audioFrame.size = 0;
            audioFrames.add(audioFrame);

        }
    }

    private long prePTS = 0;
    public long getPTS(){
        long result = System.nanoTime()/1000L;
        if(result<prePTS){
            return -1;
        }
        prePTS = result;
        return result;
    }

    private class AudioFrame{
        byte[] data;
        int size;
        long pts;
    }
}
