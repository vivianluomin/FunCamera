package com.example.asus1.funcamera.RecordVideo.Views;

import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.example.asus1.funcamera.Base.BaseActivity;
import com.example.asus1.funcamera.R;
import com.example.asus1.funcamera.RecordVideo.Controller.RecordPersenter;
import com.example.asus1.funcamera.RecordVideo.Controller.ViewController;
import com.example.asus1.funcamera.RecordVideo.RecordUtil.AudioRecordEncode;
import com.example.asus1.funcamera.RecordVideo.RecordUtil.VideoMediaMuxer;
import com.example.asus1.funcamera.RecordVideo.RecordUtil.VideoRecordEncode;
import com.example.asus1.funcamera.RecordVideo.RecordUtil.onFramPrepareLisnter;

import java.io.IOException;

public class RecordActivtiy extends BaseActivity implements View.OnClickListener,ViewController{

    private static final String TAG = "RecordActivtiy";
    private RecordButtonView mRecordButtom;
    private RecordView mRecordView;
    private boolean mRecord = false;
    private RecordPersenter mPresenter = RecordPersenter.getPresenterInstantce();

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
    protected void onResume() {
        super.onResume();
        mPresenter.setViewController(this);
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
            stopRecording();
            mRecord = false;
        }else {
            mRecordButtom.setClick(true);
            startRecording();
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

    @Override
    public void startRecording() {
        mPresenter.startRecoding();
        startRecordUI();
    }

    @Override
    public void stopRecording() {
        mPresenter.stopRecoding();
    }

    @Override
    public void setVideoEncode(VideoRecordEncode encode) {
        mRecordView.setVideoEndoer(encode);
    }

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
