package com.demo.bridge;

import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Handler;
import android.os.Looper;
import android.telephony.TelephonyManager;
import android.webkit.WebView;
import android.widget.Toast;

import com.yjt.bridge.JSCallBack;
import com.yjt.bridge.thread.Executor;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by zhengxiaoyong on 16/4/19.
 */
public class JSProxy {

    public static void showToast(WebView webView, JSONObject data, JSCallBack callback) {
        Toast.makeText(webView.getContext(), data.toString(), Toast.LENGTH_SHORT).show();
        JSCallBack.invokeJSCallBack(callback, true, null, null);
    }

    public static void getAppName(final WebView webView, JSONObject data, final JSCallBack callback) {
        String appName;
        try {
            PackageManager manager = webView.getContext().getApplicationContext().getPackageManager();
            appName = manager.getApplicationLabel(manager.getApplicationInfo(webView.getContext().getApplicationContext().getPackageName(), 0)).toString();
        } catch (PackageManager.NameNotFoundException e1) {
            e1.printStackTrace();
            appName = "";
        }
        JSONObject result = new JSONObject();
        try {
            result.put("result", appName);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        JSCallBack.invokeJSCallBack(callback, true, result, null);
    }

    public static void getDeviceId(final WebView webView, JSONObject data, final JSCallBack callback) {
        JSONObject result = new JSONObject();
        try {
            result.put("deivceId", ((TelephonyManager) webView.getContext().getSystemService(Context.TELEPHONY_SERVICE)).getDeviceId());
        } catch (JSONException e) {
            e.printStackTrace();
        }
        JSCallBack.invokeJSCallBack(callback, true, result, null);
    }


    public static void getOsSdk(WebView webView, JSONObject data, JSCallBack callback) {
        JSONObject result = new JSONObject();
        try {
            result.put("os_sdk", Build.VERSION.SDK_INT);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        JSCallBack.invokeJSCallBack(callback, true, result, null);
    }

    public static void delayExecuteTask(WebView webView, JSONObject data, final JSCallBack callback) {
        new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {
            @Override
            public void run() {
                JSONObject result = new JSONObject();
                try {
                    result.put("result", "延迟3s执行native方法");
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                JSCallBack.invokeJSCallBack(callback, true, result, null);
            }
        }, 3000);
    }

    public static void performTimeConsumeTask(WebView webView, JSONObject data, final JSCallBack callback) {
        Executor.runOnAsyncThread(new Runnable() {
            @Override
            public void run() {
                try {
                    Thread.sleep(3000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                JSONObject result = new JSONObject();
                try {
                    result.put("result", "执行耗时操作后的返回");
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                JSCallBack.invokeJSCallBack(callback, true, result, null);
            }
        });
    }

    public static void finish(WebView webView, JSONObject data, JSCallBack callback) {
        if (webView.getContext() instanceof Activity) {
            ((Activity) webView.getContext()).finish();
        }
    }

}
