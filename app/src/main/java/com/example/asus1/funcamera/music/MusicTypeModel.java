package com.example.asus1.funcamera.music;

public class MusicTypeModel {

    private String mTitle;
    private String mIcon;
    private String mSrc;

    public MusicTypeModel(String mTitle, String mIcon) {
        this.mTitle = mTitle;
        this.mIcon = mIcon;
    }

    public String getmTitle() {
        return mTitle;
    }

    public void setmTitle(String mTitle) {
        this.mTitle = mTitle;
    }

    public String getmIcon() {
        return mIcon;
    }

    public void setmIcon(String mIcon) {
        this.mIcon = mIcon;
    }

    public String getmSrc() {
        return mSrc;
    }

    public void setmSrc(String mSrc) {
        this.mSrc = mSrc;
    }
}
