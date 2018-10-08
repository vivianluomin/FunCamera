package com.example.asus1.funcamera.Video;

import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;

import com.example.asus1.funcamera.Base.Base2Activity;
import com.example.asus1.funcamera.R;

import java.util.ArrayList;
import java.util.List;

public class AllVideoActivity extends Base2Activity implements Handler.Callback{

    private ImageView mClose;
    private RecyclerView mRecyclerView;
    private AllVideoAdapter mAdapter;
    private List<VideoModel> mDatas = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_all_video);
        init();
    }

    private void init(){

        mClose = (ImageView)findViewById(R.id.iv_close);
        mClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        mRecyclerView = (RecyclerView)findViewById(R.id.recycler_view);
        mAdapter = new AllVideoAdapter(this,mDatas);
        mRecyclerView.setLayoutManager(new GridLayoutManager(this,3,
                GridLayoutManager.VERTICAL,false));
        mRecyclerView.setAdapter(mAdapter);
        getData();

    }


    private void getData(){

    }

    @Override
    public boolean handleMessage(Message msg) {

        return false;

    }
}
