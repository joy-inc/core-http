package com.joy.http.volley.toolbox;

import android.os.Handler;
import android.os.Looper;

import com.joy.http.Progress;
import com.joy.http.volley.Cache;
import com.joy.http.volley.NetworkResponse;
import com.joy.http.volley.Request;
import com.joy.http.volley.Response;
import com.joy.http.volley.ServerError;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * Created by Daisw on 2017/6/4.
 */

public abstract class ByteRequest<T> extends Request<Progress<T>> {

    private int mCurProgress;
    private final boolean isProgress;
    private final Handler mPoster;

    public ByteRequest(Method method, String url, boolean isProgress) {
        super(method, url);
        this.isProgress = isProgress;
        this.mPoster = new Handler(Looper.getMainLooper());
    }

    public File getStorageFile() {
        return null;
    }

    public final Cache.Entry getStorageEntry() {
        File file = getStorageFile();
        if (file == null || !file.exists()) {
            return null;
        }
        try {
            Cache.Entry entry = new Cache.Entry();
            entry.contentLength = file.length();
            entry.data = new FileInputStream(file);
            return entry;
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            return null;
        }
    }

    public final byte[] toByteArray(Response response) throws IOException, ServerError {
        final InputStream is = response.data;
        if (is == null) {
            throw new ServerError();
        }
        final long contentLength = response.contentLength;

        BufferedInputStream bis = new BufferedInputStream(is);
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        byte[] buffer = new byte[8192];// 8k
        int n;
        long count = 0L;

        if (response instanceof NetworkResponse) {
            final File file = getStorageFile();
            BufferedOutputStream bos = null;
            if (file != null) {
                bos = new BufferedOutputStream(new FileOutputStream(file));
            }
            while ((n = bis.read(buffer)) != -1) {
                if (isCanceled()) {
                    bis.close();
                    if (bos != null) {
                        bos.close();
                    }
                    return null;
                }
                baos.write(buffer, 0, n);
                if (bos != null) {
                    bos.write(buffer, 0, n);
                }
                count += n;
                postResult(contentLength, count);
            }
            bis.close();
            if (bos != null) {
                bos.close();
                addMarker("network-cache-written");
            }
        } else {// CacheResponse
            while ((n = bis.read(buffer)) != -1) {
                if (isCanceled()) {
                    bis.close();
                    return null;
                }
                baos.write(buffer, 0, n);
                count += n;
                postResult(contentLength, count);
            }
            bis.close();
        }
        return baos.toByteArray();
    }

    private void postResult(long contentLength, long curLength) {
        if (isProgress && contentLength > 0 && curLength < contentLength) {
            final int curProgress = (int) (curLength / (float) contentLength * 100);
            if (curProgress > mCurProgress) {
                mCurProgress = curProgress;
                mPoster.post(() -> {
                    Progress<T> p = new Progress<>(curProgress);
                    if (mListener != null) {
                        mListener.onSuccess(getTag(), p);
                    }
                    mObserver.onNext(p);
                });
            }
        }
    }
}
