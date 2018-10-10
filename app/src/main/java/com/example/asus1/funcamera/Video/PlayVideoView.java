package com.example.asus1.funcamera.Video;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.VideoView;

public class PlayVideoView extends VideoView {

    public PlayVideoView(Context context) {
        super(context);
    }

    public PlayVideoView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public PlayVideoView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int width = MeasureSpec.getSize(widthMeasureSpec);
        int height = MeasureSpec.getSize(heightMeasureSpec);
        setMeasuredDimension(width,height);
    }
}
