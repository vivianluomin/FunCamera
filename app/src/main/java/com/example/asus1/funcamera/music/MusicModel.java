package com.example.asus1.funcamera.music;

public class MusicModel {

    private String mTitle;
    private String mTime;
    private String mSrc;

    public MusicModel(String mTitle, String mTime) {
        this.mTitle = mTitle;
        this.mTime = mTime;
    }

    public String getmTitle() {
        return mTitle;
    }

    public void setmTitle(String mTitle) {
        this.mTitle = mTitle;
    }

    public String getmTime() {
        return mTime;
    }

    public void setmTime(String mTime) {
        this.mTime = mTime;
    }

    public String getmSrc() {
        return mSrc;
    }

    public void setmSrc(String mSrc) {
        this.mSrc = mSrc;
    }
}
