package com.example.asus1.funcamera.RecordVideo.Controller;

import android.opengl.EGLContext;

import com.example.asus1.funcamera.RecordVideo.Encoder.VideoEncoder;
import com.example.asus1.funcamera.RecordVideo.Encoder.VideoMuxer;
import com.example.asus1.funcamera.RecordVideo.RecordUtil.VideoMediaMuxer;
import com.example.asus1.funcamera.RecordVideo.RecordUtil.VideoRecordEncode;

import java.io.IOException;


public class RecordPersenter {
    private ViewController mViewController;
    private ModelController mModelController;
    private static RecordPersenter mPersenter = new RecordPersenter();

    private RecordPersenter(){

    }

    public static RecordPersenter getPresenterInstantce(){
        return mPersenter;
    }

    public void setViewController(ViewController viewController){
        mViewController = viewController;
    }

    public void setModeController(ModelController modeController){
        mModelController = modeController;
    }

    public void startRecoding(int input) {
        mModelController.startRecording(input);

    }

    public VideoEncoder startRecoding(EGLContext context, int textId){
        VideoMuxer muxer = new VideoMuxer(context,textId);
        mModelController.startRecording(0);
        return muxer.mVideoEncoder;
    }

    public void stopRecoding(){
        if(mModelController!=null)
        mModelController.stopRecording();
    }

    public String getVideoPath(){
        return mModelController.getVideoPath();
    }

    public void setVideoEncode(VideoRecordEncode encode){
        mViewController.setVideoEncode(encode);
    }
}
