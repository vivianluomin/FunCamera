package com.example.asus1.funcamera.music;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.asus1.funcamera.R;

import org.greenrobot.eventbus.EventBus;
import org.w3c.dom.Text;

public class MusicLeftHolder extends RecyclerView.ViewHolder {

    private ImageView mIcon;
    private TextView mTitle;
    private View mView;
    private Context mContext;
    private FrameLayout mFramLayout;
    private MusicTypeModel mData;

    public MusicLeftHolder(View itemView,Context context) {
        super(itemView);
        mView = itemView;
        mContext = context;
        mIcon = (ImageView)mView.findViewById(R.id.iv_image);
        mTitle = (TextView)mView.findViewById(R.id.tv_title);

        mFramLayout = (FrameLayout)mView.findViewById(R.id.framelayout);
        mFramLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(mData!=null){

                    EventBus.getDefault().post(new ChangeMessage(mData.getmSrc(),mData.getmTitle()));
                }
            }
        });
    }

    public void setData(MusicTypeModel data){
        Glide.with(mContext)
                .load(data.getmIcon())
                .into(mIcon);
        mTitle.setText(data.getmTitle());
        mData = data;
    }
}
