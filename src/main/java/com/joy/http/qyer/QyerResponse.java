package com.joy.http.qyer;

/**
 * Created by KEVIN.DAI on 15/7/16.
 *
 * @param <T>
 */
public class QyerResponse<T> {

    public static final int STATUS_SUCCESS = 1;// 1为正确
    public static final String STATUS = "status";
    public static final String MSG = "msg";
    public static final String INFO = "info";
    public static final String DATA = "data";

    /**
     * 1    正确
     * -9	track_client_id参数错误
     * -10	api_auth参数错误
     * -11	TOKEN过期
     * -12	用户未登录
     */
    private int status;// 错误状态
    private String msg = "";// 提示信息
    private T t;// 数据

    public QyerResponse() {
    }

    public void setMsg(String msg) {
        if (msg == null) {
            msg = "";
        } else {
            msg = msg.trim();
        }
        this.msg = msg;
    }

    public String getMsg() {
        return msg;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public int getStatus() {
        return status;
    }

    public void setData(T t) {
        this.t = t;
    }

    public T getData() {
        return t;
    }

    public boolean isSuccess() {
        return status == STATUS_SUCCESS;
    }

    public boolean isFailed() {
        return status != STATUS_SUCCESS;
    }
}
