package com.example.asus1.funcamera.Video;

import android.graphics.Bitmap;

public class VideoModel {

    private Bitmap mCover;
    private String mSrc;
    private String mTime;

    public VideoModel(Bitmap mCover, String mSrc, String mTime) {
        this.mCover = mCover;
        this.mSrc = mSrc;
        this.mTime = mTime;
    }

    public Bitmap getmCover() {
        return mCover;
    }

    public void setmCover(Bitmap mCover) {
        this.mCover = mCover;
    }

    public String getmSrc() {
        return mSrc;
    }

    public void setmSrc(String mSrc) {
        this.mSrc = mSrc;
    }

    public String getmTime() {
        return mTime;
    }

    public void setmTime(String mTime) {
        this.mTime = mTime;
    }
}
