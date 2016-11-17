package com.joy.http.utils;

import java.util.Map;

/**
 * Created by KEVIN.DAI on 15/11/26.
 */
public class ParamsUtil {

    public static String createUrl(Map<String, String> params) {
        if (params == null || params.isEmpty())
            return "";
        StringBuilder sb = new StringBuilder();
        for (Map.Entry<String, String> entry : params.entrySet()) {
            sb.append(entry.getKey());
            sb.append('=');
            sb.append(entry.getValue());
            sb.append('&');
        }
        sb.deleteCharAt(sb.length() - 1);
        return sb.toString();
    }
}
