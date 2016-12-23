package com.joy.http.volley;

import com.android.volley.Cache;

/**
 * fetch cache only default.
 * Created by KEVIN.DAI on 15/11/28.
 */
public class RetroEntry extends Cache.Entry {

    private boolean mExpired;
    private boolean mRefreshNeeded;

    public void setRequestMode(RequestMode mode) {
        switch (mode) {
            case CACHE_ONLY:// 既没有过期也不需要更新
                mExpired = false;
                mRefreshNeeded = false;
                break;
            case REFRESH_AND_CACHE:// 过期了，需要更新缓存
                mExpired = true;
                break;
            case CACHE_AND_REFRESH:// 没有过期但是需要更新
                mExpired = false;
                mRefreshNeeded = true;
                break;
            default:
                break;
        }
    }

    @Override
    public boolean isExpired() {
        return mExpired;
    }

    @Override
    public boolean refreshNeeded() {
        return mRefreshNeeded;
    }
}
