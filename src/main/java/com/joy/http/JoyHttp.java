package com.joy.http;

import android.content.Context;

import com.joy.http.qyer.QyerReqFactory;
import com.joy.http.volley.Request;
import com.joy.http.volley.RequestLauncher;
import com.joy.http.volley.VolleyLog;
import com.joy.http.volley.toolbox.Volley;

import java.util.Map;

/**
 * Created by Daisw on 16/9/8.
 */
public class JoyHttp {

    private static volatile RequestLauncher mLauncher;

    private static final int DEFAULT_TIMEOUT_MS = 10 * 1000;
    private static final int DEFAULT_MAX_RETRIES = 0;
    private static int mTimeoutMs = DEFAULT_TIMEOUT_MS;
    private static int mRetryCount = DEFAULT_MAX_RETRIES;

    private JoyHttp() {
    }

    public static void initialize(Context appContext, boolean debug) {
        if (mLauncher == null) {
            synchronized (JoyHttp.class) {
                if (mLauncher == null) {
                    VolleyLog.DEBUG = debug;
                    mLauncher = Volley.newLauncher(appContext);
//                    mLauncher.addRequestFinishedListener(mReqFinishLis);
                }
            }
        }
    }

    public static void initialize(Context appContext, Map<String, String> defaultParams, boolean debug) {
        QyerReqFactory.setDefaultParams(defaultParams);
        initialize(appContext, debug);
    }

    public static void shutDown() {
        if (mLauncher != null) {
//            mLauncher.removeRequestFinishedListener(mReqFinishLis);
            mLauncher.abortAll();
            mLauncher.stop();
            mLauncher = null;
        }
        QyerReqFactory.clearDefaultParams();
    }

    public static RequestLauncher getLauncher() {
        return mLauncher;
    }

//    private static RequestQueue.RequestFinishedListener mReqFinishLis = request -> {
//        if (VolleyLog.DEBUG) {
//            VolleyLog.d("~~Global monitor # request finished. tag: %s, sequence number: %d", request.getTag(), request.getSequence());
//        }
//    };

    public static void setTimeoutMs(int timeoutMs) {
        mTimeoutMs = timeoutMs;
    }

    public static int getTimeoutMs() {
        return mTimeoutMs;
    }

    public static void setRetryCount(int retryCount) {
        mRetryCount = retryCount;
    }

    public static int getRetryCount() {
        return mRetryCount;
    }

    public static boolean isRequestLaunched(Object tag) {
        return mLauncher != null && mLauncher.isLaunched(tag);
    }

    /**
     * Cancels all requests in this queue for which the given filter applies.
     *
     * @param filter The filtering function to use
     */
    public static void abort(RequestLauncher.RequestFilter filter) {
        if (mLauncher != null) {
            mLauncher.abort(filter);
        }
    }

    /**
     * Cancels all requests in this queue with the given tag. Tag must be non-null
     * and equality is by identity.
     */
    public static void abort(Object tag) {
        if (mLauncher != null) {
            mLauncher.abort(tag);
        }
    }

    public static void abortAll() {
        if (mLauncher != null) {
            mLauncher.abortAll();
        }
    }

    public static void abort(Request<?> request) {
        if (mLauncher != null) {
            mLauncher.abort(request);
        }
    }
}
