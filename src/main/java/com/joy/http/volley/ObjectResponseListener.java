package com.joy.http.volley;

import com.joy.http.JoyError;

/**
 * Created by KEVIN.DAI on 15/11/8.
 */
public interface ObjectResponseListener<T> {

    void onSuccess(Object tag, T t);

    void onError(Object tag, JoyError error);
}
