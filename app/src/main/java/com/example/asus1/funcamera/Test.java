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
                    Document document = Jsoup.connect("http://www.kugou.com/song/dxf244a.html")
                            .get();
                    Element body = document.body();
                    System.out.println(body.html());
                    Element mainPage = body.getElementsByClass("mainPage").first();
                    Element album = mainPage.getElementsByClass("content").first()
                            .getElementsByClass("singerContent").first();
                    System.out.println(album.html());
//                    Element image = album.getElementsByTag("a").first()
//                            .getElementsByTag("img").first();
//                    String album_image = image.attr("src");
//                    System.out.println(album_image);
                }catch (IOException e){
                    e.printStackTrace();
                }
            }
        }).start();

    }
}


