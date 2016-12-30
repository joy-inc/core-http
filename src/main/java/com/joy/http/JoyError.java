package com.joy.http;

/**
 * Created by Daisw on 2016/12/22.
 */

public class JoyError extends Throwable {

    public static final int STATUS_NONE = -1;
    private int statusCode;

    public JoyError(int statusCode, String message) {
        super(message);
        this.statusCode = statusCode;
    }

    public static JoyError empty() {
        return new JoyError(STATUS_NONE, "");
    }

    public JoyError(String message) {
        super(message);
    }

    public int getStatusCode() {
        return this.statusCode;
    }
}
