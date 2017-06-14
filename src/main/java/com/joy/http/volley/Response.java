package com.joy.http.volley;

import java.io.InputStream;

/**
 * Created by Daisw on 2017/4/20.
 */

public class Response {

    public final InputStream data;
    public final long contentLength;

    public Response(InputStream data, long contentLength) {
        this.data = data;
        this.contentLength = contentLength;
    }
}
