package com.example.asus1.funcamera.RecordVideo.EGLUtil;

import android.opengl.EGLContext;
import android.opengl.Matrix;
import android.util.Log;
import android.view.Surface;

import com.example.asus1.funcamera.RecordVideo.Views.Photo;

public class RenderHandler implements Runnable {

    private boolean mRequestEGLContext = false;
    private int mRequestDraw = 0;
    private EGLHelper mEGLHelper;
    private EGLContext mShareContext;
    private int mTextId;
    private Surface mLinkSurface;
    private Object mSyn = new Object();
    private float[] mStMatrix = new float[16];
    private boolean mRequestRelease = false;

    private static final String TAG = "RenderHandler";

    public static RenderHandler createRenderHandler(){
        RenderHandler handler = new RenderHandler();
        new Thread(handler).start();
        synchronized (handler.mSyn){
            try {
                handler.mSyn.wait();
            }catch (InterruptedException e){
                return null;
            }
        }

        return handler;
    }

    private void prepare(){
        mEGLHelper = new EGLHelper(mShareContext,mLinkSurface,mTextId);
        mSyn.notifyAll();
    }

    public void setEGLContext(EGLContext context,Surface surface,int textId){
        mShareContext = context;
        mLinkSurface = surface;
        mTextId = textId;
        Matrix.setIdentityM(mStMatrix,0);
        mRequestEGLContext = true;

    }

    public void draw(int textId,float[] stMatrix){
        if(mRequestRelease) return;
        synchronized (mSyn){
            mTextId = textId;
           // System.arraycopy(stMatrix,0,mStMatrix,0,16);
            mStMatrix = stMatrix;
            mRequestDraw ++;
            mSyn.notifyAll();
        }

    }

    public void stop(){
        synchronized (mSyn){
            if(mRequestRelease) return;
            mRequestRelease = true;
        }

    }

    @Override
    public void run() {

        synchronized (mSyn){
            mRequestRelease= mRequestEGLContext = false;
            mRequestDraw = 0;
            mSyn.notifyAll();
        }
        boolean localRequestDraw = false;
        for(;;){
            synchronized (mSyn){
                if(mRequestRelease) break;
                if(mRequestEGLContext){
                    mRequestEGLContext = false;
                    prepare();
                }
            }

            localRequestDraw = mRequestDraw>0 ;
            if(localRequestDraw){
                if(mTextId>=0){
                    mRequestDraw --;
                    mEGLHelper.makeCurrent();
                    mEGLHelper.render(mTextId,mStMatrix);
                }

            }else {
                synchronized (mSyn){
                    try {
                        mSyn.wait();
                    }catch (InterruptedException e){
                        break;
                    }
                }

            }
        }
    }
}
