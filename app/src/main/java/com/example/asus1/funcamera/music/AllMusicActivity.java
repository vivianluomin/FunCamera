package com.example.asus1.funcamera.music;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.asus1.funcamera.Base.BaseActivity;
import com.example.asus1.funcamera.R;

import java.util.ArrayList;
import java.util.List;

public class AllMusicActivity extends BaseActivity {

    private RecyclerView mRecyclerView;
    private RecyclerView mRecyclerViewLeft;
    private ImageView mClose;
    private TextView mTitle;

    private MusicLeftRecyclerAdapter mLeftAdapter;
    private MusicMainRecyclerAdapter mMainAdapter;
    private List<MusicModel> mMainData = new ArrayList<>();
    private List<MusicTypeModel> mLeftData = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_all_music);
        init();
    }

    private void init(){
        mRecyclerView = (RecyclerView)findViewById(R.id.recycler_view);
        mRecyclerViewLeft = (RecyclerView)findViewById(R.id.recycler_view_left);
        mClose = (ImageView)findViewById(R.id.iv_close);
        mTitle = (TextView)findViewById(R.id.tv_title);
        mLeftAdapter = new MusicLeftRecyclerAdapter(this,mLeftData);
        mMainAdapter = new MusicMainRecyclerAdapter(this,mMainData);
        initData();
    }

    private void initData(){

    }
}
