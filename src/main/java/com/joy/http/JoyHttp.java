package com.joy.http;

import android.content.Context;

import com.android.volley.Cache;
import com.android.volley.VolleyLog;
import com.joy.http.qyer.QyerReqFactory;
import com.joy.http.volley.RetroRequestQueue;
import com.joy.http.volley.RetroVolley;

import java.util.Map;

/**
 * Created by Daisw on 16/9/8.
 */
public class JoyHttp {

    private static volatile JoyHttp mJoyHttp;
    private RetroRequestQueue mReqQueue;

    private JoyHttp(Context appContext, boolean debug) {
        VolleyLog.DEBUG = debug;
        mReqQueue = RetroVolley.newRequestQueue(appContext);
//        mReqQueue.addRequestFinishedListener(mReqFinishLis);
    }

    public static void initialize(Context appContext, boolean debug) {
        if (mJoyHttp == null) {
            synchronized (JoyHttp.class) {
                if (mJoyHttp == null) {
                    mJoyHttp = new JoyHttp(appContext, debug);
                }
            }
        }
    }

    public static void initialize(Context appContext, Map<String, String> defaultParams, boolean debug) {
        QyerReqFactory.setDefaultParams(defaultParams);
        initialize(appContext, debug);
    }

    public static void shutDown() {
        if (mJoyHttp != null && mJoyHttp.mReqQueue != null) {
//            mReqQueue.removeRequestFinishedListener(mReqFinishLis);
            mJoyHttp.mReqQueue.cancelAll(request -> true);
            mJoyHttp.mReqQueue.stop();
            mJoyHttp.mReqQueue = null;
        }
        mJoyHttp = null;
    }

    public static RetroRequestQueue getLauncher() {
        return mJoyHttp == null ? null : mJoyHttp.mReqQueue;
    }

    public static Cache getVolleyCache() {
        if (mJoyHttp == null) {
            return null;
        }
        return mJoyHttp.mReqQueue == null ? null : mJoyHttp.mReqQueue.getCache();
    }

//    private static RequestQueue.RequestFinishedListener mReqFinishLis = request -> {
//        if (VolleyLog.DEBUG) {
//            VolleyLog.d("~~Global monitor # request finished. tag: %s, sequence number: %d", request.getTag(), request.getSequence());
//        }
//    };
}
