package com.joy.http.volley;

/**
 * Created by KEVIN.DAI on 15/7/16.
 *
 * @param <T>
 */
public class QyerResponse<T> {

    private static final int STATUS_PARSE_BROKEN = -10000;// 如果status为-10000，表明服务器返回JSON格式有误
    private static final int STATUS_SUCCESS = 1;// 1为正确

    /**
     * 1    正确
     * -9	track_client_id参数错误
     * -10	api_auth参数错误
     * -11	TOKEN过期
     * -12	用户未登录
     */
    private int status;// 错误状态
    private String msg = "";// 提示信息
    private T data;// 数据

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

    public void setParseBrokenStatus() {

        this.status = STATUS_PARSE_BROKEN;
    }

    public int getStatus() {

        return status;
    }

    public void setData(T data) {

        this.data = data;
    }

    public T getData() {

        return data;
    }

    public boolean isSuccess() {

        return this.status == STATUS_SUCCESS;
    }

    public boolean isFailed() {

        return this.status != STATUS_SUCCESS;
    }

    public boolean isParseBroken() {

        return this.status == STATUS_PARSE_BROKEN;
    }
}