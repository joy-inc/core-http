package com.joy.http;

import rx.Observable;

/**
 * Created by Daisw on 2016/12/29.
 */

public interface ILauncher {

    /**
     * fetch net-->response.
     */
    <T, R extends IRequest<T>> Observable<T> launchRefreshOnly(R request);
    <T> Observable<T> launchRefreshOnly(IRequest<T> request, Object tag);

    /**
     * fetch cache-->response.
     */
    <T> Observable<T> launchCacheOnly(IRequest<T> request);
    <T> Observable<T> launchCacheOnly(IRequest<T> request, Object tag);

    /**
     * cache expired: fetch net, update cache-->response.
     */
    <T> Observable<T> launchRefreshAndCache(IRequest<T> request);
    <T> Observable<T> launchRefreshAndCache(IRequest<T> request, Object tag);

    /**
     * cache update needed: fetch cache-->response, fetch net, update cache-->response.
     */
    <T> Observable<T> launchCacheAndRefresh(IRequest<T> request);
    <T> Observable<T> launchCacheAndRefresh(IRequest<T> request, Object tag);

    <T> Observable<T> launch(IRequest<T> request, RequestMode mode);
    <T> Observable<T> launch(IRequest<T> request, RequestMode mode, Object tag);
}
