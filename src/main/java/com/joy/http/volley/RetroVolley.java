package com.joy.http.volley;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Build;

import com.android.volley.Network;
import com.android.volley.toolbox.BasicNetwork;
import com.android.volley.toolbox.HttpStack;
import com.android.volley.toolbox.HurlStack;

import java.io.File;

//import android.net.http.AndroidHttpClient;

/**
 * Created by KEVIN.DAI on 15/11/28.
 */
public class RetroVolley {

    /**
     * Default on-disk cache directory.
     */
    private static final String DEFAULT_CACHE_DIR = "volley";

    /**
     * Creates a default instance of the worker pool and calls {@link RetroRequestQueue#start()} on it.
     * You may set a maximum size of the disk cache in bytes.
     *
     * @param context           A {@link Context} to use for creating the cache dir.
     * @param stack             An {@link HttpStack} to use for the network, or null for default.
     * @param maxDiskCacheBytes the maximum size of the disk cache, in bytes. Use -1 for default size.
     * @return A started {@link RetroRequestQueue} instance.
     */
    public static RetroRequestQueue newRequestQueue(Context context, HttpStack stack, int maxDiskCacheBytes) {
        File cacheDir = new File(context.getCacheDir(), DEFAULT_CACHE_DIR);

        String userAgent = "volley/0";
        try {
            String packageName = context.getPackageName();
            PackageInfo info = context.getPackageManager().getPackageInfo(packageName, 0);
            userAgent = packageName + "/" + info.versionCode;
        } catch (PackageManager.NameNotFoundException e) {
        }

        if (stack == null) {
            if (Build.VERSION.SDK_INT >= 9) {
                stack = new HurlStack();
            } else {
                // Prior to Gingerbread, HttpUrlConnection was unreliable.
                // See: http://android-developers.blogspot.com/2011/09/androids-http-clients.html
//                stack = new HttpClientStack(AndroidHttpClient.newInstance(userAgent));
            }
        }

        Network network = new BasicNetwork(stack);

        RetroRequestQueue queue;
        if (maxDiskCacheBytes <= -1) {
            // No maximum size specified
            queue = new RetroRequestQueue(new RetroCache(cacheDir), network);
        } else {
            // Disk cache size specified
            queue = new RetroRequestQueue(new RetroCache(cacheDir, maxDiskCacheBytes), network);
        }

        queue.start();

        return queue;
    }

    /**
     * Creates a default instance of the worker pool and calls {@link RetroRequestQueue#start()} on it.
     * You may set a maximum size of the disk cache in bytes.
     *
     * @param context           A {@link Context} to use for creating the cache dir.
     * @param maxDiskCacheBytes the maximum size of the disk cache, in bytes. Use -1 for default size.
     * @return A started {@link RetroRequestQueue} instance.
     */
    public static RetroRequestQueue newRequestQueue(Context context, int maxDiskCacheBytes) {
        return newRequestQueue(context, null, maxDiskCacheBytes);
    }

    /**
     * Creates a default instance of the worker pool and calls {@link RetroRequestQueue#start()} on it.
     *
     * @param context A {@link Context} to use for creating the cache dir.
     * @param stack   An {@link HttpStack} to use for the network, or null for default.
     * @return A started {@link RetroRequestQueue} instance.
     */
    public static RetroRequestQueue newRequestQueue(Context context, HttpStack stack) {
        return newRequestQueue(context, stack, -1);
    }

    /**
     * Creates a default instance of the worker pool and calls {@link RetroRequestQueue#start()} on it.
     *
     * @param context A {@link Context} to use for creating the cache dir.
     * @return A started {@link RetroRequestQueue} instance.
     */
    public static RetroRequestQueue newRequestQueue(Context context) {
        return newRequestQueue(context, null);
    }
}