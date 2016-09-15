package com.joy.http.volley;

import android.text.TextUtils;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkError;
import com.android.volley.NoConnectionError;
import com.android.volley.ParseError;
import com.android.volley.ServerError;
import com.android.volley.TimeoutError;

/**
 * Created by KEVIN.DAI on 15/11/7.
 */
public class ErrorHelper {

    // --Volley的异常列表--
    // AuthFailureError：如果在做一个HTTP的身份验证，可能会发生这个错误。
    // NetworkError：Socket关闭，服务器宕机，DNS错误都会产生这个错误。
    // NoConnectionError：和NetworkError类似，这个是客户端没有网络连接。
    // ParseError：在使用JsonObjectRequest或JsonArrayRequest时，如果接收到的JSON是畸形，会产生异常。
    // ServerError：服务器的响应的一个错误，最有可能的4xx或5xx HTTP状态代码。
    // TimeoutError：Socket超时，服务器太忙或网络延迟会产生这个异常。默认情况下，Volley的超时时间为2.5秒。如果得到这个错误可以使用RetryPolicy。

    /**
     * @param error
     * @return Return generic message for errors
     */
    public static String getErrorType(Throwable error) {

        if (error == null)
            return "";

        String StringRes = "";
        if (error instanceof TimeoutError) {

            StringRes = "Server Timeout";
        } else if (error instanceof ServerError) {

            StringRes = "Server down";
        } else if (error instanceof AuthFailureError) {

            StringRes = "Authentication Failure";
        } else if (error instanceof NetworkError) {

            StringRes = "No internet";
        } else if (error instanceof NoConnectionError) {

            StringRes = "No network connection found";
        } else if (error instanceof ParseError) {

            StringRes = "Parsing Failure";
        }
//        return "No internet";
        return TextUtils.isEmpty(StringRes) ? error.getMessage() : StringRes;
    }

    /**
     * Determines whether the error is related to network
     *
     * @param error
     * @return
     */
    private static boolean isNetworkProblem(Throwable error) {

        return (error instanceof NetworkError) || (error instanceof NoConnectionError);
    }

    /**
     * Determines whether the error is related to server
     *
     * @param error
     * @return
     */
    private static boolean isServerProblem(Throwable error) {

        return (error instanceof ServerError) || (error instanceof AuthFailureError);
    }
}
