package com.example.asus1.funcamera.RecordVideo.Views;

import android.content.Context;
import android.opengl.GLES20;

import com.example.asus1.funcamera.R;
import com.example.asus1.funcamera.Utils.Constant;
import com.example.asus1.funcamera.Utils.ShaderUtil;

import java.nio.ByteBuffer;
import java.nio.FloatBuffer;

public class BeautyPhoto extends Photo {

    private Context mContext;

    private int maPositionHandle;
    private int maTextureHandle;
    private int muMvpMatrixHandle;
    private int muTexMatrixHandle;

    private FloatBuffer mVertexBuffer;
    private FloatBuffer mTextureBuffer;
    private ByteBuffer mIndexbuffer;

    private int mSinglrStepOffsetHandle;
    private int mParamaHandle;

    private int mProgram;

    public BeautyPhoto() {
        super();
    }



    @Override
    protected void initFragmentData() {
        mProgram = ShaderUtil.loadProgram(mVertexShder, ShaderUtil.
                readShaderFromRawResource(R.raw.beauty, Constant.GLOABLE_CONTXT));
        maPositionHandle = GLES20.glGetAttribLocation(mProgram,"aPosition");
        maTextureHandle = GLES20.glGetAttribLocation(mProgram,"aTexCoord");
        muMvpMatrixHandle = GLES20.glGetUniformLocation(mProgram,"uMvpMatrix");
        muTexMatrixHandle = GLES20.glGetUniformLocation(mProgram,"uTexMatrix");
        mSinglrStepOffsetHandle = GLES20.glGetUniformLocation(mProgram,"singleStepOffset");
        mParamaHandle = GLES20.glGetUniformLocation(mProgram,"params");
    }

    @Override
    protected void filter() {
        GLES20.glUniform1f(mParamaHandle,1f);
        GLES20.glUniform2fv(mSinglrStepOffsetHandle, 1, FloatBuffer
                .wrap(new float[]{2.0f/mInputWidth,2.0f/mInputHeight}));
    }
}
