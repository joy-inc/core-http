package com.joy.http;

import android.content.Context;

import com.android.volley.Cache;
import com.android.volley.RequestQueue;
import com.android.volley.VolleyLog;
import com.joy.http.volley.RetroRequestQueue;
import com.joy.http.volley.RetroVolley;

/**
 * Created by Daisw on 16/9/8.
 */
public class JoyHttp {

    /**
     * Global request queue for Volley.
     */
    private static RetroRequestQueue mReqQueue;

    public static void initialize(Context context, boolean debug) {

        initVolley(context);
        VolleyLog.DEBUG = debug;
    }

    public static void shutDown() {

        releaseVolley();
    }

    /**
     * the queue will be created if it is null.
     */
    private static void initVolley(Context context) {

        if (mReqQueue == null) {

            mReqQueue = RetroVolley.newRequestQueue(context);
            mReqQueue.addRequestFinishedListener(mReqFinishLis);
        }
    }

    private static void releaseVolley() {

        if (mReqQueue != null) {

            mReqQueue.removeRequestFinishedListener(mReqFinishLis);
            mReqQueue.cancelAll(request -> true);
//            mReqQueue.stop();
//            mReqQueue = null;
        }
    }

    /**
     * @return The Volley Request queue.
     */
    public static RetroRequestQueue getLauncher() {

        return mReqQueue;
    }

    public static Cache getVolleyCache() {

        return mReqQueue == null ? null : mReqQueue.getCache();
    }

    private static RequestQueue.RequestFinishedListener mReqFinishLis = request -> {

        if (VolleyLog.DEBUG)
            VolleyLog.d("~~request finished. tag: " + request.getTag() + ", sequence number: " + request.getSequence());
    };
}
