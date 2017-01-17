package com.demo.bridge;

import android.util.Log;
import android.webkit.JsPromptResult;
import android.webkit.JsResult;
import android.webkit.WebChromeClient;
import android.webkit.WebView;

import com.yjt.bridge.JSCall;

public class JSBridgeWebviewClient extends WebChromeClient {

    @Override
    public boolean onJsPrompt(WebView view, String url, String message, String defaultValue, JsPromptResult result) {
        Log.i("WebChromeClient", "onJsPrompt");
        Log.i("url", url);
        Log.i("message", message);
        Log.i("defaultValue", defaultValue);
        result.confirm();
        JSCall.newInstance().call(view, message);
//        return super.onJsPrompt(view, url, message, defaultValue, result);
        return true;
    }

    @Override
    public boolean onJsAlert(WebView view, String url, String message, JsResult result) {
        Log.i("WebChromeClient", "onJsAlert");
        return super.onJsAlert(view, url, message, result);
    }

    @Override
    public boolean onJsConfirm(WebView view, String url, String message, JsResult result) {
        Log.i("WebChromeClient", "onJsConfirm");
        return super.onJsConfirm(view, url, message, result);
    }
}
