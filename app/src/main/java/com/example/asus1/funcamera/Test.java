package com.example.asus1.funcamera;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;

public class Test {

    private static String mUrl = "http://www.kugou.com/yy/rank/home/1-6666.html?from=rank";

    public static void main(String[] args){
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Document document = Jsoup.connect(mUrl).get();
                    Element body = document.body();

                    Element content = body.getElementsByClass("pc_temp_wrap").first();

                    // Elements elements = content.children().first().children();

                    Element left = content.getElementsByClass("pc_temp_main").first();
                    Element center = left.getElementsByClass("pc_temp_content").first();
                    left = left.getElementsByClass("pc_temp_side").first();
                    left = left.getElementsByClass("pc_rank_sidebar").first();
                    Elements lis = left.getElementsByTag("li");
                    for(Element li:lis){
                       Element element = li.getElementsByTag("a").first();
                       String title = element.attr("title");
                       String src = element.attr("href");
                       Element span = element.getElementsByTag("span").first();
                       String icon = span.attr("style");
                       icon = icon.split("src='")[1];
                       icon = icon.split("',")[0];

                    }

                    center = center.getElementsByClass("pc_temp_container").first();
                    center = center.getElementById("rankWrap");
                    center = center.getElementsByClass("pc_temp_songlist").first();
                    Elements clis = center.getElementsByTag("li");
                    for(Element li:clis){
                        String title = li.attr("title");
                        String src = li.getElementsByTag("a").attr("href");
                        String time = li.getElementsByClass("pc_temp_time").text();
                        System.out.println(title+"----"+src+"----"+time);

                    }

                }catch (IOException e){
                    e.printStackTrace();
                }

            }

        }).start();
    }
}
