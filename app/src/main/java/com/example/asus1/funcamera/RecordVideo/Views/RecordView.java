package com.example.asus1.funcamera.RecordVideo.Views;

import android.content.Context;
import android.graphics.SurfaceTexture;
import android.opengl.EGL14;
import android.opengl.GLES11Ext;
import android.opengl.GLES20;
import android.opengl.GLSurfaceView;
import android.opengl.Matrix;
import android.util.AttributeSet;

import com.example.asus1.funcamera.RecordVideo.RecordUtil.VideoRecordEncode;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

public class RecordView extends GLSurfaceView {

    public RecordRender mRender;
    public int mTextId;
    private  Context mContext;
    private float[] mStMatrix = new float[16];
    private boolean mRequestUpdateTex = false;
    private boolean mFlip = false;

    private static final String TAG = "RecordView";

    public RecordView(Context context) {
        this(context,null);
    }

    public RecordView(Context context, AttributeSet attrs) {
        super(context, attrs);
        mContext = context;
        setEGLContextClientVersion(2);
        mRender = new RecordRender();
        Matrix.setIdentityM(mStMatrix,0);
        setRenderer(mRender);
        setRenderMode(GLSurfaceView.RENDERMODE_CONTINUOUSLY);
    }

    public void setVideoEndoer(final VideoRecordEncode endoer){
        queueEvent(new Runnable() {
            @Override
            public void run() {
                synchronized (mRender){
                    endoer.setEGLContext(EGL14.eglGetCurrentContext(),mTextId);
                    mRender.mEncode = endoer;
                }
            }
        });
    }


    private class RecordRender implements Renderer,SurfaceTexture.OnFrameAvailableListener{

        private Photo mPhoto;
        private SurfaceTexture mSurfaceTexture;
        private CameraHelper mCamera;
        private float[] mProjMatrix = new float[16];
        private VideoRecordEncode mEncode;

        @Override
        public void onSurfaceCreated(GL10 gl, EGLConfig config) {

            GLES20.glClearColor(1,1,1,1);
            GLES20.glEnable(GLES20.GL_DEPTH_TEST);
            mPhoto = new Photo();
            initTextureId();//构建纹理id
            mSurfaceTexture = new SurfaceTexture(mTextId);//构建用于预览的surfaceTexture
            mSurfaceTexture.setOnFrameAvailableListener(this);
            mCamera = CameraHelper.createCamer(mContext,mSurfaceTexture);//开启预览

        }

        @Override
        public void onSurfaceChanged(GL10 gl, int width, int height) {
            GLES20.glViewport(0,0,width,height);

        }

        @Override
        public void onDrawFrame(GL10 gl) {
            GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT|GLES20.GL_DEPTH_BUFFER_BIT);
            if(mRequestUpdateTex){
                mRequestUpdateTex = false;
                //得到最新的图像
                mSurfaceTexture.updateTexImage();
                //得到图像的纹理矩阵
                mSurfaceTexture.getTransformMatrix(mStMatrix);
                //绘制图像
            }

            mPhoto.draw(mTextId,mStMatrix);
            mFlip = !mFlip;
            if(mFlip){
                synchronized (this){
                    if(mEncode!=null){
                        mEncode.onFrameAvaliable(mStMatrix);
                    }

                }

            }
        }

        private void initTextureId(){

            int[] texutes = new int[1];
            GLES20.glGenTextures(1,texutes,0);
            mTextId = texutes[0];
            GLES20.glBindTexture(GLES11Ext.GL_TEXTURE_EXTERNAL_OES,mTextId);

            GLES20.glTexParameterf(GLES11Ext.GL_TEXTURE_EXTERNAL_OES,
                    GLES20.GL_TEXTURE_MIN_FILTER,GLES20.GL_NEAREST);//设置MIN 采样方式
            GLES20.glTexParameterf(GLES11Ext.GL_TEXTURE_EXTERNAL_OES,
                    GLES20.GL_TEXTURE_MAG_FILTER,GLES20.GL_LINEAR);//设置MAG采样方式
            GLES20.glTexParameterf(GLES11Ext.GL_TEXTURE_EXTERNAL_OES,
                    GLES20.GL_TEXTURE_WRAP_S,GLES20.GL_CLAMP_TO_EDGE);//设置S轴拉伸方式
            GLES20.glTexParameterf(GLES11Ext.GL_TEXTURE_EXTERNAL_OES,
                    GLES20.GL_TEXTURE_WRAP_T,GLES20.GL_CLAMP_TO_EDGE);//设置T轴拉伸方式

        }


        @Override
        public void onFrameAvailable(SurfaceTexture surfaceTexture) {
            mRequestUpdateTex = true;

        }
        
    }
}
