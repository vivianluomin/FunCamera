package com.example.asus1.funcamera.Video;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.asus1.funcamera.R;

public class VideoViewHolder extends RecyclerView.ViewHolder {

    private ImageView mCover;
    private String mData;
    private TextView mTime;

    public VideoViewHolder(View itemView) {
        super(itemView);
        mCover = (ImageView)itemView.findViewById(R.id.iv_video_cover);
        mTime = (TextView)itemView.findViewById(R.id.tv_time);
        mCover.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
    }

    public void setData(VideoModel model){
        mCover.setImageBitmap(model.getmCover());
        mData = model.getmSrc();
        mTime.setText(model.getmTime());

    }
}
