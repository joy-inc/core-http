package com.joy.http;

/**
 * Created by Daisw on 2017/5/25.
 */

public class JoyError extends Exception {

    /**
     * 错误类型：区别于statusCode，用来标识当前错误的类型（接口定义的错误、HTTP错误）。
     */
    public static final int TYPE_NONE = 0;
    public static final int TYPE_HTTP = 1;
    public static final int TYPE_SERVER_DEFINED = 2;
    private int mType = TYPE_NONE;

    /**
     * 状态码：HTTP状态码（4xx、5xx等）；服务器定义的状态码用来标识Token失效、参数错误等。
     * 需要用type来区分。
     */
    private int mStatusCode;

    public JoyError() {
        super();
    }

    public JoyError(int type, int statusCode, String message) {
        super(message);
        mType = type;
        mStatusCode = statusCode;
    }

    public JoyError(int type, int statusCode) {
        this(type, statusCode, "");
    }

    public JoyError(int type, int statusCode, Throwable cause) {
        super(cause);
        mType = type;
        mStatusCode = statusCode;
    }

    public JoyError(String message) {
        super(message);
    }

    public JoyError(String message, Throwable cause) {
        super(message, cause);
    }

    public JoyError(Throwable cause) {
        super(cause);
    }

    public int getType() {
        return mType;
    }

    public int getStatusCode() {
        return mStatusCode;
    }

    public boolean isHttpError() {
        return mType == TYPE_HTTP;
    }

    public boolean isServerDefinedError() {
        return mType == TYPE_SERVER_DEFINED;
    }

    private boolean isCancelCaused;

    public void setCancelCaused(boolean cancelCaused) {
        isCancelCaused = cancelCaused;
    }

    public boolean isCancelCaused() {
        return isCancelCaused;
    }

    @Override
    public String toString() {
        return "JoyError{" +
                "type=" + mType +
                ", statusCode=" + mStatusCode +
                ", isCancelCaused=" + isCancelCaused +
                '}';
    }
}
