package com.joy.http.volley;

import com.android.volley.Cache;
import com.android.volley.Network;
import com.android.volley.Request;
import com.android.volley.RequestQueue;

import rx.Observable;

import static com.joy.http.volley.RequestMode.CACHE_AND_REFRESH;
import static com.joy.http.volley.RequestMode.CACHE_ONLY;
import static com.joy.http.volley.RequestMode.REFRESH_AND_CACHE;
import static com.joy.http.volley.RequestMode.REFRESH_ONLY;

//import javax.inject.Singleton;

/**
 * Created by Daisw on 16/6/27.
 * Modified by Daisw on 16/9/13.(add some methods)
 */
//@Singleton
public final class RetroRequestQueue extends RequestQueue {

    RetroRequestQueue(Cache cache, Network network) {

        super(cache, network);
    }

    /**
     * fetch net-->response.
     */
    public <T> Observable<T> launchRefreshOnly(ObjectRequest<T> request) {

        return launch(request, REFRESH_ONLY);
    }

    public <T> Observable<T> launchRefreshOnly(ObjectRequest<T> request, Object tag) {

        return launch(request, REFRESH_ONLY, tag);
    }

    /**
     * fetch cache-->response.
     */
    public <T> Observable<T> launchCacheOnly(ObjectRequest<T> request) {

        return launch(request, CACHE_ONLY);
    }

    public <T> Observable<T> launchCacheOnly(ObjectRequest<T> request, Object tag) {

        return launch(request, CACHE_ONLY, tag);
    }

    /**
     * cache expired: fetch net, update cache-->response.
     */
    public <T> Observable<T> launchRefreshAndCache(ObjectRequest<T> request) {

        return launch(request, REFRESH_AND_CACHE);
    }

    public <T> Observable<T> launchRefreshAndCache(ObjectRequest<T> request, Object tag) {

        return launch(request, REFRESH_AND_CACHE, tag);
    }

    /**
     * cache update needed: fetch cache-->response, fetch net, update cache-->response.
     */
    public <T> Observable<T> launchCacheAndRefresh(ObjectRequest<T> request) {

        return launch(request, CACHE_AND_REFRESH);
    }

    public <T> Observable<T> launchCacheAndRefresh(ObjectRequest<T> request, Object tag) {

        return launch(request, CACHE_AND_REFRESH, tag);
    }

    public <T> Observable<T> launch(ObjectRequest<T> request, RequestMode mode) {

        return launch(request, mode, request.getIdentifier());
    }

    public <T> Observable<T> launch(ObjectRequest<T> request, RequestMode mode, Object tag) {

        request.setTag(tag);
        request.setRequestMode(mode);
        return addRequest(request);
    }

    <T> Observable<T> addRequest(Request<T> request) {

        return ((ObjectRequest<T>) super.add(request)).observable();
    }

    public void cancelLaunch(Object tag) {

        cancelAll(tag);
    }

    public void cancelAllLauncher() {

        cancelAll(request -> true);
    }
}
