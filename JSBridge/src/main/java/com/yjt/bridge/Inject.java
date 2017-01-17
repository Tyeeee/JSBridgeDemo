package com.yjt.bridge;

import android.support.v4.util.ArrayMap;
import android.text.TextUtils;
import android.webkit.WebView;

import org.json.JSONObject;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.List;

public class Inject {

    private ArrayMap<String, ArrayMap<String, Method>> mClassInfo;
    private List<Class<?>> mInjectClasses;

    private volatile static Inject mInject;

    private Inject() {
        // cannot be instantiated
        mClassInfo = new ArrayMap<>();
        mInjectClasses = new ArrayList<>();
    }

    public static synchronized Inject getInstance() {
        if (mInject == null) {
            mInject = new Inject();
        }
        return mInject;
    }

    public static synchronized void releaseInstance() {
        if (mInject != null) {
            mInject = null;
        }
    }

    public Inject addInjectClass(Class<?> clazz) {
        if (clazz == null) {
            throw new NullPointerException("Inject:The clazz can not be null!");
        }
        mInjectClasses.add(clazz);
        return this;
    }

    public void inject() {
        int size = mInjectClasses.size();
        if (size != 0) {
            mClassInfo.clear();
            for (int i = 0; i < size; i++) {
                putMethod(mInjectClasses.get(i));
            }
            mInjectClasses.clear();
        }
    }


    public Method getMethod(String className, String methodName) {
        if (!TextUtils.isEmpty(className) && !TextUtils.isEmpty(methodName)) {
            if (mClassInfo.containsKey(className)) {
                ArrayMap<String, Method> arrayMap = mClassInfo.get(className);
                if (arrayMap != null && arrayMap.containsKey(methodName)) {
                    return arrayMap.get(methodName);
                }
                return null;
            }
        }
        return null;
    }

    private void putMethod(Class<?> clazz) {
        if (clazz != null) {
            ArrayMap<String, Method> arrayMap = new ArrayMap<>();
            for (Method method : clazz.getDeclaredMethods()) {
                if ((method.getModifiers() & Modifier.PUBLIC) != 0 
                        && (method.getModifiers() & Modifier.STATIC) != 0 
                        && !TextUtils.isEmpty(method.getName()) 
                        && method.getReturnType() == void.class) {
                    Class<?>[] parameterTypes = method.getParameterTypes();
                    if (parameterTypes != null && parameterTypes.length == 3) {
                        if (WebView.class == parameterTypes[0] && JSONObject.class == parameterTypes[1] && JSCallBack.class == parameterTypes[2]) {
                            arrayMap.put(method.getName(), method);
                        }
                    }
                }
            }
            mClassInfo.put(clazz.getSimpleName(), arrayMap);
        }
    }
}
