package com.example.asus1.funcamera.Utils;

import android.opengl.GLES20;
import android.util.Log;

public class ShaderUtil {

    private static final String ERROR   = "ES30_ERROR";

    private static int loadShader(int shaderType,String shaderSource){

        int shader = GLES20.glCreateShader(shaderType);
        if(shader!=0){
            GLES20.glShaderSource(shader,shaderSource);
            GLES20.glCompileShader(shader);

            int[] compiledId = new int[1];
            GLES20.glGetShaderiv(shader,GLES20.GL_COMPILE_STATUS,compiledId,0);
            if(compiledId[0] == 0){
                Log.e(ERROR,"Could not compile shader "+shaderType);
                Log.e(ERROR, GLES20.glGetShaderInfoLog(shader) );
                GLES20.glDeleteShader(shader);
                shader = 0;
            }
        }

        return shader;
    }

    public static int loadProgram(String vertexShaderSource,String fragmentShaderSource){

        int vertexShader = loadShader(GLES20.GL_VERTEX_SHADER,vertexShaderSource);
        if(vertexShader == 0){
            return 0;
        }

        int fragmentShader = loadShader(GLES20.GL_FRAGMENT_SHADER,fragmentShaderSource);

        if(fragmentShader == 0){
            return  0;
        }

        int program = GLES20.glCreateProgram();
        if(program!=0){
            GLES20.glAttachShader(program,vertexShader);
            checkGLError("glAttachShader");
            GLES20.glAttachShader(program,fragmentShader);
            checkGLError("glAttachShader");
            GLES20.glLinkProgram(program);//链接程序

            int[] linkStatus = new int[1];
            GLES20.glGetProgramiv(program,GLES20.GL_LINK_STATUS,linkStatus,0);
            if(linkStatus[0] != GLES20.GL_TRUE){
                Log.e(ERROR, "Could not link program" );
                Log.e(ERROR, GLES20.glGetProgramInfoLog(program));
                GLES20.glDeleteProgram(program);
                program = 0;
            }

        }

        return program;
    }

    private static void checkGLError(String info){
        int error;
        if((error = GLES20.glGetError())!=GLES20.GL_NO_ERROR){
            Log.e(ERROR,info+": glError "+error);
            throw  new RuntimeException(info+": glError "+error);
        }
    }

}
