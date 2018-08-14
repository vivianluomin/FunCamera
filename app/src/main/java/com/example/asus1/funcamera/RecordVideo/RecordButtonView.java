package com.example.asus1.funcamera.RecordVideo;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.widget.GridLayout;

import com.example.asus1.funcamera.R;

public class RecordButtonView extends View  {

    private Context mContext;
    private Paint mInnerPaint;
    private Paint mOuterPaint;

    private int mInnerRadius = 5;
    private int mOuterRadius = 6;

    private final int mDefaultWidth = 300;
    private final int mDefaultHeight = 300;
    private final int mSepc = 15;

    private int mMaxRadius;
    private int mWidth;
    private int mHeight;
    private int mAlpa = 255;

    private boolean mClick = false;

    private static final String TAG = "RecordButtonView";

    public RecordButtonView(Context context) {
        this(context,null);
    }

    public RecordButtonView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs,0);
    }

    public RecordButtonView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        mContext = context;
        init();
    }

    private void init(){
        mInnerPaint = new Paint();
        mOuterPaint = new Paint();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {

        int width = MeasureSpec.getSize(widthMeasureSpec);
        int widthMode = MeasureSpec.getMode(widthMeasureSpec);
        int height = MeasureSpec.getSize(heightMeasureSpec);
        int heightMode = MeasureSpec.getMode(heightMeasureSpec);

        if(widthMode == MeasureSpec.AT_MOST){
            mWidth = mDefaultWidth;
        }else {
            mWidth = width;
        }

        if(heightMode == MeasureSpec.AT_MOST){
            mHeight = mDefaultWidth;
        }else {
            mHeight = height;
        }

        if(mWidth<mHeight){
            mInnerRadius = mWidth/3;
        }else {
            mInnerRadius = mHeight/3;
        }
        mMaxRadius = mWidth/2;
        mOuterRadius = mInnerRadius+mSepc/2;
        mInnerRadius = mInnerRadius- mSepc;
        setMeasuredDimension(mWidth,mHeight);

    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if(!mClick){
            mOuterPaint.setColor(mContext.getResources().getColor(R.color.white));
            mOuterPaint.setStyle(Paint.Style.FILL_AND_STROKE);
            canvas.drawCircle(mWidth/2,mHeight/2,mInnerRadius+mSepc,mOuterPaint);
            mInnerPaint.setColor(mContext.getResources().getColor(R.color.main_color));
            mInnerPaint.setStyle(Paint.Style.FILL_AND_STROKE);
            canvas.drawCircle(mWidth/2,mHeight/2,mInnerRadius,mInnerPaint);
        }else {
            mOuterPaint.setColor(mContext.getResources().getColor(R.color.main_color));
            mOuterPaint.setStyle(Paint.Style.STROKE);
            mOuterPaint.setAlpha(mAlpa);
            mOuterPaint.setStrokeWidth(mSepc);
            canvas.drawCircle(mWidth/2,mHeight/2,mOuterRadius,mOuterPaint);
            mOuterRadius = (mOuterRadius+mSepc/3);
            if(mOuterRadius >=mMaxRadius-mSepc/2){
                mOuterRadius = mInnerRadius+mSepc/2;
                mAlpa = 255;
            }
            mAlpa = mAlpa-mSepc;

            mInnerPaint.setColor(mContext.getResources().getColor(R.color.main_color));
            mInnerPaint.setStyle(Paint.Style.FILL_AND_STROKE);
            canvas.drawCircle(mWidth/2,mHeight/2,mInnerRadius+mSepc,mInnerPaint);
        }

    }

    public void setClick(boolean click){
        Log.d(TAG, "setClick: "+click);
        mClick = click;
    }

}
