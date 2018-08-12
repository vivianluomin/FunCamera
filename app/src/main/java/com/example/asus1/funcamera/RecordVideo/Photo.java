package com.example.asus1.funcamera.RecordVideo;

import android.opengl.GLES11Ext;
import android.opengl.GLES20;

import com.example.asus1.funcamera.Utils.ShaderUtil;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

public class Photo {

    private String mVertexShder = "attribute vec3 aPosition;" +
            "attribute vec2 aTexCoord;" +
            "varying vec2 vTextureCoord;" +
            "void main(){" +
            "gl_Position = vec4(aPosition,1);" +
            "vTextureCoord = aTexCoord;" +
            "}";

    private String mFragmentShader = "#extension GL_OES_EGL_image_external : require\n" +
            "precision highp float;" +
            "varying vec2 vTextureCoord;" +
            "uniform samplerExternalOES uSampler;" +
            "void main(){" +
            "gl_FragColor = texture2D(uSampler,vTextureCoord);" +
            "}";

    private int maPositionHandle;
    private int maTextureHandle;

    private FloatBuffer mVertexBuffer;
    private FloatBuffer mTextureBuffer;
    private ByteBuffer mIndexbuffer;

    private int mProgram;

    private int mVCount = 4;
    private int mIndexCount = 6;

    public Photo(){
        initVertexData();
        initFragmentData();
    }

    private void initVertexData(){

        float[] vertexs = new float[]{
                -1,1,0,
                -1,-1,0,
                1,1,0,
                1,-1,0
        };

        ByteBuffer vbb = ByteBuffer.allocateDirect(vertexs.length*4);
        vbb.order(ByteOrder.nativeOrder());
        mVertexBuffer = vbb.asFloatBuffer();
        mVertexBuffer.put(vertexs);
        mVertexBuffer.position(0);

        float[] textures = new float[]{
                0,0,
                0,1,
                1,0,
                1,1
        };

        ByteBuffer cbb = ByteBuffer.allocateDirect(textures.length*4);
        cbb.order(ByteOrder.nativeOrder());
        mTextureBuffer = cbb.asFloatBuffer();
        mTextureBuffer.put(textures);
        mTextureBuffer.position(0);

        byte[] indexs = new byte[]{
          0,1,2,
          1,3,2
        };

        mIndexbuffer = ByteBuffer.allocateDirect(indexs.length);
        mIndexbuffer.order(ByteOrder.nativeOrder());
        mIndexbuffer.put(indexs);
        mIndexbuffer.position(0);

    }

    private void initFragmentData(){

        mProgram = ShaderUtil.loadProgram(mVertexShder,mFragmentShader);
        maPositionHandle = GLES20.glGetAttribLocation(mProgram,"aPosition");
        maTextureHandle = GLES20.glGetAttribLocation(mProgram,"aTexCoord");

    }

    public void draw(int textId){
        GLES20.glUseProgram(mProgram);

        GLES20.glVertexAttribPointer(maPositionHandle,3,GLES20.GL_FLOAT,
                false,3*4,mVertexBuffer);
        GLES20.glVertexAttribPointer(maTextureHandle,2,GLES20.GL_FLOAT,
                false,2*4,mTextureBuffer);
        GLES20.glEnableVertexAttribArray(maPositionHandle);
        GLES20.glEnableVertexAttribArray(maTextureHandle);

        GLES20.glActiveTexture(GLES20.GL_TEXTURE0);
        GLES20.glBindTexture(GLES11Ext.GL_TEXTURE_EXTERNAL_OES,textId);

        GLES20.glDrawElements(GLES20.GL_TRIANGLES,mIndexCount,
                GLES20.GL_UNSIGNED_BYTE,mIndexbuffer);
    }


}
