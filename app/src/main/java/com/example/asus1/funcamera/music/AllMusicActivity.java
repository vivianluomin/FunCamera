package com.example.asus1.funcamera.music;

import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.webkit.ConsoleMessage;
import android.webkit.WebChromeClient;
import android.webkit.WebResourceResponse;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.asus1.funcamera.Base.BaseActivity;
import com.example.asus1.funcamera.R;
import com.github.lzyzsd.jsbridge.BridgeWebView;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class AllMusicActivity extends BaseActivity implements Handler.Callback{

    private RecyclerView mRecyclerView;
    private RecyclerView mRecyclerViewLeft;
    private ImageView mClose;
    private TextView mTitle;

    private MusicLeftRecyclerAdapter mLeftAdapter;
    private MusicMainRecyclerAdapter mMainAdapter;
    private List<MusicModel> mMainData = new ArrayList<>();
    private List<MusicTypeModel> mLeftData = new ArrayList<>();
    private Handler mHandler;
    private final int MSG_INIT = 100;
    private final int MSG_UPDATE = 200;
    private final int MSG_PLAY = 300;

    BridgeWebView mWebView;

    private String mUrl = "http://www.kugou.com/yy/rank/home/1-6666.html?from=rank";

    private static final String TAG = "AllMusicActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_all_music);
        mHandler = new Handler(getMainLooper(),this);
        EventBus.getDefault().register(this);
        init();
    }

    private void init(){
        mRecyclerView = (RecyclerView)findViewById(R.id.recycler_view);
        mRecyclerViewLeft = (RecyclerView)findViewById(R.id.recycler_view_left);
        mClose = (ImageView)findViewById(R.id.iv_close);
        mTitle = (TextView)findViewById(R.id.tv_title);
        mLeftAdapter = new MusicLeftRecyclerAdapter(this,mLeftData);
        mMainAdapter = new MusicMainRecyclerAdapter(this,mMainData);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mRecyclerView.setAdapter(mMainAdapter);
        mRecyclerViewLeft.setLayoutManager(new LinearLayoutManager(this));
        mRecyclerViewLeft.setAdapter(mLeftAdapter);
        initData();
    }

    private void initData(){

        new Thread(new Runnable() {


            @Override
            public void run() {
                try {
                    Document document = Jsoup.connect(mUrl).get();
                    Element body = document.body();

                    Element content = body.getElementsByClass("pc_temp_wrap").first();
                    Element left = content.getElementsByClass("pc_temp_main").first();
                    Element center = left.getElementsByClass("pc_temp_content").first();

                   List<MusicModel> musicModels = getMusicData(center);
                   List<MusicTypeModel> typeModels = getLeftData(left);
                    Log.d(TAG, "run: "+musicModels.size()+"---"+typeModels.size());

                   mMainData.clear();
                   mMainData.addAll(musicModels);
                   mLeftData.clear();
                   mLeftData.addAll(typeModels);
                   mHandler.obtainMessage(MSG_INIT).sendToTarget();

                }catch (IOException e){
                    e.printStackTrace();
                }

            }

        }).start();

    }

    @Override
    public boolean handleMessage(Message msg) {
        switch (msg.what){
            case MSG_INIT:
                Log.d(TAG, "handleMessage: "+mMainData.size());
                mMainAdapter.notifyDataSetChanged();
                mLeftAdapter.notifyDataSetChanged();
                break;

            case MSG_UPDATE:
                Log.d(TAG, "handleMessage: "+mMainData.size());
                String string = (String) msg.obj;
                mTitle.setText(string);
                mMainAdapter.notifyDataSetChanged();
                break;
            case MSG_PLAY:
                String ulr = (String)msg.obj;

        }

        return false;
    }

    private Element connect(String url){

        try {
            Document document = Jsoup.connect(url).get();
            Element body = document.body();

            Element content = body.getElementsByClass("pc_temp_wrap").first();

            // Elements elements = content.children().first().children();

            Element left = content.getElementsByClass("pc_temp_main").first();
            Element center = left.getElementsByClass("pc_temp_content").first();

            return center;

        }catch (IOException e){
            e.printStackTrace();
        }

        return null;
    }

    private List<MusicTypeModel> getLeftData(Element left){

        List<MusicTypeModel> typeModels = new ArrayList<>();
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
            MusicTypeModel model = new MusicTypeModel(title,icon);
            model.setmSrc(src);
            typeModels.add(model);

        }

        return typeModels;
    }

    private List<MusicModel> getMusicData(Element center){
        List<MusicModel> models = new ArrayList<>();

        center = center.getElementsByClass("pc_temp_container").first();
        center = center.getElementById("rankWrap");
        center = center.getElementsByClass("pc_temp_songlist").first();
        Elements clis = center.getElementsByTag("li");
        for(Element li:clis){
            String title = li.attr("title");
            String src = li.getElementsByTag("a").attr("href");
            String time = li.getElementsByClass("pc_temp_time").text();
            MusicModel musicModel = new MusicModel(title,time);
            musicModel.setmSrc(src);
            models.add(musicModel);
        }

        return models;
    }

    @Subscribe
    public void changeMusic(final ChangeMessage message){
        new Thread(new Runnable() {
            @Override
            public void run() {
                List<MusicModel> musicModels = getMusicData(connect(message.getSrc()));
                Log.d(TAG, "run: "+message.getSrc());
                Log.d(TAG, "run: "+musicModels.get(0).getmTitle());
                mMainData.clear();
                mMainData.addAll(musicModels);
                mHandler.obtainMessage(MSG_UPDATE,message.getTitle()).sendToTarget();
            }
        }).start();


    }

    @Subscribe
    public void getMusic(final MusicMessage musicMessage){

         mWebView = new BridgeWebView(this);
        mWebView.loadUrl(musicMessage.getSrc());
        mWebView.setWebChromeClient(new WebChromeClient(){
            @Override
            public boolean onConsoleMessage(ConsoleMessage consoleMessage) {
                if(consoleMessage.message().contains(".mp3")){
                    String s = consoleMessage.message().split("audio file '")[1];
                    s = s.split("'.")[0];
                    mHandler.obtainMessage(MSG_PLAY,s).sendToTarget();
                }
                return super.onConsoleMessage(consoleMessage);
            }

        });



    }


    @Override
    protected void onDestroy() {
        EventBus.getDefault().unregister(this);
        super.onDestroy();

    }
}
