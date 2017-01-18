package com.joy.http;

/**
 * Created by Daisw on 2016/12/22.
 */

public class JoyError extends Throwable {

    /**
     * 错误类型，区别于statusCode，用来区分此错误是接口返回的自定义错误还是HTTP错误。
     */
    public static final int TYPE_NONE = 0;
    public static final int TYPE_HTTP = 1;
    public static final int TYPE_SERVER = 2;
    private int type;

    /**
     * 状态码，如4xx或5xx等HTTP状态码或服务器定义的状态码（Token失效、参数错误等），
     * 需要用type来区分
     */
    public static final int STATUS_NONE = 0;
    private int statusCode;

    public JoyError(int type, int statusCode, String message) {
        super(message);
        this.type = type;
        this.statusCode = statusCode;
    }

    public JoyError(String message) {
        super(message);
        this.type = TYPE_NONE;
        this.statusCode = STATUS_NONE;
    }

    public int getType() {
        return this.type;
    }

    public int getStatusCode() {
        return this.statusCode;
    }

    public boolean isHttpError() {
        return this.type == TYPE_HTTP;
    }

    public boolean isServerError() {
        return this.type == TYPE_HTTP;
    }

    public boolean isOtherErrors() {
        return !isHttpError() && !isServerError();
    }

    public boolean isStatusCodeValid() {
        return statusCode != STATUS_NONE;
    }
}
