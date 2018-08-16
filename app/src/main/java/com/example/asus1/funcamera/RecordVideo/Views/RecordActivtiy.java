package com.example.asus1.funcamera.RecordVideo.Views;

import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.example.asus1.funcamera.Base.BaseActivity;
import com.example.asus1.funcamera.R;
import com.example.asus1.funcamera.RecordVideo.RecordUtil.AudioRecordEncode;
import com.example.asus1.funcamera.RecordVideo.RecordUtil.VideoRecordEncode;
import com.example.asus1.funcamera.RecordVideo.RecordUtil.onFramPrepareLisnter;

public class RecordActivtiy extends BaseActivity implements View.OnClickListener{

    private static final String TAG = "RecordActivtiy";
    private RecordButtonView mRecordButtom;
    private RecordView mRecordView;
    private boolean mRecord = false;
    private VideoRecordEncode mVideoEncode;
    private AudioRecordEncode mAudioEncode;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_record);
        initView();

    }

    private void initView(){
        mRecordButtom = findViewById(R.id.view_record);
        mRecordButtom.setOnClickListener(this);
        mRecordView = findViewById(R.id.view_display);
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        setWiondow();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.view_record:
                setRecord();
                break;
                
        }
        
    }
    
    private void setRecord(){
        Log.d(TAG, "setRecord: ");
        if(mRecord){
            mRecordButtom.setClick(false);
            mRecordButtom.postInvalidate();
            mRecord = false;
        }else {
            mRecordButtom.setClick(true);
            startRecoord();
            mRecord = true;
        }
    }

    private void startRecordUI(){
        new Thread(new Runnable() {
            @Override
            public void run() {
                while (mRecord){
                    RecordActivtiy.this.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            mRecordButtom.postInvalidate();

                        }
                    });

                    try {
                        Thread.sleep(100);
                    }catch (InterruptedException e){
                        e.printStackTrace();
                    }
                }
            }
        }).start();
    }

    private void startRecoord(){
        Log.d(TAG, "startRecoord: "+Thread.currentThread().getName());
        mVideoEncode = new
                VideoRecordEncode(lisnter,1280, 720);
        mAudioEncode = new AudioRecordEncode();
        mVideoEncode.prepare();
        mAudioEncode.onPerpare();
        mVideoEncode.startRecord();
        mAudioEncode.startRecording();
        startRecordUI();


    }

    private  onFramPrepareLisnter lisnter = new onFramPrepareLisnter() {
        @Override
        public void onPrepare(VideoRecordEncode encode) {
            mRecordView.setVideoEndoer(encode);
            Log.d(TAG, "onPrepare: ");


        }
    };

    @Override
    protected void onPause() {
        Log.d(TAG, "onPause: ");
        super.onPause();
    }

    @Override
    protected void onStop() {
        Log.d(TAG, "onStop: ");
        super.onStop();
    }
    
    
}
