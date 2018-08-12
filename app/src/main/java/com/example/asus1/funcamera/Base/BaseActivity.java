package com.example.asus1.funcamera.Base;

import android.Manifest;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Toast;

import com.example.asus1.funcamera.R;

import java.util.ArrayList;
import java.util.List;

public class BaseActivity extends AppCompatActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP){
            Window window = getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);

        }else{

            Window window = getWindow();
            View decorView = window.getDecorView();
            decorView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                        |View.SYSTEM_UI_FLAG_LAYOUT_STABLE);
            window.setStatusBarColor(Color.TRANSPARENT);
        }

        setPermission();
    }

    private void setPermission(){
        String[] permissions = new String[]{
                Manifest.permission.CAMERA,
                Manifest.permission.WRITE_EXTERNAL_STORAGE,
                Manifest.permission.READ_EXTERNAL_STORAGE
        };
        List<String> pers = new ArrayList<>();

        for(String per :permissions){
            if(ContextCompat.checkSelfPermission(this,per)
                    != PackageManager.PERMISSION_GRANTED){
                pers.add(per);
            }
        }

        if(pers.size()>0){
            ActivityCompat.requestPermissions(this,pers.toArray(new String[pers.size()]),
                    100);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        for(int i = 0;i<grantResults.length;i++){
            if(grantResults[i]!= PackageManager.PERMISSION_GRANTED){
                Toast.makeText(this,getResources().
                        getText(R.string.requestPermission),Toast.LENGTH_SHORT).show();
            }
        }
    }
}
