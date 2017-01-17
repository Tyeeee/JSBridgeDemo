package com.yjt.bridge.thread;

import android.os.Handler;
import android.os.Looper;
import android.os.Process;

import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class Executor {

    private static final ThreadPoolExecutor mExecutor;

    static {
        mExecutor = new ThreadPoolExecutor(
                3,
                3,
                0L,
                TimeUnit.MILLISECONDS,
                new LinkedBlockingQueue<Runnable>(),
                new ThreadFactory() {
                    @Override
                    public Thread newThread(final Runnable runnable) {
                        Thread thread = new Thread(new Runnable() {
                            @Override
                            public void run() {
                                Process.setThreadPriority(Process.THREAD_PRIORITY_BACKGROUND);
                                runnable.run();
                            }
                        }, "Executor");
                        if (thread.isDaemon()) {
                            thread.setDaemon(false);
                        }
                        return thread;
                    }
                });
    }

    public static void runOnAsyncThread(Runnable runnable) {
        if (runnable == null) {
            return;
        }
        mExecutor.execute(runnable);
    }

    public static void runOnMainThread(Runnable runnable) {
        if (runnable == null) {
            return;
        }
        new Handler(Looper.getMainLooper()).post(runnable);
    }

    public static boolean isMainThread() {
        return Looper.myLooper() == Looper.getMainLooper();
    }

    public static void shutDown() {
        if (mExecutor != null && !mExecutor.isShutdown() && !mExecutor.isTerminating()) {
            mExecutor.shutdown();
        }
    }
}
