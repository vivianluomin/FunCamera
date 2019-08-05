package com.example.asus1.funcamera.RecordVideo.Encoder;

import android.content.pm.PackageManager;
import android.media.MediaCodec;
import android.media.MediaCodecInfo;
import android.media.MediaCodecList;
import android.media.MediaFormat;
import android.opengl.EGLContext;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Looper;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.Surface;

import com.example.asus1.funcamera.RecordVideo.EGLUtil.RenderHandler;
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

    private EGLContext mShare_Context;
    private RenderHandler mHandler;
    private int mTexId;
    private boolean mEnOS = false;
    private int mRquestDraw = 0;

    private Object mLock = new Object();
    private HandlerThread mHandlerThread;
    private Handler mVideoHandler;

    public VideoEncoder(VideoMuxer muxer,EGLContext shareContext,int textId){
        mMuxer = muxer;
        mShare_Context = shareContext;
        mTexId = textId;
        mVideoHandler = createHandler(true);
        try {
            mVideoCodec = MediaCodec.createEncoderByType(VIDEO_MIMW);
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
            mVideoCodec.setCallback(mCallBack,mVideoHandler);
            mVideoCodec.configure(mVideoFormat,null,null,MediaCodec.CONFIGURE_FLAG_ENCODE);
         mInputSuface = mVideoCodec.createInputSurface();

        mHandler = RenderHandler.createRenderHandler();
        mHandler.setEGLContext(mShare_Context,mInputSuface,mTexId);

        }catch (IOException e){
            e.printStackTrace();
        }


    }

    public Surface getInputSurface(){
        return mInputSuface;
    }

    public void startRecoding(){
        new Thread(this).start();
    }

    private android.os.Handler createHandler(boolean async) {
        if (async) {
            try {
                if (mHandlerThread != null) {
                    mHandlerThread.quit();
                }
                mHandlerThread = new HandlerThread(TAG);
                mHandlerThread.start();
                return new android.os.Handler(mHandlerThread.getLooper());
            } catch (Exception e) {

            }
        }
        return new android.os.Handler(Looper.myLooper() != null ? Looper.myLooper() : Looper.getMainLooper());
    }

    @Override
    public void run() {
        new Thread(mHandler).start();
        mVideoCodec.start();
    }

    public void stopRecoding(){
        mVideoCodec.signalEndOfInputStream();
    }

    private void clear(){
        mVideoCodec.stop();
        mVideoCodec.release();
        mHandler.stop();
        mVideoCodec = null;
        mHandler = null;
    }

    public void onFrameAvaliable(int textId,float[] matixs){
        if(mHandler!=null){
            mHandler.draw(textId,matixs);
        }
    }

    private MediaCodec.Callback mCallBack = new MediaCodec.Callback() {
        @Override
        public void onInputBufferAvailable(@NonNull MediaCodec codec, int index) {
            
        }

        @Override
        public void onOutputBufferAvailable(@NonNull MediaCodec codec, int index, @NonNull MediaCodec.BufferInfo info) {
            Log.d(TAG, "onOutputBufferAvailable: ");
            ByteBuffer byteBuffer = codec.getOutputBuffer(index);
            info.presentationTimeUs = getPTS();
            mMuxer.addData(0,byteBuffer,info.presentationTimeUs,info.size,info.flags);
            codec.releaseOutputBuffer(index,false);
            if((info.flags & MediaCodec.BUFFER_FLAG_END_OF_STREAM) !=0){
                Log.d(TAG, "onOutputBufferAvailable: "+"end of stream");
                mEnOS = true;
                mMuxer.clear();
                clear();
            }
        }

        @Override
        public void onError(@NonNull MediaCodec codec, @NonNull MediaCodec.CodecException e) {

        }

        @Override
        public void onOutputFormatChanged(@NonNull MediaCodec codec, @NonNull MediaFormat format) {

        }
    };

    private long prePTS = 0;

    public long getPTS() {
        long result = System.nanoTime() / 1000L;
        if (result < prePTS) {
            return -1;
        }
        prePTS = result;
        return result;
    }
}
