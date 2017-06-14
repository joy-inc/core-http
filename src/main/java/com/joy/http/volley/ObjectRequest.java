package com.joy.http.volley;

import android.text.TextUtils;
import android.util.Log;

import com.alibaba.fastjson.JSON;
import com.joy.http.utils.ParamsUtil;
import com.joy.http.volley.toolbox.JsonRequest;

import java.io.IOException;
import java.util.Map;

/**
 * Created by KEVIN.DAI on 15/7/10.
 */
public class ObjectRequest<T> extends JsonRequest<T> {

    private Class<?> mClazz;
    private Map<String, String> mHeaders, mParams;
    private String mCacheKey;

    /**
     * Creates a new request with the given method.
     *
     * @param method the request {@link Method} to use
     * @param url    URL to fetch the Object
     * @param clazz  the Object class to return
     */
    public ObjectRequest(Method method, String url, Class<?> clazz) {
        super(method, url);
        mClazz = clazz;
    }

    public void setHeaders(Map<String, String> headers) {
        mHeaders = headers;
    }

    public void setParams(Map<String, String> params) {
        mParams = params;
    }

    @Override
    public Map<String, String> getHeaders() throws AuthFailureError {
        if (mHeaders != null && !mHeaders.isEmpty()) {
            return mHeaders;
        }
        return super.getHeaders();
    }

    @Override
    protected Map<String, String> getParams() throws AuthFailureError {
        if (mParams != null && !mParams.isEmpty()) {
            return mParams;
        }
        return null;
    }

    @Override
    protected void deliverResponse(T t) {
        super.deliverResponse(isTestMode() ? this.t : t);
    }

    @Override
    public void deliverError(Throwable e) {
        if (isTestMode()) {
            super.deliverResponse(this.t);
        } else {
            super.deliverError(e);
        }
    }

    @Override
    public Result<T> parseNetworkResponse(Response response) {
        Log.e("daisw", "ObjectRequest ## contentLength: " + response.contentLength);
        try {
            String json = toString(response);
            if (json == null) {
                return Result.error(new NullPointerException("the json string is null."));
            }
            return Result.success(shift(json));
        } catch (IOException e) {
            e.printStackTrace();
            return Result.error(e);
        } catch (ServerError e) {
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

    protected final T shift(String json) throws IllegalAccessException, InstantiationException {
        T t;
        if (TextUtils.isEmpty(json)) {
            t = (T) mClazz.newInstance();
        } else {
            if (json.startsWith("[")) {// JsonArray
                t = ((T) JSON.parseArray(json, mClazz));
            } else {// JsonObj
                t = (T) JSON.parseObject(json, mClazz);
            }
        }
        return t;
    }

    @Override
    protected void onFinish() {
        super.onFinish();
        t = null;
        mClazz = null;
        mCacheKey = null;
        if (mHeaders != null) {
            mHeaders.clear();
            mHeaders = null;
        }
        if (mParams != null) {
            mParams.clear();
            mParams = null;
        }
    }

    // --- for test data ---
    private T t;

    public void setTestData(T t) {
        this.t = t;
    }

    public void setTestData(String json) {
        try {
            setTestData(shift(json));
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InstantiationException e) {
            e.printStackTrace();
        }
    }

    private boolean isTestMode() {
        return this.t != null;
    }

    @Override
    public void cancel() {
        super.cancel();
        if (VolleyLog.DEBUG) {
            VolleyLog.d("~~Canceled # tag: %s", getTag());
        }
    }

    public void setCacheKey(String cacheKey) {
        mCacheKey = cacheKey;
    }

    @Override
    public String getCacheKey() {
        if (TextUtils.isEmpty(mCacheKey)) {
            if (getMethod() == Method.POST) {
                return Method.POST + ":" + getOriginUrl() + "?" + ParamsUtil.createUrl(mParams);
            } else {
                return super.getCacheKey();
            }
        } else {
            return mCacheKey;
        }
    }
}
