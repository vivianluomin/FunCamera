package com.example.asus1.funcamera.Video;

import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.media.MediaCodec;
import android.media.MediaMetadataRetriever;
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
        mData = model.getmSrc();
        mTime.setText(model.getmTime());
        MediaMetadataRetriever retriever = new MediaMetadataRetriever();
        retriever.setDataSource(mData);
        Bitmap bitmap = retriever.getFrameAtTime(0,MediaMetadataRetriever.OPTION_CLOSEST_SYNC);
        Matrix matrix = new Matrix();
        matrix.setScale(0.2f,0.3f);
        bitmap = Bitmap.createBitmap(bitmap,0,0,
                bitmap.getWidth(),bitmap.getHeight(),matrix,true);
        mCover.setImageBitmap(bitmap);

    }
}
