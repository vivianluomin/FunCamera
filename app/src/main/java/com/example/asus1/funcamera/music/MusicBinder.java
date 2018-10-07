package com.example.asus1.funcamera.music;

import android.media.MediaPlayer;
import android.os.Binder;
import android.util.Log;

import java.io.IOException;

public class MusicBinder extends Binder {
    private static final String TAG = "MusicBinder";

    private String mUrl;
    private MediaPlayer mMediaPlayer = new MediaPlayer();
    private int mTime;
    private playControllerListener mListener;


    public interface playControllerListener{
         void playing();
         void pausiong();
    }

    public void setController(playControllerListener listener){
        mListener = listener;
    }

   public void setUrl(String url){
       mUrl = url;
   }


   public void prepareMediaPlayer(String url,int time){
       try {
           mUrl = url;
           mTime = time/2;
           mMediaPlayer.reset();
           mMediaPlayer.setDataSource(url);
           mMediaPlayer.prepareAsync();
           mMediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
               @Override
               public void onPrepared(MediaPlayer mp) {
                    playMusic();
                   mMediaPlayer.seekTo(mTime);
               }
           });
       }catch (IOException e){
           e.printStackTrace();
       }


   }

    public void playMusic(){
        Log.d(TAG, "playMusic: "+Thread.currentThread());
        if(!mMediaPlayer.isPlaying()){
            mMediaPlayer.start();
            mListener.playing();
        }
    }

    public void pauseMusic(){
       if(mMediaPlayer.isPlaying()){
           mMediaPlayer.pause();
           mListener.pausiong();
       }
    }

    public void stopMusic(){
       mMediaPlayer.stop();
       mMediaPlayer.release();
       mMediaPlayer = null;
    }

    public boolean isPlaying(){
       return mMediaPlayer.isPlaying();
    }
}
