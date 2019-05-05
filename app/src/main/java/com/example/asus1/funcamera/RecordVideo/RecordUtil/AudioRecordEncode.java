package com.example.asus1.funcamera.RecordVideo.RecordUtil;

import android.media.AudioFormat;
import android.media.AudioRecord;
import android.media.MediaCodec;
import android.media.MediaCodecInfo;
import android.media.MediaFormat;
import android.media.MediaRecorder;
import android.util.Log;

import com.example.asus1.funcamera.RecordVideo.Views.Photo;

import java.io.IOException;
import java.net.PortUnreachableException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;

public class AudioRecordEncode implements Runnable {

    public static final int BIT_RATE = 64000;
    public static final int SAMPLES_PER_FRAME = 1024; //ACC，bytes/frame/channel
    public static final int FRAME_PER_BUFFER = 25; //ACC,frame/buffer/sec
    public static final String MIME_TYPE = "audio/mp4a-latm";
    // 采样率
    // 44100是目前的标准，但是某些设备仍然支持22050，16000，11025
    // 采样频率一般共分为22.05KHz、44.1KHz、48KHz三个等级
    public final static int AUDIO_SAMPLE_RATE = 44100;
    // 音频通道 单声道
    public final static int AUDIO_CHANNEL = AudioFormat.CHANNEL_IN_MONO;
    // 音频格式：PCM编码
    public final static int AUDIO_ENCODING = AudioFormat.ENCODING_PCM_16BIT;

    private boolean mIsCapturing = false;
    private boolean mEOS = false;
    private boolean mRuestStop = false;

    private int mRuqestDrain = 0;
    private Object mSyn  = new Object();

    public AudioThread mAudioThread;

    private MediaCodec mCodec;
    private VideoMediaMuxer mMuxer;
    private boolean mMuxerStart = false;
    private int mTrackIndex;

    private MediaCodec.BufferInfo mBfferInfo;

    private static final String TAG = "AudioRecordEncode";

    public AudioRecordEncode(VideoMediaMuxer muxer) {

        mBfferInfo = new MediaCodec.BufferInfo();
        mMuxer = muxer;

        synchronized (mSyn){
            new Thread(this).start();
            try {
                mSyn.wait();
            }catch (InterruptedException e){
                e.printStackTrace();
            }
        }
    }

    public void onPerpare(){
        mEOS = false;
        try {
            MediaFormat audioFormat = MediaFormat.createAudioFormat(MIME_TYPE,
                    AUDIO_SAMPLE_RATE,AUDIO_CHANNEL);
            audioFormat.setInteger(MediaFormat.KEY_AAC_PROFILE, MediaCodecInfo.CodecProfileLevel.AACObjectLC);
            audioFormat.setInteger(MediaFormat.KEY_CHANNEL_MASK, AudioFormat.CHANNEL_IN_MONO);
            audioFormat.setInteger(MediaFormat.KEY_BIT_RATE, BIT_RATE);
            audioFormat.setInteger(MediaFormat.KEY_CHANNEL_COUNT, 1);
            mCodec = MediaCodec.createEncoderByType(MIME_TYPE);
            mCodec.configure(audioFormat,null,null,MediaCodec.CONFIGURE_FLAG_ENCODE);
            mCodec.start();

        }catch (IOException e){
            e.printStackTrace();
        }

    }

    public void startRecording(int input){
        synchronized (mSyn){
            mIsCapturing = true;
            mRuestStop = false;
            mAudioThread = new AudioThread();
            mAudioThread.setAudioInput(input);
            mAudioThread.start();
            mSyn.notifyAll();

        }

    }

    @Override
    public void run() {
        synchronized (mSyn){
            mRuestStop = false;
            mRuqestDrain = 0;
            mSyn.notifyAll();
        }
        boolean localRuqestDrain;
        boolean localRequestStop;
        boolean IsRunning = true;
        while (IsRunning){

            synchronized (mSyn){
                localRequestStop = mRuestStop;
                localRuqestDrain = (mRuqestDrain>0);
                if(localRuqestDrain){
                    mRuqestDrain --;
                }
            }

            if(localRequestStop){
                drain();
                encode(null,0,getPTSUs());
                mEOS = true;
                drain();
                release();
                break;
            }

            if(localRuqestDrain){

                drain();
            }else {
                synchronized (mSyn){

                    try {
                        Log.d(TAG, "run: wait");
                        mSyn.wait();
                    }catch (InterruptedException e){
                        break;
                    }
                }
            }
        }

        synchronized (mSyn){
            mRuestStop = true;
            mIsCapturing= false;
        }
    }

    private void drain(){
        int count = 0;
        LOOP:while (mIsCapturing){
            int encodeStatue = mCodec.
                    dequeueOutputBuffer(mBfferInfo,10000);
            if(encodeStatue == MediaCodec.INFO_TRY_AGAIN_LATER){
                if(!mEOS){
                    if(++count >5){
                        break LOOP;
                    }
                }
            }else if(encodeStatue == MediaCodec.INFO_OUTPUT_FORMAT_CHANGED){
                Log.d(TAG, "drain: "+encodeStatue);

                MediaFormat format = mCodec.getOutputFormat();
                //mTrackIndex = mMuxer.addTrack(format);
                mMuxerStart = true;
                if(!mMuxer.start()){
                    synchronized (mMuxer){
                        while (!mMuxer.isStarted()){
                            try {
                                mMuxer.wait(100);
                            }catch (InterruptedException e){
                                break LOOP;
                            }
                        }
                    }
                }

            }else if(encodeStatue <0){
                Log.d(TAG, "drain:unexpected result " +
                        "from encoder#dequeueOutputBuffer: " + encodeStatue);
            }else {
                ByteBuffer byteBuffer = mCodec.getOutputBuffer(encodeStatue);
                mBfferInfo.presentationTimeUs = getPTSUs();
                prevOutputPTSUs = mBfferInfo.presentationTimeUs;
                mMuxer.writeSampleData(1,byteBuffer,mBfferInfo);
                mCodec.releaseOutputBuffer(encodeStatue,false);
                if ((mBfferInfo.flags & MediaCodec.BUFFER_FLAG_END_OF_STREAM) != 0) {
                    // when EOS come.
                    Log.d(TAG, "drain: EOS");
                    mIsCapturing = false;
                    break;      // out of while
                }
            }

        }
    }

    private void release(){
        if(mCodec != null){
            mCodec.stop();
            mCodec.release();
            mCodec = null;
        }

        if (mMuxerStart) {
            if (mMuxer != null) {
                try {
                    mMuxer.stop();
                } catch (final Exception e) {
                    Log.e(TAG, "failed stopping muxer", e);
                }
            }
        }
        mBfferInfo = null;
    }

    public void onStopRecording(){
        synchronized (mSyn){
            if(!mIsCapturing||mRuestStop){
                return;
            }
            mIsCapturing = false;
            mRuestStop = true;
            mSyn.notifyAll();
        }

    }

    private void onFrameAvaliable(){
        synchronized (mSyn){
            if(!mIsCapturing || mRuestStop ){
                return;
            }
            mRuqestDrain++;
            mSyn.notifyAll();
        }
    }

    public class AudioThread extends Thread{

        //音频源
        private  int AUDIO_INPUT = MediaRecorder.AudioSource.MIC;
        // 录音对象
        private AudioRecord mAudioRecord;

        public void setAudioInput(int input){
            AUDIO_INPUT = input;
        }

        @Override
        public void run() {
           // android.os.Process.setThreadPriority(android.os.Process.THREAD_PRIORITY_URGENT_AUDIO);

            createAudio();
            if(mAudioRecord.getState() != AudioRecord.STATE_INITIALIZED){
                mAudioRecord = null;
            }

            if(mAudioRecord!=null){
                try {
                    if(mIsCapturing){

                        ByteBuffer buffer = ByteBuffer.allocateDirect(SAMPLES_PER_FRAME);
                        mAudioRecord.startRecording();
                        int readSize;
                        try {
                            for(;mIsCapturing && !mRuestStop && !mEOS;){
                                buffer.clear();
                                readSize =  mAudioRecord.read(buffer,SAMPLES_PER_FRAME);

                                if(readSize>0){
                                    buffer.position(readSize);
                                    buffer.flip();
                                    encode(buffer,readSize,getPTSUs());
                                    onFrameAvaliable();
                                }
                            }
                            onFrameAvaliable();
                        }finally {
                            mAudioRecord.stop();
                        }

                    }
                }finally {
                    mAudioRecord.release();
                    mAudioRecord = null;
                }

            }

        }

        private void createAudio(){
            //获得缓冲区字节大小
            int buffersize = AudioRecord.getMinBufferSize(AUDIO_SAMPLE_RATE,
                    AUDIO_CHANNEL,AUDIO_ENCODING);
            mAudioRecord = new AudioRecord(AUDIO_INPUT,AUDIO_SAMPLE_RATE,
                    AUDIO_CHANNEL,AUDIO_ENCODING,buffersize);
        }
    }

    private void encode(ByteBuffer byteBuffer,int length,long presentationTimeUs){
        if(!mIsCapturing) return;
        ByteBuffer inputBuffer ;
        int index;
        while (mIsCapturing){
            index = mCodec.dequeueInputBuffer(presentationTimeUs);
            if(index>0){
                inputBuffer = mCodec.getInputBuffer(index);
                inputBuffer.clear();
                if(byteBuffer!=null){
                    inputBuffer.put(byteBuffer);
                }

                if(length<=0){
                    mEOS = true;
                    mCodec.queueInputBuffer(index,0,0,
                            presentationTimeUs,MediaCodec.BUFFER_FLAG_END_OF_STREAM);
                    break;
                }else {
                    mCodec.queueInputBuffer(index,0,length,presentationTimeUs,0);
                }
                break;
            }else {

            }
        }
    }

    private long prevOutputPTSUs = 0;
    /**
     * get next encoding presentationTimeUs
     * @return
     */
    protected long getPTSUs() {
        long result = System.nanoTime() / 1000L;
        // presentationTimeUs should be monotonic
        // otherwise muxer fail to write
        if (result < prevOutputPTSUs)
            result = (prevOutputPTSUs - result) + result;
        return result;
    }
}
