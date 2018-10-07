package com.example.asus1.funcamera.RecordVideo.Controller;

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
    public void startRecoding(int input){
        try {
            VideoMediaMuxer muxer = new VideoMediaMuxer();
            if(mModelController!=null)
                mModelController.startRecording(input);
        }catch (IOException e){
            e.printStackTrace();
        }
    }

    public void stopRecoding(){
        if(mModelController!=null)
        mModelController.stopRecording();
    }

    public void setVideoEncode(VideoRecordEncode encode){
        mViewController.setVideoEncode(encode);
    }
}
