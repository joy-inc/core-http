package com.joy.http.volley;

import android.util.Log;

import com.joy.http.Progress;
import com.joy.http.volley.toolbox.ByteRequest;
import com.joy.http.volley.toolbox.DiskBasedCache;

import java.io.File;
import java.io.IOException;

/**
 * Created by Daisw on 2017/3/23.
 */

public class DownloadRequest extends ByteRequest<File> {

    private final File mRootDirectory;
    private final String mFilename;
    private File mStorageFile;

    public DownloadRequest(Method method, String url, boolean isProgress, File rootDirectory, String filename) {
        super(method, url, isProgress);
        mRootDirectory = rootDirectory;
        mFilename = filename;
    }

    public DownloadRequest(Method method, String url, boolean isProgress, File rootDirectory) {
        this(method, url, isProgress, rootDirectory, DiskBasedCache.getFilenameForKey(method + ":" + url));
    }

    @Override
    public File getStorageFile() {
        if (mRootDirectory == null) {
            return null;
        }
        if (!mRootDirectory.exists()) {
            if (!mRootDirectory.mkdirs()) {
                return null;
            }
        }
        if (mStorageFile == null) {
            mStorageFile = new File(mRootDirectory, mFilename);
        }
        return mStorageFile;
    }

    @Override
    protected Result<Progress<File>> parseNetworkResponse(Response response) {
        Log.i("daisw", "DownloadRequest ## contentLength: " + response.contentLength);
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
        return Result.success(new Progress<>(mStorageFile));
    }
}
