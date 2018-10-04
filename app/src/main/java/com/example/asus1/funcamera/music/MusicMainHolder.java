package com.example.asus1.funcamera.music;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.example.asus1.funcamera.R;

public class MusicMainHolder extends RecyclerView.ViewHolder {

    private View mView;
    private TextView mTitle;
    private TextView mTime;

    public MusicMainHolder(View itemView) {
        super(itemView);
        mView = itemView;
        mTitle = (TextView)mView.findViewById(R.id.tv_music_name);
        mTime = (TextView)mView.findViewById(R.id.tv_music_time);
    }

    public void setData(MusicModel data){
        mTitle.setText(data.getmTitle());
        mTime.setText(data.getmTime());

    }

}
