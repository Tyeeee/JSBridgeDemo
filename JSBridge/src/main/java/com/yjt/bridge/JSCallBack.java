package com.yjt.bridge;

import android.text.TextUtils;
import android.webkit.WebView;

import com.yjt.bridge.constant.Constant;
import com.yjt.bridge.exception.JSCallbackException;
import com.yjt.bridge.thread.Executor;

import org.json.JSONException;
import org.json.JSONObject;

import java.lang.ref.WeakReference;
import java.util.Locale;

public class JSCallBack {

    private WeakReference<WebView> mWebViews;
    private String mPort;

    private JSCallBack(WebView webView, String port) {
        this.mWebViews = new WeakReference<>(webView);
        this.mPort = port;
    }

    public static JSCallBack newInstance(WebView webView, String port) {
        return new JSCallBack(webView, port);
    }

    private void call(boolean isInvokeSuccess, JSONObject resultData, String message) throws JSCallbackException {
        final WebView webView = mWebViews.get();
        if (webView == null)
            throw new JSCallbackException("The WebView related to the JsCallback has been recycled!");
        JSONObject object = new JSONObject();
        JSONObject status = new JSONObject();
        try {
            status.put("code", isInvokeSuccess ? 0 : 1);
            if (!TextUtils.isEmpty(message)) {
                status.put("message", message);
            } else {
                status.put("message", "");
            }
            object.put("status", status);
            if (resultData != null) {
                object.put("data", resultData);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        final String format = String.format(Locale.getDefault(), Constant.JS_FORMAT, mPort, object.toString());
        if (Executor.isMainThread()) {
            webView.loadUrl(format);
        } else {
            Executor.runOnMainThread(new Runnable() {
                @Override
                public void run() {
                    webView.loadUrl(format);
                }
            });
        }
    }

    public static void invokeJSCallBack(JSCallBack callback, boolean isInvokeSuccess, JSONObject resultData, String statusMsg) {
        if (callback == null)
            return;
        try {
            callback.call(isInvokeSuccess, resultData, statusMsg);
        } catch (JSCallbackException e) {
            e.printStackTrace();
        }
    }
}
