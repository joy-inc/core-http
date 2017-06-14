package com.joy.http.volley;

import java.io.InputStream;

/**
 * Created by Daisw on 2017/4/20.
 */

public class CacheResponse extends Response {

    public CacheResponse(InputStream data, long contentLength) {
        super(data, contentLength);
    }
}
