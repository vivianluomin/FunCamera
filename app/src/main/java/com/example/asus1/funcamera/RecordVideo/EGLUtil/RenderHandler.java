package com.example.asus1.funcamera.RecordVideo.EGLUtil;

import android.opengl.EGLContext;
import android.opengl.Matrix;
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

    public static RenderHandler createRenderHandler(){
        RenderHandler handler = new RenderHandler();
        new Thread(handler).start();
        try {
            handler.mSyn.wait();
        }catch (InterruptedException e){
            return null;
        }
        return handler;
    }

    private void prepare(){
        mEGLHelper = new EGLHelper(mShareContext,mLinkSurface,mTextId);
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

        mTextId = textId;
        System.arraycopy(stMatrix,0,mStMatrix,0,16);
        mRequestDraw ++;
        mSyn.notifyAll();

    }


    @Override
    public void run() {
        synchronized (mSyn){
            mRequestRelease= mRequestEGLContext = false;
            mRequestDraw = 0;
        }
        boolean localRequestDraw = false;
        for(;;){
            if(mRequestRelease) break;
            if(mRequestEGLContext){
                mRequestEGLContext = false;
                prepare();
                mSyn.notifyAll();
            }
            localRequestDraw = mRequestDraw>0 ;
            if(localRequestDraw){
                mRequestDraw --;
                mEGLHelper.makeCurrent();
                mEGLHelper.render(mTextId,mStMatrix);
            }else {
                try {
                    mSyn.wait();
                }catch (InterruptedException e){
                    break;
                }
            }
        }
    }
}
