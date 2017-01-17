package com.demo.bridge;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.webkit.WebSettings;
import android.webkit.WebView;

import com.yjt.bridge.JSBridge;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        WebView webView = (WebView) findViewById(R.id.webView);
        WebSettings settings = webView.getSettings();
        settings.setJavaScriptEnabled(true);
        JSBridge.getInstance().setInjectClass(JSProxy.class).inject();
        webView.setWebChromeClient(new JSBridgeWebviewClient());
        webView.loadUrl("file:///android_asset/test.html");
    }
}
