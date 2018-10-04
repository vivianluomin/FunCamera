package com.example.asus1.funcamera.music;

import android.content.Context;
import android.support.v7.widget.ActivityChooserView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.asus1.funcamera.R;
import com.example.asus1.funcamera.Utils.Constant;

import java.io.PipedOutputStream;
import java.util.List;

public class MusicLeftRecyclerAdapter extends RecyclerView.Adapter<MusicLeftHolder> {
    private Context mContext;
    private List<MusicTypeModel> mData;

    public MusicLeftRecyclerAdapter(Context context,List<MusicTypeModel> data) {

        mContext = context;
        mData = data;
    }

    @Override
    public MusicLeftHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.layout_music_recyclerview_left_item,parent,
                false);

        return new MusicLeftHolder(view,mContext);
    }

    @Override
    public void onBindViewHolder(MusicLeftHolder holder, int position) {
        holder.setData(mData.get(position));
    }

    @Override
    public int getItemCount() {
        return mData.size();
    }
}
