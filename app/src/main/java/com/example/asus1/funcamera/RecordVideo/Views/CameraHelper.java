package com.example.asus1.funcamera.RecordVideo.Views;

import android.content.Context;
import android.graphics.SurfaceTexture;
import android.hardware.camera2.CameraAccessException;
import android.hardware.camera2.CameraCaptureSession;
import android.hardware.camera2.CameraCharacteristics;
import android.hardware.camera2.CameraDevice;
import android.hardware.camera2.CameraManager;
import android.hardware.camera2.CaptureFailure;
import android.hardware.camera2.CaptureRequest;
import android.hardware.camera2.CaptureResult;
import android.hardware.camera2.TotalCaptureResult;
import android.opengl.EGLSurface;
import android.os.HandlerThread;
import android.os.Looper;
import android.support.annotation.NonNull;
import android.support.v4.content.PermissionChecker;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;
import android.view.Surface;
import android.view.SurfaceView;
import android.view.WindowManager;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Handler;

public class CameraHelper  {

    private Context mContext;
    private CameraManager mCameraManager;
    private CameraDevice mCameraDevice;
    private String mCameraId = null;
    private boolean mFlashSupport = false;
    private Surface mSurface;
    private CaptureRequest.Builder mPreviewBuilder;

    private android.os.Handler mMainHandler;
    private android.os.Handler mChildHanlder;

    private static final String TAG = "CameraHelper";


    public CameraHelper(Context context, SurfaceTexture surfaceTexture){

        mContext = context;
        Display display = ((WindowManager)context
                .getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay();
        DisplayMetrics metrics = new DisplayMetrics();
        display.getMetrics(metrics);
        int screenWidth = metrics.widthPixels;
        int screenHidth = metrics.heightPixels;

        //设置图像像素比位4:3
        surfaceTexture.
                setDefaultBufferSize(4*screenWidth/3,3 * screenWidth / 4);
        mSurface = new Surface(surfaceTexture);
        initCamera();
    }

    private void initCamera(){

        //得到CameraManager
        mCameraManager = (CameraManager) mContext.getSystemService(Context.CAMERA_SERVICE);
        try {
            for(String id : mCameraManager.getCameraIdList()){

                CameraCharacteristics characteristics
                        = mCameraManager.getCameraCharacteristics(id);
                Integer front = characteristics.get(CameraCharacteristics.LENS_FACING);
                mCameraId = id;
                //是否支持闪光灯
                Boolean available = characteristics.
                        get(CameraCharacteristics.FLASH_INFO_AVAILABLE);
                mFlashSupport = available == null? false :available;

                //启动前置摄像头
                if(front!=null && front == CameraCharacteristics.LENS_FACING_FRONT){
                    break;
                }
            }

            mMainHandler = new android.os.Handler(Looper.getMainLooper());
            if(mCameraId!=null){
                //打开摄像头
                mCameraManager.openCamera(mCameraId,mStateCallback
                        ,mMainHandler);

            }

        }catch (CameraAccessException e){
            e.printStackTrace();
        }

    }

    private CameraDevice.StateCallback mStateCallback = new CameraDevice.StateCallback() {
        @Override
        public void onOpened(@NonNull CameraDevice camera) {

            mCameraDevice = camera;
            //开启预览
            createPreview();
        }

        @Override
        public void onDisconnected(@NonNull CameraDevice camera) {

            mCameraDevice.close();
            mCameraDevice = null;

        }

        @Override
        public void onError(@NonNull CameraDevice camera, int error) {

            mCameraDevice.close();
            mCameraDevice = null;

        }
    };

    private void createPreview(){

        List<Surface> surfaces = new ArrayList<>();
        surfaces.add(mSurface);
        try {
            //设置一个具有输出Surface的CaptureRequest.Builder
           mPreviewBuilder =  mCameraDevice.createCaptureRequest(CameraDevice.TEMPLATE_PREVIEW);
            mPreviewBuilder.addTarget(mSurface);

            //进行相机预览
            mCameraDevice.createCaptureSession(surfaces,mStateCallbackSession,null);

        }catch (CameraAccessException e){
            e.printStackTrace();
        }



    }

    private CameraCaptureSession.StateCallback  mStateCallbackSession = new CameraCaptureSession.StateCallback() {
        @Override
        public void onConfigured(@NonNull CameraCaptureSession session) {

            mPreviewBuilder.set(CaptureRequest.CONTROL_AF_MODE,
                    CaptureRequest.CONTROL_AF_MODE_CONTINUOUS_PICTURE);
            try {
                //发送请求
                session.setRepeatingRequest(mPreviewBuilder.build(),
                        null,null);

            }catch (CameraAccessException e){
                e.printStackTrace();
            }

        }

        @Override
        public void onConfigureFailed(@NonNull CameraCaptureSession session) {
            Log.d(TAG, "onConfigureFailed: ");
        }
    };

}
