package com.example.asus1.funcamera.RecordVideo.Views;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Intent;
import android.media.MediaRecorder;
import android.opengl.EGLContext;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;

import com.example.asus1.funcamera.Base.BaseActivity;
import com.example.asus1.funcamera.R;
import com.example.asus1.funcamera.RecordVideo.Controller.RecordPersenter;
import com.example.asus1.funcamera.RecordVideo.Controller.ViewController;
import com.example.asus1.funcamera.RecordVideo.Encoder.VideoEncoder;
import com.example.asus1.funcamera.RecordVideo.RecordUtil.SplitVideoThread;
import com.example.asus1.funcamera.RecordVideo.RecordUtil.VideoRecordEncode;
import com.example.asus1.funcamera.Video.AllVideoActivity;
import com.example.asus1.funcamera.music.AllMusicActivity;

import java.io.File;

public class RecordActivtiy extends BaseActivity implements View.OnClickListener
        ,ViewController,MusicPlayerThread.MusicPlayLinstener,Handler.Callback{

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
    private String mVideoPath;

    private Dialog mLoadingDoalog;

    public EGLContext mSharedContext;

    private Handler mMainHandler = new Handler(this);

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
        mLoadingDoalog = new AlertDialog.Builder(this)
                .setMessage("请稍等")
                .setTitle("合成中")
                .create();

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
                if(mMusic_Url!=null&&!mMusic_Url.equals("")){
                    mLoadingDoalog.show();
                    new SplitVideoThread("vivian",mVideoPath,mMusic_Url
                    ,mTime,mMainHandler).start();

                }else {
                    Intent intent1 = new Intent(RecordActivtiy.this,
                            AllVideoActivity.class);
                    startActivity(intent1);
                }

                break;

                
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
        //mPresenter.startRecoding(input);
        mRecordView.getEGLContext(this,mPresenter);
        startRecordUI();
        //mVideoPath = mPresenter.getVideoPath();
    }


    @Override
    public void stopRecording() {
        mPresenter.stopRecoding();
        mRecordView.setVideoEncoder(null);
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
    public boolean handleMessage(Message msg) {
        mLoadingDoalog.dismiss();

        if(msg.what == 1){
            String path = (String) msg.obj;
            if(path!=null){
                File file = new File(mVideoPath);
                file.delete();
            }

            Intent intent1 = new Intent(RecordActivtiy.this,
                    AllVideoActivity.class);
            startActivity(intent1);
        }


        return false;
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
