package com.example.asus1.funcamera.music;

public class MusicMessage {

    private String src;
    private String mTime;
    private String mName;

    public MusicMessage(String src,String time,String name) {
        this.src = src;
        mTime  =time;
        mName = name;
    }

    public String getmName() {
        return mName;
    }

    public void setmName(String mName) {
        this.mName = mName;
    }

    public String getSrc() {
        return src;
    }

    public void setSrc(String src) {
        this.src = src;
    }

    public String getmTime() {
        return mTime;
    }

    public void setmTime(String mTime) {
        this.mTime = mTime;
    }
}
