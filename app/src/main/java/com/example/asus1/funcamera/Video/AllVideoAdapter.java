package com.example.asus1.funcamera.Video;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.asus1.funcamera.R;

import java.util.List;

public class AllVideoAdapter extends RecyclerView.Adapter<VideoViewHolder> {

    private List<VideoModel> mDatas;
    private Context mContext;

    public AllVideoAdapter(Context context,List<VideoModel> data) {
        this.mDatas = data;
        this.mContext = context;
    }

    @Override
    public VideoViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.layout_all_video_item,
                parent,false);
        return new VideoViewHolder(view);
    }

    @Override
    public void onBindViewHolder(VideoViewHolder holder, int position) {
        holder.setData(mDatas.get(position));
    }

    @Override
    public int getItemCount() {
        return mDatas.size();
    }
}
