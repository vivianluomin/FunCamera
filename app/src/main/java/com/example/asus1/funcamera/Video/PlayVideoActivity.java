package com.example.asus1.funcamera.Video;

import android.content.Intent;
import android.media.MediaPlayer;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.MediaController;
import android.widget.VideoView;

import com.example.asus1.funcamera.Base.BaseActivity;
import com.example.asus1.funcamera.R;

public class PlayVideoActivity extends BaseActivity implements View.OnClickListener{

    private VideoView mVideoView;
    private ImageView mClose;
    private String mSrc;
    private ImageView mPause;
    private boolean mPlaying = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_play_video);
        Intent intent = getIntent();
        mSrc = intent.getStringExtra("src");
        init();
        if(mSrc!=null&&!mSrc.equals("")){
            setData();
        }



    }

    private void init(){
        mVideoView = (VideoView)findViewById(R.id.video_view);
        mVideoView.setOnClickListener(this);
        mVideoView.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                mPause.setVisibility(View.VISIBLE);
                mPlaying = false;
            }
        });

        mPause = (ImageView)findViewById(R.id.iv_pause);
        mPause.setVisibility(View.GONE);
        mClose = (ImageView)findViewById(R.id.iv_close);
        mClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });


    }

    private void setData(){
        mVideoView.setVideoPath(mSrc);
        mVideoView.start();

    }

    @Override
    protected void onResume() {
        super.onResume();
        mVideoView.start();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.video_view:
                if(mPlaying){
                    mVideoView.pause();
                    mPause.setVisibility(View.VISIBLE);
                    mPlaying = false;
                }else {
                    mVideoView.start();
                    mPause.setVisibility(View.GONE);
                    mPlaying = true;
                }

                break;
        }
    }
}