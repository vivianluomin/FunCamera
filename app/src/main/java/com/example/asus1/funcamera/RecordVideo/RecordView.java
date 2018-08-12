package com.example.asus1.funcamera.RecordVideo;

import android.content.Context;
import android.opengl.GLSurfaceView;
import android.util.AttributeSet;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

public class RecordView extends GLSurfaceView {

    private RecordRender mRender;

    public RecordView(Context context) {
        this(context,null);
    }

    public RecordView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    private class RecordRender implements Renderer{
        @Override
        public void onSurfaceCreated(GL10 gl, EGLConfig config) {

        }

        @Override
        public void onSurfaceChanged(GL10 gl, int width, int height) {

        }

        @Override
        public void onDrawFrame(GL10 gl) {

        }
    }
}
