package com.example.asus1.funcamera;

import android.media.MediaPlayer;
import android.net.http.SslError;
import android.provider.Settings;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.webkit.ConsoleMessage;
import android.webkit.SslErrorHandler;
import android.webkit.WebChromeClient;
import android.webkit.WebResourceError;
import android.webkit.WebResourceRequest;
import android.webkit.WebResourceResponse;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.example.asus1.funcamera.Base.BaseActivity;
import com.example.asus1.funcamera.Utils.Constant;
import com.example.asus1.funcamera.Utils.JSSecretWebView;
import com.github.lzyzsd.jsbridge.BridgeWebView;
import com.github.lzyzsd.jsbridge.CallBackFunction;
import com.google.gson.Gson;
import com.google.gson.JsonObject;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.zip.GZIPInputStream;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.Interceptor;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class MainActivity extends BaseActivity {

    private String url = "http://interface.music.163.com/eapi/batch";
    private String param = "D797DF7B908FA3D1172C5C52214D6868F23E1F51A39FC5AC785FA0F1CF610D9B2CD8A12BE0320BC0B366E3FF0BC805A2ADD6884DFE6A6CE4F4C98642B706E9E6522676DC648FB86CC6375F11C8026CDF2B61D638BDF4FB74FCB75D8EF66D73A67AC02E6E99A857DDCA26280FA6744C16D3EBDD36046221D86FE6FE84C98F771986F271EBCDB7FD2D6A848234507C528A7877508F648A29E2D894A3FA28861CDD2144B7401EF0B3B4E18949B2193BE3348FF44A617F50649B3D4EDB88CF7A775EB76CC4E19CB78A2007A476B6CE028EBCD37F18970470672EB0D135805FFE4C43C038AC1AC23A507E78387E3E19175DBCB656F9DACDC6AE74E5B1002BE6751798DE234723CBE705B4BDD9905906C52D7C057051709C83D742BBEED946787DBC0BC1FF6F8D81FB6691F68678BF3CCFA8CB5B84720E7330168DAEE16E8EF2719B8979C224534DA9FBD2E81EEC394847166CD7862BAA83E5E9FB036AC6B829D0FD5A1CCFBFA929FA19D043CC5ED988E3924A839E695106F366B0D1B74386F02EFBD975D86BF4D07A8339B3AE79D73F8EA2223B7E8C7B15C6B1F0764A1CD298039BF6348D35EC4C9FF75A3EC0F2BE10818784E43EE533C734649C0328F0AF075540AAAD4FB89B360D6ACF7AC0664B65B206CD4E33E7EC70F61C7678A7B6646956119554FCDE7F193917C3FA5F1634DB503BB367AF5514B63DCCE6867F4F71B3745727968EBC83D791C1E8A6EB59472A59F5D01DAB337A01D312E2607B432240D1D42853A2CDF0F0F49D7D01A45D7DC985A9BD73FBD990DAD4F5184AFC83C27EA01F585A0B4E447FBC29C4F1F2DD4AC9FFC035049DD2FCE46FA779B637343C47E4B4FEC8BD155D2147A2C83475CC00E24E0EBE24A76D5E26DF2AB0CA1795BFC2CEE73A9F0FFEF7A510C2E7EA492AE93E9C0CA2CFB548F877D2D85F4C4BD4CF87AEA7B6";

    private String music= "http://music.163.com/outchain/player?type=2&id=1313898095&auto=1&height=66";

    private BridgeWebView mWebView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mWebView = (BridgeWebView)findViewById(R.id.web_view);
        mWebView.loadUrl("http://www.kugou.com/song/q84gx62.html");
        mWebView.setWebChromeClient(new WebChromeClient(){
            @Override
            public boolean onConsoleMessage(ConsoleMessage consoleMessage) {
                if(consoleMessage.message().contains(".mp3")){
                    String s = consoleMessage.message().split("audio file '")[1];
                    s = s.split("'.")[0];
                    System.out.println(s);
                }
                return super.onConsoleMessage(consoleMessage);
            }


        });
    }
}
