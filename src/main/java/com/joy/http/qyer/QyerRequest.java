package com.joy.http.qyer;

import android.text.TextUtils;
import android.util.Log;

import com.joy.http.JoyError;
import com.joy.http.volley.ObjectRequest;
import com.joy.http.volley.Response;
import com.joy.http.volley.Result;
import com.joy.http.volley.ServerError;
import com.joy.http.volley.VolleyLog;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import static com.joy.http.JoyError.TYPE_SERVER_DEFINED;

/**
 * Created by Daisw on 2016/12/29.
 */

public class QyerRequest<T> extends ObjectRequest<T> {

    public QyerRequest(Method method, String url, Class<?> clazz) {
        super(method, url, clazz);
    }

    @Override
    public Result<T> parseNetworkResponse(Response response) {
        if (VolleyLog.DEBUG) {
            Log.i(VolleyLog.TAG, "QyerRequest ## contentLength: " + response.contentLength);
        }
        try {
            long startTime = System.currentTimeMillis();
            String json = toString(response);
            if (VolleyLog.DEBUG) {
                Log.i(VolleyLog.TAG, "QyerRequest ## spent time: " + (System.currentTimeMillis() - startTime) + "ms");
            }
            if (json == null) {
                return Result.error(new NullPointerException("the json string is null."));
            }
            QyerResponse<T> qyerResp = onResponse(json);
            if (qyerResp.isSuccess()) {
                return Result.success(qyerResp.getData());
            } else {
                return Result.error(new JoyError(TYPE_SERVER_DEFINED,
                        qyerResp.getStatus(), qyerResp.getMsg()));
            }
        } catch (IOException e) {
            e.printStackTrace();
            return Result.error(e);
        } catch (ServerError e) {
            e.printStackTrace();
            return Result.error(e);
        } catch (JSONException e) {
            e.printStackTrace();
            return Result.error(e);
        } catch (IllegalAccessException e) {
            e.printStackTrace();
            return Result.error(e);
        } catch (InstantiationException e) {
            e.printStackTrace();
            return Result.error(e);
        }
    }

    protected QyerResponse<T> onResponse(String json) throws JSONException, IllegalAccessException, InstantiationException {
        QyerResponse<T> resp = new QyerResponse();
        if (TextUtils.isEmpty(json)) {
            return resp;
        }
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
        return resp;
    }
}
