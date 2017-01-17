package com.joy.http;

import rx.functions.Action1;

/**
 * Created by Daisw on 2016/12/22.
 */

public class JoyErrorAction implements Action1<Throwable> {

    @Override
    public void call(Throwable t) {
        if (t instanceof JoyError) {
            call((JoyError) t);
        }
    }

    public void call(JoyError error) {
    }
}
