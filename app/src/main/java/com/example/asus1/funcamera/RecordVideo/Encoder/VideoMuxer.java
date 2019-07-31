package com.example.asus1.funcamera.RecordVideo.Encoder;

import com.example.asus1.funcamera.RecordVideo.RecordUtil.FFmpegMuxer;

import java.nio.ByteBuffer;

public class VideoMuxer {

    private FFmpegMuxer mMuxer;
    private AudioEncoder mAudioEncoder;
    private VideoEncoder mVideoEncoder;

    public VideoMuxer(){
        mMuxer = new FFmpegMuxer();
        mAudioEncoder = new AudioEncoder(this);
        mVideoEncoder = new VideoEncoder(this);
    }

    public void startRecoding(){

    }
    public void stopRecoding(){

    }
    public void addData(int mediaTrack, ByteBuffer byteBuffer,long pts){
        mMuxer.writeData(mediaTrack,byteBuffer,pts);
    }


}
