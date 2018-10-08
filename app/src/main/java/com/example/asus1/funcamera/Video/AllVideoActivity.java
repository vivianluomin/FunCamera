package com.example.asus1.funcamera.Video;

import android.graphics.Bitmap;
import android.media.MediaMetadataRetriever;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;

import com.example.asus1.funcamera.Base.Base2Activity;
import com.example.asus1.funcamera.R;
import com.example.asus1.funcamera.RecordVideo.RecordUtil.VideoMediaMuxer;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class AllVideoActivity extends Base2Activity implements Handler.Callback{

    private ImageView mClose;
    private RecyclerView mRecyclerView;
    private AllVideoAdapter mAdapter;
    private List<VideoModel> mDatas = new ArrayList<>();
    private FrameLayout mNoneContent;
    private Handler mHandler = new Handler(this);
    private final int RESPONSE_DATA = 11;

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
        mNoneContent = (FrameLayout)findViewById(R.id.framelayout_none_content);
        getData();

    }


    private void getData(){
        new Thread(new ReadFileRunnable()).start();
    }

    @Override
    public boolean handleMessage(Message msg) {

        switch (msg.what){
            case RESPONSE_DATA:
                mDatas.clear();
                Bundle bundle = msg.getData();
                if (bundle!=null){
                    mDatas.addAll(bundle.<VideoModel>getParcelableArrayList("data"));
                }
                if(mNoneContent.getVisibility()!=View.GONE){
                    mNoneContent.setVisibility(View.GONE);
                }
                mAdapter.notifyDataSetChanged();

        }

        return false;

    }

    private class ReadFileRunnable implements Runnable{
        private static final String TAG = "ReadFileRunnable";
        @Override
        public void run() {
            File file = new File(Environment.
                    getExternalStoragePublicDirectory(Environment.DIRECTORY_MOVIES), VideoMediaMuxer.DIR_NAME);
            file.mkdirs();
            String[] files = file.list();
            Log.d(TAG, "run: "+files.length);
            ArrayList<VideoModel> models = new ArrayList<>();
            for(int i = 0;i<files.length;i++){
                String src = new File(file,files[i]).toString();
                String time = files[i];
                models.add(new VideoModel(src,time));
            }

            Bundle bundle = new Bundle();
            bundle.putParcelableArrayList("data",models);
            Message message = new Message();
            message.what = RESPONSE_DATA;
            message.setData(bundle);
            mHandler.sendMessage(message);
        }
    }
}
