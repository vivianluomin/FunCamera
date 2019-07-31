package com.example.asus1.funcamera.RecordVideo.RecordUtil;

import android.media.MediaCodec;
import android.os.Environment;

import java.nio.ByteBuffer;

public class FFmpegMuxer {

    static {

        System.loadLibrary("FFmpegMuxer");
    }

    private long mHandler;
    private String mPath;

    public FFmpegMuxer(){
        init();
    }

    private void init(){
        mPath  = VideoMediaMuxer.getCaptureFile(Environment.DIRECTORY_MOVIES,VideoMediaMuxer.EXT).toString();
        mHandler = native_init(mPath);
    }

    public void write(int mediaTrack, ByteBuffer data, MediaCodec.BufferInfo info){
        writeData(mHandler,mediaTrack,data,info);
    }

    public void stop(){

        native_stop(mHandler);
    }

    public void writeData(int mediaTrack, ByteBuffer data,long pts){

    }

    private native long native_init(String path);

    private native void writeData(long handler,int mediaTrack, ByteBuffer data, MediaCodec.BufferInfo info);

    private native void native_stop(long handler);

}
