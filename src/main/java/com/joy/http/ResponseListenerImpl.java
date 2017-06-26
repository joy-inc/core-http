package com.joy.http;

/**
 * Created by Daisw on 2017/1/17.
 */

public class ResponseListenerImpl<T> implements ResponseListener<T> {

    @Override
    public void onSuccess(Object tag, T t) {
    }

    @Override
    public void onError(Object tag, Throwable error) {
        if (error instanceof JoyError) {
            onError(tag, (JoyError) error);
        }
    }

    public void onError(Object tag, JoyError error) {
    }
}
