package com.example.asus1.funcamera.RecordVideo.Views;

import com.example.asus1.funcamera.Utils.Constant;
import com.example.asus1.funcamera.Utils.ShaderUtil;

public class FuDiaoPhoto extends Photo {

    public FuDiaoPhoto() {
        super();
    }

    @Override
    protected void initProgram() {
        mProgram = ShaderUtil.loadProgram(mVertexShder,ShaderUtil.
                readShderFromAssets("fudiao.sh", Constant.GLOABLE_CONTXT));
        initFragmentData();
    }
}
