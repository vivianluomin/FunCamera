package com.example.asus1.funcamera.RecordVideo;

import android.content.Context;
import android.opengl.EGL14;
import android.opengl.EGLConfig;
import android.opengl.EGLContext;
import android.opengl.EGLDisplay;
import android.opengl.EGLSurface;

public class EGLHelper {

    private EGLDisplay mDisplay; //对实际显示设备的抽象
    private Context mContext;
    private EGLContext mEGLContext;

    private EGLSurface mSurface;

    public EGLHelper(Context context){

        mContext = context;
        init();

    }

    private void init(){
        if ( (mDisplay = EGL14.eglGetDisplay(EGL14.EGL_DEFAULT_DISPLAY)) == EGL14.EGL_NO_DISPLAY) {
            throw new RuntimeException("unable to get EGL14 display");
        }

        int[] version = new int[2];

        if (!EGL14.eglInitialize(mDisplay, version,0, version,1)) {
            throw new RuntimeException("unable to initialize EGL14");
        }

        //配置选项
        int[] configAttribs = {
                EGL14.EGL_BUFFER_SIZE,32,
                EGL14.EGL_ALPHA_SIZE,8,
                EGL14.EGL_BLUE_SIZE,8,
                EGL14.EGL_GREEN_SIZE,8,
                EGL14.EGL_RED_SIZE,8,
                EGL14.EGL_RENDERABLE_TYPE,EGL14.EGL_OPENGL_ES2_BIT,
                EGL14.EGL_SURFACE_TYPE,EGL14.EGL_WINDOW_BIT,
                EGL14.EGL_NONE
        };

        EGLConfig[] config = new EGLConfig[1];
        int[] numComfig = new int[1];
        EGL14.eglChooseConfig(mDisplay,configAttribs,0,config,0,
                config.length,numComfig,0);

        //创建OpenGL上下文
        int[] attrib_list = {
                EGL14.EGL_CONTEXT_CLIENT_VERSION, 2,
                EGL14.EGL_NONE
        };
        mEGLContext = EGL14.eglCreateContext(mDisplay, config[0], EGL14.EGL_NO_CONTEXT,
                attrib_list, 0);

        mSurface =EGL14.EGL_NO_SURFACE;
        int[] surfaceAttribs = {
                EGL14.EGL_NONE
        };
        mSurface = EGL14.eglCreateWindowSurface(mDisplay,config[0],mSurface,surfaceAttribs,0);

        EGL14.eglMakeCurrent(mDisplay,mSurface,mSurface,mEGLContext);


    }



}
