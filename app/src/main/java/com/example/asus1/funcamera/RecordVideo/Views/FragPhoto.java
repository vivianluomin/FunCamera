package com.example.asus1.funcamera.RecordVideo.Views;

import android.graphics.drawable.ShapeDrawable;

import com.example.asus1.funcamera.R;
import com.example.asus1.funcamera.Utils.Constant;
import com.example.asus1.funcamera.Utils.ShaderUtil;

public class FragPhoto extends Photo {

    public FragPhoto() {
        super();
    }



    @Override
    protected void initProgram() {
        String fragment = ShaderUtil.readShderFromAssets("frag.sh",Constant.GLOABLE_CONTXT);
        mProgram = ShaderUtil.loadProgram(mVertexShder,fragment);
        initFragmentData();
    }
}
