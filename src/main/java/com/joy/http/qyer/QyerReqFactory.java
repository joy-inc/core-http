package com.joy.http.qyer;

import com.joy.http.ReqFactory;
import com.joy.http.utils.ParamsUtil;

import java.util.Map;

/**
 * Created by Daisw on 16/9/14.
 */

public class QyerReqFactory {

    private static Map<String, String> mDefaultParams;

    public static void setDefaultParams(Map<String, String> defaultParams) {
        mDefaultParams = defaultParams;
    }

    public static Map<String, String> getDefaultParams() {
        return mDefaultParams;
    }

    public static void clearDefaultParams() {
        if (mDefaultParams != null) {
            mDefaultParams.clear();
            mDefaultParams = null;
        }
    }

    public static Map<String, String> generateParams(Map<String, String> params) {
        if (mDefaultParams != null) {
            params.putAll(mDefaultParams);
        }
        return params;
    }

    /**
     * @param baseUrl
     * @param clazz
     * @param params  length = [0,2]
     * @param <T>
     * @return
     */
    public static <T> QyerRequest<T> newGet(String baseUrl, Class<T> clazz, Map<String, String>... params) {
        ReqFactory.checkParamsIsValid(params);
        StringBuilder sb = new StringBuilder(baseUrl);
        if (ReqFactory.isParamsSingle(params)) {
            sb.append('?').append(ParamsUtil.createUrl(generateParams(params[0])));
        } else if (ReqFactory.isParamsDouble(params)) {
            String fullUrl = sb.append('?').append(ParamsUtil.createUrl(generateParams(params[0]))).toString();
            QyerRequest<T> req = QyerRequest.get(fullUrl, clazz);
            req.setHeaders(params[1]);
            return req;
        }
        return QyerRequest.get(sb.toString(), clazz);
    }

    /**
     * @param baseUrl
     * @param clazz
     * @param params  length = [0,2]
     * @param <T>
     * @return
     */
    public static <T> QyerRequest<T> newPost(String baseUrl, Class clazz, Map<String, String>... params) {
        ReqFactory.checkParamsIsValid(params);
        QyerRequest<T> req = QyerRequest.post(baseUrl, clazz);
        if (ReqFactory.isParamsSingle(params)) {
            req.setParams(generateParams(params[0]));
        } else if (ReqFactory.isParamsDouble(params)) {
            req.setParams(generateParams(params[0]));
            req.setHeaders(params[1]);
        }
        return req;
    }
}
