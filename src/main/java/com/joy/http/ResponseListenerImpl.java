package com.joy.http;

/**
 * Created by Daisw on 2017/1/17.
 */

public class ResponseListenerImpl<T> implements ResponseListener<T> {

    @Override
    public void onSuccess(T t) {
    }

    @Override
    public void onError(Throwable error) {
        if (error instanceof JoyError) {
            onError((JoyError) error);
        }
    }

    public void onError(JoyError error) {
    }
}
