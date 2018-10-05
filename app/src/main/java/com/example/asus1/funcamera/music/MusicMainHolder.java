package com.example.asus1.funcamera.music;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.example.asus1.funcamera.R;

import org.greenrobot.eventbus.EventBus;

public class MusicMainHolder extends RecyclerView.ViewHolder {

    private View mView;
    private TextView mTitle;
    private TextView mTime;
    private FrameLayout mFramLayout;
    private MusicModel mData;

    public MusicMainHolder(View itemView) {
        super(itemView);
        mView = itemView;
        mTitle = (TextView)mView.findViewById(R.id.tv_music_name);
        mTime = (TextView)mView.findViewById(R.id.tv_music_time);
        mFramLayout = (FrameLayout)mView.findViewById(R.id.framelayout);
        mFramLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(mData!=null){
                    EventBus.getDefault().post(new MusicMessage(mData.getmSrc()
                            ,mData.getmTime(),mData.getmTitle()));
                }

            }
        });
    }

    public void setData(MusicModel data){
        mTitle.setText(data.getmTitle());
        mTime.setText(data.getmTime());
        mData = data;

    }

}
