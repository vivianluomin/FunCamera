package com.example.asus1.funcamera.RecordVideo;

import android.app.Activity;
import android.graphics.Color;
import android.os.Build;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

import com.example.asus1.funcamera.Base.BaseActivity;
import com.example.asus1.funcamera.R;

public class RecordActivtiy extends BaseActivity implements View.OnClickListener{

    private static final String TAG = "RecordActivtiy";
    private RecordButtonView mRecordButtom;
    private boolean mRecord = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_record);
        initView();

    }

    private void initView(){
        mRecordButtom = findViewById(R.id.view_record);
        mRecordButtom.setOnClickListener(this);
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
            startRecordUI();
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
}
