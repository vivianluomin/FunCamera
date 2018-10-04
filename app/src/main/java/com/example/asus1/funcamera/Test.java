package com.example.asus1.funcamera;

import android.util.Log;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class Test {

    private static String mUrl = "http://www.kugou.com/yy/rank/home/1-6666.html?from=rank";

    public static void main(String[] args) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Document document = Jsoup.connect("http://www.kugou.com/song/qb6iy13.html")
                            .userAgent("Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/69.0.3497.100 Safari/537.36")
                            .header("Accept","text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,image/apng,*/*;q=0.8")
                            .header("Accept-Encoding","gzip, deflate")
                            .header("Cache-Control","max-age=0")
                            .header("Connection","keep-alive")
                            .header("Host","www.kugou.com")
                            .followRedirects(true)
                            .get();
                    Element body = document.body();
                    System.out.println(body.html());
                    Element mainPage = body.getElementsByClass("mainPage").first();
                    Element audio = mainPage.getElementById("myAudio");
                    System.out.println(audio.toString());
                    String src = audio.attr("src");
                    System.out.println(src);
                }catch (IOException e){
                    e.printStackTrace();
                }
            }
        }).start();


    }
}


