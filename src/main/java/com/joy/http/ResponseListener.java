package com.joy.http;

/**
 * Created by KEVIN.DAI on 15/11/8.
 */
public interface ResponseListener<T> {

    void onSuccess(Object tag, T t);

    void onError(Object tag, JoyError error);
}
