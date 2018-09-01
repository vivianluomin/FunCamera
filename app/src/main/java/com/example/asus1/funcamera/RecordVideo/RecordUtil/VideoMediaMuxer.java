package com.example.asus1.funcamera.RecordVideo.RecordUtil;

import android.media.MediaCodec;
import android.media.MediaFormat;
import android.media.MediaMuxer;
import android.os.Environment;
import android.util.Log;

import com.example.asus1.funcamera.RecordVideo.Controller.ModelController;
import com.example.asus1.funcamera.RecordVideo.Controller.RecordPersenter;

import java.io.File;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.text.SimpleDateFormat;
import java.util.GregorianCalendar;
import java.util.Locale;

public class VideoMediaMuxer implements ModelController {

    private static final String DIR_NAME = "FunCamera";
    private static final SimpleDateFormat mDateTimeForamt =
            new SimpleDateFormat("yyyy-MM-dd-HH-mm-ss", Locale.CHINA);
    private static String EXT= ".mp4";

    private String mOutputPath;
    private MediaMuxer mMediaMuxer;
    private VideoRecordEncode mVideoEncode;
    private AudioRecordEncode mAudioEndoe;
    private boolean mIsStart =  false;
    private int mEncodeCount ;
    private  int mStartEncodeCount ;
    private static final String TAG = "VideoMediaMuxer";
    private RecordPersenter mPresenter = RecordPersenter.getPresenterInstantce();

    public VideoMediaMuxer()throws IOException{

        mOutputPath = getCaptureFile(Environment.DIRECTORY_MOVIES,EXT).toString();
        mMediaMuxer = new MediaMuxer(mOutputPath,MediaMuxer.OutputFormat.MUXER_OUTPUT_MPEG_4);
        mEncodeCount = 0;
        mStartEncodeCount = 0;
        mPresenter.setModeController(this);
    }

    public void addEncode(VideoRecordEncode videoRecordEncode,AudioRecordEncode audioRecordEncode){
        mVideoEncode = videoRecordEncode;
        mAudioEndoe = audioRecordEncode;
        mEncodeCount = 2;
    }

    public int addTrack(MediaFormat format){
       int track= mMediaMuxer.addTrack(format);
       return track;
    }

    public void preprare(){
        //MediaCodec初始化
        mVideoEncode.prepare();
        mAudioEndoe.onPerpare();
    }

    @Override
    public void startRecording(){
            //开始录制
            mVideoEncode = new
                    VideoRecordEncode(this,lisnter,1280, 720);
            mAudioEndoe = new AudioRecordEncode(this);
            //判断有几个MediaCodec
            this.addEncode(mVideoEncode,mAudioEndoe);
            this.preprare();
        mVideoEncode.startRecord();
        mAudioEndoe.startRecording();
    }

    private  onFramPrepareLisnter lisnter = new onFramPrepareLisnter() {
        @Override
        public void onPrepare(VideoRecordEncode encode) {
           mPresenter.setVideoEncode(encode);
        }
    };

    @Override
    public void stopRecording(){
        mVideoEncode.onStopRecording();
        mAudioEndoe.onStopRecording();
    }

    synchronized public boolean start(){
        //当两个MediaCodec都准备好了，才可以写入文件
        mStartEncodeCount++;
        Log.d(TAG, "start: "+mStartEncodeCount);
        if(mEncodeCount>0&&(mStartEncodeCount == mEncodeCount)){
            mMediaMuxer.start();
            mIsStart = true;
            notifyAll();
            return mIsStart;
        }
        return mIsStart;
    }

    public synchronized boolean isStarted() {
        return mIsStart;
    }

   synchronized public void stop(){
         mStartEncodeCount -- ;
         if(mEncodeCount>0&&mStartEncodeCount<=0){
             mMediaMuxer.stop();
             mMediaMuxer.release();
             mIsStart = false;
         }
    }

    public void writeSampleData(int mediaTrack, ByteBuffer byteBuffer, MediaCodec.BufferInfo bufferInfo){
        //写入文件
        if(mStartEncodeCount>0){
            mMediaMuxer.writeSampleData(mediaTrack,byteBuffer,bufferInfo);
        }

    }

    public static final File getCaptureFile(final String type, final String ext) {
        final File dir = new File(Environment.getExternalStoragePublicDirectory(type), DIR_NAME);
        dir.mkdirs();
        if (dir.canWrite()) {
            return new File(dir, getDateTimeString() + ext);
        }
        return null;
    }

    private static final String getDateTimeString() {
        final GregorianCalendar now = new GregorianCalendar();
        return mDateTimeForamt.format(now.getTime());
    }

}
