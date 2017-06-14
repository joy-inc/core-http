package com.joy.http.volley.toolbox;

import android.util.Log;

import com.joy.http.Progress;
import com.joy.http.volley.Response;
import com.joy.http.volley.Result;
import com.joy.http.volley.ServerError;

import java.io.File;
import java.io.IOException;

/**
 * Created by Daisw on 2017/6/6.
 */

public class ByteArrayRequest extends ByteRequest<byte[]> {

    public ByteArrayRequest(Method method, String url, boolean isProgress) {
        super(method, url, isProgress);
    }

    public ByteArrayRequest(String url, boolean isProgress) {
        this(Method.GET, url, isProgress);
    }

    @Override
    public File getStorageFile() {
        return null;
    }

    @Override
    protected Result<Progress<byte[]>> parseNetworkResponse(Response response) {
        Log.i("daisw", "ImageRequest ## contentLength: " + response.contentLength);
        byte[] dataByteArray;
        long startTime = System.currentTimeMillis();
        try {
            dataByteArray = toByteArray(response);
        } catch (IOException e) {
            e.printStackTrace();
            return Result.error(e);
        } catch (ServerError e) {
            e.printStackTrace();
            return Result.error(e);
        }
        Log.i("daisw", "=====Time: " + (System.currentTimeMillis() - startTime) + "ms");
        if (dataByteArray == null) {
            return Result.error(new NullPointerException("the byte array of image data is null."));
        }
        Log.i("daisw", "======Size: " + dataByteArray.length);
        return Result.success(new Progress<>(dataByteArray));
    }
}
