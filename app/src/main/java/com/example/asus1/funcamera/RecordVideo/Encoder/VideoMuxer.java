package com.example.asus1.funcamera.RecordVideo.Encoder;

import android.opengl.EGLContext;
import android.util.Log;

import com.example.asus1.funcamera.RecordVideo.Controller.ModelController;
import com.example.asus1.funcamera.RecordVideo.Controller.RecordPersenter;
import com.example.asus1.funcamera.RecordVideo.RecordUtil.FFmpegMuxer;

import java.nio.ByteBuffer;


public class VideoMuxer implements ModelController {

    private FFmpegMuxer mMuxer;
    public AudioEncoder mAudioEncoder;
    public VideoEncoder mVideoEncoder;
    private EGLContext mShareContext;
    private int TextId;
    private int mEncoderCount = 0;
    private Object mLock = new Object();
    private RecordPersenter mPresenter = RecordPersenter.getPresenterInstantce();
    private static final String TAG = "VideoMuxer";

    public VideoMuxer(EGLContext shareContext, int textId){
        mShareContext = shareContext;
        mMuxer = new FFmpegMuxer();
        mAudioEncoder = new AudioEncoder(this);
        mVideoEncoder = new VideoEncoder(this,shareContext,textId);
        mEncoderCount = 2;
        mPresenter.setModeController(this);
    }


    @Override
    public void startRecording(int input) {
        mAudioEncoder.startRecoding();
        mVideoEncoder.startRecoding();
    }

    @Override
    public void stopRecording() {
        mAudioEncoder.stopRecoding();
        mVideoEncoder.stopRecoding();
    }

    @Override
    public String getVideoPath() {
        return null;
    }

    public void addData(int mediaTrack, ByteBuffer byteBuffer, long pts, int size, int flag){
        mMuxer.writeData(mediaTrack,byteBuffer,pts,size,flag);
    }

    public void clear(){
        synchronized (mLock){
            mEncoderCount --;
        }
        if(mEncoderCount == 0){
            Log.d(TAG, "clear: FFmpeg");
            mMuxer.stop();
        }
    }


}
