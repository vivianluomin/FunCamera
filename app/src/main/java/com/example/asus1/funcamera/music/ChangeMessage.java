package com.example.asus1.funcamera.music;

public class ChangeMessage {

    private String src;
    private String title;


    public ChangeMessage(String src,String title) {
        this.src = src;
        this.title = title;
    }

    public String getSrc() {
        return src;
    }

    public void setSrc(String src) {
        this.src = src;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }
}
