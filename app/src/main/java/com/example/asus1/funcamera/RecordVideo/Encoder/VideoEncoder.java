package com.example.asus1.funcamera.RecordVideo.Encoder;

import android.content.pm.PackageManager;
import android.media.MediaCodec;
import android.media.MediaCodecInfo;
import android.media.MediaCodecList;
import android.media.MediaFormat;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.Surface;

import com.example.asus1.funcamera.RecordVideo.RecordUtil.VideoMediaMuxer;

import java.io.IOException;
import java.nio.ByteBuffer;

public class VideoEncoder implements Runnable{

    private MediaCodec mVideoCodec;
    private MediaFormat mVideoFormat;

    public static String VIDEO_MIMW = "video/avc";
    private static final String TAG = "VideoEncoder";
    private static final int BIT_RATE = 720*1280*3;

    private Surface mInputSuface;
    private VideoMuxer mMuxer;

    public VideoEncoder(VideoMuxer muxer){
        mMuxer = muxer;

        try {
            mVideoCodec = MediaCodec.createByCodecName(VIDEO_MIMW);
            mVideoFormat = MediaFormat.createVideoFormat(VIDEO_MIMW,720,1280);
            mVideoFormat.setInteger(MediaFormat.KEY_BIT_RATE,BIT_RATE);
            mVideoFormat.setInteger(MediaFormat.KEY_FRAME_RATE,25);
            mVideoFormat.setInteger(MediaFormat.KEY_COLOR_FORMAT,
                    MediaCodecInfo.CodecCapabilities.COLOR_FormatSurface);
            MediaCodecInfo.CodecCapabilities codecCapabilities =
                    mVideoCodec.getCodecInfo().getCapabilitiesForType(VIDEO_MIMW);
            MediaCodecInfo.CodecProfileLevel[] profileLevels =
                    codecCapabilities.profileLevels;
            MediaCodecInfo.CodecProfileLevel profile = null;
            for(MediaCodecInfo.CodecProfileLevel profileLevel:profileLevels){
                if(profileLevel.profile == MediaCodecInfo.CodecProfileLevel.AVCProfileMain){
                    profile = profileLevel;
                    break;
                }
            }
            if(profile == null){
                profile = profileLevels[0];
            }

            mVideoFormat.setInteger(MediaFormat.KEY_PROFILE,profile.profile);
            mVideoFormat.setInteger(MediaFormat.KEY_LEVEL,profile.level);

        mVideoCodec.configure(mVideoFormat,null,null,MediaCodec.CONFIGURE_FLAG_ENCODE);
        mInputSuface = mVideoCodec.createInputSurface();
        mVideoCodec.setCallback(mCallBack);

        }catch (IOException e){
            e.printStackTrace();
        }


    }

    public Surface getInputSurface(){
        return mInputSuface;
    }

    public void startRecoding(){

    }

    @Override
    public void run() {
        mVideoCodec.start();
    }

    private MediaCodec.Callback mCallBack = new MediaCodec.Callback() {
        @Override
        public void onInputBufferAvailable(@NonNull MediaCodec codec, int index) {

        }

        @Override
        public void onOutputBufferAvailable(@NonNull MediaCodec codec, int index, @NonNull MediaCodec.BufferInfo info) {
            ByteBuffer byteBuffer = codec.getOutputBuffer(index);
            mMuxer.addData(0,byteBuffer,info.presentationTimeUs);
        }

        @Override
        public void onError(@NonNull MediaCodec codec, @NonNull MediaCodec.CodecException e) {

        }

        @Override
        public void onOutputFormatChanged(@NonNull MediaCodec codec, @NonNull MediaFormat format) {

        }
    };
}
