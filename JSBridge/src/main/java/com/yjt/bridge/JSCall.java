package com.yjt.bridge;

import android.annotation.TargetApi;
import android.net.Uri;
import android.os.Build;
import android.text.TextUtils;
import android.webkit.WebView;

import com.yjt.bridge.constant.Constant;

import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public class JSCall {

    private String mClassName;
    private String mMethodName;
    private String mPort;
    private JSONObject mParameters;

    private JSCall() {
    }

    public static JSCall newInstance() {
        return new JSCall();
    }

    public void call(WebView webView, String message) {
        if (webView == null || TextUtils.isEmpty(message)) {
            return;
        }
        parseMessage(message);
        invokeNativeMethod(webView);
    }

    private void parseMessage(String message) {
        if (!message.startsWith(Constant.PROTOCOL_SCHEMA)) {
            return;
        }
        Uri uri = Uri.parse(message);
        mClassName = uri.getHost();
        String path = uri.getPath();
        if (!TextUtils.isEmpty(path)) {
            mMethodName = path.replace("/", "");
        } else {
            mMethodName = "";
        }
        mPort = String.valueOf(uri.getPort());
        try {
            mParameters = new JSONObject(uri.getQuery());
        } catch (JSONException e) {
            e.printStackTrace();
            mParameters = new JSONObject();
        }
    }

    @TargetApi(Build.VERSION_CODES.KITKAT)
    private void invokeNativeMethod(WebView webView) {
        Method method = Inject.getInstance().getMethod(mClassName, mMethodName);
        JSCallBack callBack = JSCallBack.newInstance(webView, mPort);
        if (method == null) {
            JSCallBack.invokeJSCallBack(callBack, false, null, "Method (" + mMethodName + ") in this class (" + mClassName + ") not found!");
            return;
        }
        Object[] objects = new Object[3];
        objects[0] = webView;
        objects[1] = mParameters;
        objects[2] = callBack;
        try {
            method.invoke(null, objects);
        } catch (IllegalAccessException | InvocationTargetException e) {
            e.printStackTrace();
        }
    }

}
