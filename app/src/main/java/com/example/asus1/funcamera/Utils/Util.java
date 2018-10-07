package com.example.asus1.funcamera.Utils;

public class Util {

    public static int getMS(String time){
        String[] tis = time.split(":");
        int ti = Integer.parseInt(tis[0])*60+Integer.parseInt(tis[1]);
        ti = ti*1000;

        return ti;
    }

}
