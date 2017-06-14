package com.joy.http;

/**
 * Created by KEVIN.DAI on 15/11/8.
 */
public interface ResponseListener<T> {

    void onSuccess(T t);

    void onError(Throwable error);
}
