package com.example.asus1.funcamera.music;

import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.media.MediaPlayer;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.webkit.ConsoleMessage;
import android.webkit.WebChromeClient;
import android.webkit.WebResourceResponse;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.example.asus1.funcamera.Base.BaseActivity;
import com.example.asus1.funcamera.R;
import com.example.asus1.funcamera.Utils.Util;
import com.github.lzyzsd.jsbridge.BridgeWebView;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class AllMusicActivity extends BaseActivity implements Handler.Callback,
        View.OnClickListener,MusicBinder.playControllerListener{

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
    private MusicBinder mBinder;
    private MusicConnection mConnection;
    private RelativeLayout mBottom;
    private ImageView mPlayController;
    private TextView mUseMusic;
    private TextView mstartTime;
    private TextView mEndTime;
    private TextView mMusicName;
    private String mUrl = "http://www.kugou.com/yy/rank/home/1-6666.html?from=rank";
    private String mCurrntMusic = "";

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
        mClose.setOnClickListener(this);
        mTitle = (TextView)findViewById(R.id.tv_title);
        mLeftAdapter = new MusicLeftRecyclerAdapter(this,mLeftData);
        mMainAdapter = new MusicMainRecyclerAdapter(this,mMainData);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mRecyclerView.setAdapter(mMainAdapter);
        mRecyclerViewLeft.setLayoutManager(new LinearLayoutManager(this));
        mRecyclerViewLeft.setAdapter(mLeftAdapter);
        mBottom = (RelativeLayout)findViewById(R.id.relative_bottom);
        mMusicName = (TextView)findViewById(R.id.tv_music_name);
        mPlayController = (ImageView)findViewById(R.id.iv_control);
        mPlayController.setOnClickListener(this);
        mstartTime = (TextView)findViewById(R.id.tv_start_time);
        mEndTime = (TextView)findViewById(R.id.tv_end_time);
        mUseMusic = (TextView)findViewById(R.id.tv_use);
        mUseMusic.setOnClickListener(this);
        mConnection = new MusicConnection();
        Intent intent = new Intent(this,MusicService.class);
        bindService(intent,mConnection,BIND_AUTO_CREATE);
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
                Bundle bundle = msg.getData();
                String ulr = bundle.getString("src");
                String time = bundle.getString("time");
                mMusicName.setText(bundle.getString("name"));
                mCurrntMusic =ulr;
                play(ulr,time);
                break;


        }

        return false;
    }

    private void play(final String url,String time){


            mBottom.setVisibility(View.VISIBLE);
            mEndTime.setText(time);
            int ti = Util.getMS(time);
            int half = ti/2/1000;
            String half_time = String.valueOf(half/60)+":"+String.valueOf(half%60);
            mstartTime.setText(half_time);
            mBinder.prepareMediaPlayer(url,ti);

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
                    Message message = new Message();
                    Bundle bundle = new Bundle();
                    bundle.putString("src",s);
                    bundle.putString("time",musicMessage.getmTime());
                    bundle.putString("name",musicMessage.getmName());
                    message.setData(bundle);
                    message.what = MSG_PLAY;
                    mHandler.sendMessage(message);
                }
                return super.onConsoleMessage(consoleMessage);
            }

        });

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.iv_control:
                if(mBinder.isPlaying()){
                    mBinder.pauseMusic();
                }else {
                    mBinder.playMusic();
                }
                break;
            case R.id.tv_use:
                if(!mCurrntMusic.equals("")){
                    Intent intent = new Intent();
                    intent.putExtra("music",mCurrntMusic);
                    intent.putExtra("time",Util.getMS(mEndTime.getText().toString()));
                    setResult(RESULT_OK,intent);
                    finish();
                }
                break;

            case R.id.iv_close:
                finish();
                break;
        }
    }

    @Override
    public void playing() {
        mPlayController.setImageResource(R.mipmap.ic_pause);
    }

    @Override
    public void pausiong() {
        mPlayController.setImageResource(R.mipmap.ic_play);
    }

    @Override
    protected void onDestroy() {
        EventBus.getDefault().unregister(this);
        mBinder.stopMusic();
        unbindService(mConnection);
        stopService(new Intent(this,MusicService.class));
        super.onDestroy();

    }

    private class MusicConnection implements ServiceConnection{
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            mBinder = (MusicBinder) service;
            Log.d(TAG, "onServiceConnected: ");
            mBinder.setController(AllMusicActivity.this);
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {

        }
    }
}
