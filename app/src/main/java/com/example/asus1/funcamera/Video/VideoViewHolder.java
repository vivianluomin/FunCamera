package com.example.asus1.funcamera.Video;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.MediaCodec;
import android.media.MediaMetadataRetriever;
import android.media.ThumbnailUtils;
import android.provider.MediaStore;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.asus1.funcamera.R;

public class VideoViewHolder extends RecyclerView.ViewHolder {

    private ImageView mCover;
    private String mData;
    private TextView mTime;
    private Context mContext;

    public VideoViewHolder(Context context, View itemView) {
        super(itemView);
        mContext = context;
        mCover = (ImageView)itemView.findViewById(R.id.iv_video_cover);
        mTime = (TextView)itemView.findViewById(R.id.tv_time);
        mCover.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(mContext,PlayVideoActivity.class);
                intent.putExtra("src",mData);
                mContext.startActivity(intent);
            }
        });
    }

    public void setData(VideoModel model){
        mData = model.getmSrc();
        mTime.setText(model.getmTime());
        mCover.setImageBitmap(model.getBitmap());

    }
}
