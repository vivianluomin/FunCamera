package com.example.asus1.funcamera.RecordVideo.Views;

import android.content.Intent;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;

import com.example.asus1.funcamera.Base.BaseActivity;
import com.example.asus1.funcamera.R;
import com.example.asus1.funcamera.RecordVideo.Controller.RecordPersenter;
import com.example.asus1.funcamera.RecordVideo.Controller.ViewController;
import com.example.asus1.funcamera.RecordVideo.RecordUtil.AudioRecordEncode;
import com.example.asus1.funcamera.RecordVideo.RecordUtil.VideoMediaMuxer;
import com.example.asus1.funcamera.RecordVideo.RecordUtil.VideoRecordEncode;
import com.example.asus1.funcamera.RecordVideo.RecordUtil.onFramPrepareLisnter;
import com.example.asus1.funcamera.music.AllMusicActivity;

import java.io.IOException;

public class RecordActivtiy extends BaseActivity implements View.OnClickListener
        ,ViewController,MusicPlayerThread.MusicPlayLinstener{

    private static final String TAG = "RecordActivtiy";
    private RecordButtonView mRecordButtom;
    private RecordView mRecordView;
    private ImageView mMusic;
    private ImageView mBeauty;
    private ImageView mSee;
    private boolean mRecord = false;
    private RecordPersenter mPresenter = RecordPersenter.getPresenterInstantce();

    private static int MUSIC_RESULT = 10;
    private String mMusic_Url = "";
    private int mTime = 0;
    private MusicPlayerThread mMusicThread;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_record);
        mMusicThread = new MusicPlayerThread();
        mMusicThread.start();
        mMusicThread.setLinstener(this);
        initView();

    }

    private void initView(){
        mRecordButtom = (RecordButtonView) findViewById(R.id.view_record);
        mRecordButtom.setOnClickListener(this);
        mRecordView = (RecordView) findViewById(R.id.view_display);
        mRecordView.setType(Photo.class);
        mMusic = (ImageView)findViewById(R.id.iv_music);
        mMusic.setOnClickListener(this);
        mBeauty = (ImageView)findViewById(R.id.iv_filter);
        mBeauty.setOnClickListener(this);
        mSee =(ImageView)findViewById(R.id.iv_see);
        mSee.setOnClickListener(this);
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        setWiondow();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if(mMusicThread!=null&&mRecord){
            mMusicThread.play();
        }
        mPresenter.setViewController(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.view_record:
                setRecord();
                break;
            case R.id.iv_music:
                Intent intent = new Intent(RecordActivtiy.this, AllMusicActivity.class);
                startActivityForResult(intent,MUSIC_RESULT);
                break;
            case R.id.iv_filter:
                break;
            case R.id.iv_see:

                
        }
        
    }
    
    private void setRecord(){
        Log.d(TAG, "setRecord: ");
        if(mRecord){
            mRecord = false;
            mRecordButtom.setClick(false);
            mRecordButtom.postInvalidate();
            stopRecording();
            if(!mMusic_Url.equals("")){
                mMusicThread.pause();
            }

        }else {
            mRecord = true;
            mRecordButtom.setClick(true);
            startRecording();
            if(!mMusic_Url.equals("")){
                mMusicThread.play();
            }

        }
    }

    private void startRecordUI(){
        Log.d(TAG, "startRecordUI: ");
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
        int input;
        if(mMusic_Url.equals("")){
             input = MediaRecorder.AudioSource.MIC;
        }else {
            input = MediaRecorder.AudioSource.DEFAULT;
        }
        Log.d(TAG, "startRecording: ");
        mPresenter.startRecoding(input);
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
        mMusicThread.pause();
        super.onPause();
    }

    @Override
    protected void onStop() {
        Log.d(TAG, "onStop: ");
        mMusicThread.pause();
        super.onStop();
    }

    @Override
    public void compelte() {

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == MUSIC_RESULT && resultCode == RESULT_OK){
            mMusic_Url = data.getStringExtra("music");
            mTime = data.getIntExtra("time",0);
            mMusicThread.setSrouce(mMusic_Url,mTime/2);
        }
    }

    @Override
    protected void onDestroy() {
        mMusicThread.stopMedia();
        super.onDestroy();
    }
}
