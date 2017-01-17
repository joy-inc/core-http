package com.joy.http;

import android.content.Context;

import com.android.volley.Cache;
import com.android.volley.RequestQueue;
import com.android.volley.VolleyLog;
import com.joy.http.qyer.QyerReqFactory;
import com.joy.http.volley.RetroRequestQueue;
import com.joy.http.volley.RetroVolley;

import java.util.Map;

/**
 * Created by Daisw on 16/9/8.
 */
public class JoyHttp {

    private static volatile RetroRequestQueue mReqQueue;
    private static Context mAppContext;

    private JoyHttp() {
    }

    public static void initialize(Context appContext, boolean debug) {
        if (mReqQueue == null) {
            synchronized (JoyHttp.class) {
                if (mReqQueue == null) {
                    mAppContext = appContext;
                    VolleyLog.DEBUG = debug;
                    mReqQueue = RetroVolley.newRequestQueue(appContext);
                    mReqQueue.addRequestFinishedListener(mReqFinishLis);
                }
            }
        }
    }

    public static void initialize(Context appContext, Map<String, String> defaultParams, boolean debug) {
        QyerReqFactory.setDefaultParams(defaultParams);
        initialize(appContext, debug);
    }

    public static void shutDown() {
        if (mReqQueue != null) {
            mReqQueue.removeRequestFinishedListener(mReqFinishLis);
            mReqQueue.cancelAll(request -> true);
            mReqQueue.stop();
            mReqQueue = null;
        }
        mAppContext = null;
        QyerReqFactory.clearDefaultParams();
    }

    public static Context getContext() {
        return mAppContext;
    }

    public static RetroRequestQueue getLauncher() {
        return mReqQueue;
    }

    public static Cache getVolleyCache() {
        return mReqQueue == null ? null : mReqQueue.getCache();
    }

    private static RequestQueue.RequestFinishedListener mReqFinishLis = request -> {
        if (VolleyLog.DEBUG) {
            VolleyLog.d("~~Global monitor # request finished. tag: %s, sequence number: %d", request.getTag(), request.getSequence());
        }
    };
}
