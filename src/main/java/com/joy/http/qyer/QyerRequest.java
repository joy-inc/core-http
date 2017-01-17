package com.joy.http.qyer;

import android.text.TextUtils;

import com.android.volley.Cache;
import com.android.volley.NetworkResponse;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.HttpHeaderParser;
import com.joy.http.volley.ObjectRequest;

import org.json.JSONObject;

/**
 * Created by Daisw on 2016/12/29.
 */

public class QyerRequest<T> extends ObjectRequest<T> {

    protected QyerRequest(int method, String url, Class<?> clazz) {
        super(method, url, clazz);
    }

    /**
     * Creates a new GET request.
     *
     * @param url   URL to fetch the Object
     * @param clazz the Object class to return
     */
    public static <T> QyerRequest<T> get(String url, Class<?> clazz) {
        return new QyerRequest(Method.GET, url, clazz);
    }

    /**
     * Creates a new POST request.
     *
     * @param url   URL to fetch the Object
     * @param clazz the Object class to return
     */
    public static <T> QyerRequest<T> post(String url, Class<?> clazz) {
        return new QyerRequest(Method.POST, url, clazz);
    }

    @Override
    public Response<T> parseNetworkResponse(NetworkResponse response) {
        String parsed = parseJsonResponse(response);
        QyerResponse<T> qyerResp = onResponse(parsed);
        if (qyerResp.isSuccess()) {
            Cache.Entry entry = HttpHeaderParser.parseCacheHeaders(response);
            return mResponse = Response.success(qyerResp.getData(), entry);
        } else {
            NetworkResponse nr = new NetworkResponse(qyerResp.getStatus(), qyerResp.getMsg().getBytes(), response.headers, response.notModified, response.networkTimeMs);
            return Response.error(new VolleyError(nr));
        }
    }

    protected QyerResponse<T> onResponse(String json) {
        if (VolleyLog.DEBUG) {
            VolleyLog.d("~~onResponse # json: %s", json);
        }
        QyerResponse<T> resp = new QyerResponse();
        if (TextUtils.isEmpty(json)) {
            return resp;
        }
        try {
            JSONObject jsonObj = new JSONObject(json);
            if (jsonObj.has(QyerResponse.STATUS)) {
                resp.setStatus(jsonObj.getInt(QyerResponse.STATUS));
            }
            if (jsonObj.has(QyerResponse.MSG)) {
                resp.setMsg(jsonObj.getString(QyerResponse.MSG));
            } else if (jsonObj.has(QyerResponse.INFO)) {
                resp.setMsg(jsonObj.getString(QyerResponse.INFO));
            }
            if (resp.isSuccess()) {
                if (jsonObj.has(QyerResponse.DATA)) {
                    json = jsonObj.getString(QyerResponse.DATA);
                }
                resp.setData(shift(json));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return resp;
    }
}
