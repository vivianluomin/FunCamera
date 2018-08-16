package com.example.asus1.funcamera.RecordVideo.Controller;

import com.example.asus1.funcamera.RecordVideo.RecordUtil.VideoRecordEncode;

public interface ViewController {

    void startRecording();
    void stopRecording();
    void setVideoEncode(VideoRecordEncode encode);

}
