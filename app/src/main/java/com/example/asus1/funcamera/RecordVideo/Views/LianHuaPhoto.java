package com.example.asus1.funcamera.RecordVideo.Views;

import com.example.asus1.funcamera.Utils.Constant;
import com.example.asus1.funcamera.Utils.ShaderUtil;

public class LianHuaPhoto extends Photo {

    @Override
    protected void initProgram() {
        mProgram = ShaderUtil.loadProgram(mVertexShder,ShaderUtil.
                readShderFromAssets("lianhuanhua.sh", Constant.GLOABLE_CONTXT));
        initFragmentData();
    }
}
