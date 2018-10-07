package com.example.asus1.funcamera.RecordVideo.Views;

import android.media.MediaPlayer;
import android.os.HandlerThread;

import java.io.IOException;
import java.util.logging.Handler;


public class MusicPlayerThread extends Thread implements MediaPlayer.OnPreparedListener,
        MediaPlayer.OnCompletionListener {

    private MediaPlayer mMeidaPlayer;
    private String mUrl;
    private int mStartTime;
    private MusicPlayLinstener mLinstener;

    public interface MusicPlayLinstener{
        void compelte();
    }

    public void setLinstener(MusicPlayLinstener linstener){
        mLinstener = linstener;
    }

    @Override
    public void run() {
        mMeidaPlayer = new MediaPlayer();
        mMeidaPlayer.setOnPreparedListener(this);
        mMeidaPlayer.setOnCompletionListener(this);
    }

    public void setSrouce(String url,int startTime){
        mUrl = url;
        mStartTime = startTime;
        mMeidaPlayer.reset();
        try {
            mMeidaPlayer.setDataSource(mUrl);
            mMeidaPlayer.prepareAsync();

        }catch (IOException e){
            e.printStackTrace();
        }

    }

    public void play(){
        if(mMeidaPlayer!=null&&!mMeidaPlayer.isPlaying()){
            mMeidaPlayer.start();

        }

    }

    private void seekto(){
        mMeidaPlayer.seekTo(mStartTime);
    }

    @Override
    public void onCompletion(MediaPlayer mp) {
        mLinstener.compelte();
    }

    @Override
    public void onPrepared(MediaPlayer mp) {
        //play();
        seekto();
    }

    public void pause(){
        if(mMeidaPlayer.isPlaying()){
            mMeidaPlayer.pause();
        }

    }

    public void stopMedia(){
        mMeidaPlayer.stop();
        mMeidaPlayer.release();
        mMeidaPlayer = null;
    }
}
