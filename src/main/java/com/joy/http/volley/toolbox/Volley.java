/*
 * Copyright (C) 2012 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.joy.http.volley.toolbox;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager.NameNotFoundException;
import android.net.http.AndroidHttpClient;
import android.os.Build;

import com.joy.http.volley.Network;
import com.joy.http.volley.RequestLauncher;

import java.io.File;

import static android.os.Build.VERSION.SDK_INT;
import static android.os.Build.VERSION_CODES.GINGERBREAD;

public class Volley {

    /** Default on-disk cache directory. */
    private static final String DEFAULT_CACHE_DIR = "volley";

    /**
     * Creates a default instance of the worker pool and calls {@link RequestLauncher#start()} on it.
     * You may set a maximum size of the disk cache in bytes.
     *
     * @param context A {@link Context} to use for creating the cache dir.
     * @param stack An {@link HttpStack} to use for the network, or null for default.
     * @param maxDiskCacheBytes the maximum size of the disk cache, in bytes. Use -1 for default size.
     * @return A started {@link RequestLauncher} instance.
     */
    public static RequestLauncher newLauncher(Context context, HttpStack stack, int maxDiskCacheBytes) {
        File cacheDir = new File(context.getCacheDir(), DEFAULT_CACHE_DIR);

        String userAgent = "volley/0";
        try {
            String packageName = context.getPackageName();
            PackageInfo info = context.getPackageManager().getPackageInfo(packageName, 0);
            userAgent = packageName + "/" + info.versionCode;
        } catch (NameNotFoundException e) {
            e.printStackTrace();
        }

        if (stack == null) {
            if (SDK_INT >= GINGERBREAD) {
                stack = new HurlStack();
            } else {
                // Prior to Gingerbread, HttpUrlConnection was unreliable.
                // See: http://android-developers.blogspot.com/2011/09/androids-http-clients.html
                stack = new HttpClientStack(AndroidHttpClient.newInstance(userAgent));
            }
        }

        Network network = new BasicNetwork(stack);

        RequestLauncher launcher;
        if (maxDiskCacheBytes <= -1) {
            // No maximum size specified
            launcher = new RequestLauncher(new DiskBasedCache(cacheDir), network);
        } else {
            // Disk cache size specified
            launcher = new RequestLauncher(new DiskBasedCache(cacheDir, maxDiskCacheBytes), network);
        }
        launcher.start();

        return launcher;
    }

    /**
     * Creates a default instance of the worker pool and calls {@link RequestLauncher#start()} on it.
     * You may set a maximum size of the disk cache in bytes.
     *
     * @param context A {@link Context} to use for creating the cache dir.
     * @param maxDiskCacheBytes the maximum size of the disk cache, in bytes. Use -1 for default size.
     * @return A started {@link RequestLauncher} instance.
     */
    public static RequestLauncher newLauncher(Context context, int maxDiskCacheBytes) {
        return newLauncher(context, null, maxDiskCacheBytes);
    }

    /**
     * Creates a default instance of the worker pool and calls {@link RequestLauncher#start()} on it.
     *
     * @param context A {@link Context} to use for creating the cache dir.
     * @param stack An {@link HttpStack} to use for the network, or null for default.
     * @return A started {@link RequestLauncher} instance.
     */
    public static RequestLauncher newLauncher(Context context, HttpStack stack) {
        return newLauncher(context, stack, -1);
    }

    /**
     * Creates a default instance of the worker pool and calls {@link RequestLauncher#start()} on it.
     *
     * @param context A {@link Context} to use for creating the cache dir.
     * @return A started {@link RequestLauncher} instance.
     */
    public static RequestLauncher newLauncher(Context context) {
        return newLauncher(context, null);
    }

}
