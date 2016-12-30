package com.joy.http;

/**
 * Created by KEVIN.DAI on 15/11/29.
 */
public enum RequestMode {

    /**
     * fetch net-->response.
     */
    REFRESH_ONLY,
    /**
     * fetch cache-->response.
     */
    CACHE_ONLY,
    /**
     * cache expired: fetch net, update cache-->response.
     */
    REFRESH_AND_CACHE,
    /**
     * cache update needed: fetch cache-->response, fetch net, update cache-->response.
     */
    CACHE_AND_REFRESH
}