package com.example.asus1.funcamera.Utils;

import android.content.Context;
import android.net.http.SslError;
import android.os.Build;
import android.util.AttributeSet;
import android.view.View;
import android.webkit.SslErrorHandler;
import android.webkit.ValueCallback;
import android.webkit.WebChromeClient;
import android.webkit.WebResourceError;
import android.webkit.WebResourceRequest;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import java.util.HashMap;

public class JSSecretWebView extends WebView {

    private WebChromeClient chromeClient;
    private WebSettings mSettings;

    public JSSecretWebView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public JSSecretWebView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    public JSSecretWebView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init();
    }

    public JSSecretWebView(Context context) {
        super(context);
        init();
    }
    private void init(){


    }


    public void getParams(final String id){
        HashMap<String,String> map = new HashMap<>();
        System.out.println("111111111111111111111");
        post(new Runnable() {
            @Override
            public void run() {
               evaluateJavascript("javascript:myFunc("+id+")", new ValueCallback<String>() {
                   @Override
                   public void onReceiveValue(String value) {
                       System.out.println("+++++++++++++");
                       System.out.print("--------------------"+value);
                   }
               });
            }
        });

    }


}
