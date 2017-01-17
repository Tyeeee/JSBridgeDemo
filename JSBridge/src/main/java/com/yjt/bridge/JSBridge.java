package com.yjt.bridge;

public class JSBridge {

    private volatile static JSBridge mJSBridge;

    private JSBridge() {
        // cannot be instantiated
    }

    public static synchronized JSBridge getInstance() {
        if (mJSBridge == null) {
            mJSBridge = new JSBridge();
        }
        return mJSBridge;
    }

    public static synchronized void releaseInstance() {
        if (mJSBridge != null) {
            mJSBridge = null;
        }
    }

    public Inject setInjectClass(Class<?> clazz) {
        return Inject.getInstance().addInjectClass(clazz);
    }
}
