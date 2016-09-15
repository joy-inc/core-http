package com.joy.http.volley;

import com.android.volley.VolleyError;

/**
 * Created by KEVIN.DAI on 15/11/8.
 */
public interface ObjectResponseListener<T> {

    void onSuccess(Object tag, T t);

    void onError(Object tag, VolleyError error);
}
