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
    private boolean mSetContext = false;

    private static final String TAG = "RenderHandler";

    public static RenderHandler createRenderHandler(){
        RenderHandler handler = new RenderHandler();
        return handler;
    }

    private void prepare(){
        mEGLHelper = new EGLHelper(mShareContext,mLinkSurface,mTextId);
    }

    public void setEGLContext(EGLContext context,Surface surface,int textId){
        Log.d(TAG, "setEGLContext: ");
        mShareContext = context;
        mLinkSurface = surface;
        mTextId = textId;
        Matrix.setIdentityM(mStMatrix,0);
        mRequestEGLContext = true;
        mSetContext = true;

    }

    public void draw(int textId,float[] stMatrix){
        if(mRequestRelease) return;
        synchronized (mSyn){
            mTextId = textId;
            Log.d(TAG, "draw: ");
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
        boolean localRequestDraw = false;
        for(;;){
            if(!mSetContext){
                continue;
            }
            if (mRequestRelease) break;
            if (mRequestEGLContext) {
                mRequestEGLContext = false;
                prepare();
            }

            localRequestDraw = mRequestDraw>0 ;
            if(localRequestDraw){
                if(mTextId>=0){
                    mRequestDraw --;
                    mEGLHelper.makeCurrent();
                    mEGLHelper.render(mTextId,mStMatrix);
                    Log.d(TAG, "render: end");
                }

            }else {
                synchronized (mSyn){
                    try {
                        if(mRequestDraw<=0){
                            Log.d(TAG, "wait: ");
                            mSyn.wait();
                        }
                    }catch (InterruptedException e){
                        e.printStackTrace();
                    }
                }

            }
        }
    }
}
