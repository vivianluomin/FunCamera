package com.example.asus1.funcamera.music;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.asus1.funcamera.R;

import java.util.List;

public class MusicMainRecyclerAdapter extends RecyclerView.Adapter<MusicMainHolder> {

    private Context mContext;
    private List<MusicModel> mDats;

    public MusicMainRecyclerAdapter(Context mContext, List<MusicModel> mDats) {
        this.mContext = mContext;
        this.mDats = mDats;
    }

    @Override
    public MusicMainHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(mContext).inflate(R.layout.layout_music_recyclerview_item,
                parent,false);
        return new MusicMainHolder(view);
    }

    @Override
    public void onBindViewHolder(MusicMainHolder holder, int position) {

        holder.setData(mDats.get(position));
    }

    @Override
    public int getItemCount() {
        return mDats.size();
    }
}
