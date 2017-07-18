/*
 * Copyright (C) 2011 The Android Open Source Project
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

package com.joy.http.volley;

import android.os.Handler;
import android.os.Looper;

import com.joy.http.JoyError;
import com.joy.http.JoyHttp;
import com.joy.http.LaunchMode;
import com.joy.http.ResponseListener;
import com.joy.http.volley.VolleyLog.MarkerLog;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.nio.charset.Charset;
import java.util.Collections;
import java.util.Map;

import rx.Observable;
import rx.subjects.PublishSubject;

/**
 * Base class for all network requests.
 *
 * @param <T> The type of parsed response this request expects.
 */
public abstract class Request<T> implements Comparable<Request<T>> {

    /**
     * Default encoding for POST or PUT parameters. See {@link #getParamsEncoding()}.
     */
    private static final String DEFAULT_PARAMS_ENCODING = "UTF-8";

    /**
     * Supported request methods.
     */
    public enum Method {
        DEPRECATED_GET_OR_POST,
        GET,
        POST,
        PUT,
        DELETE,
        HEAD,
        OPTIONS,
        TRACE,
        PATCH
    }

    /** An event log tracing the lifetime of this request; for debugging. */
    private final MarkerLog mEventLog = MarkerLog.ENABLED ? new MarkerLog() : null;

    /**
     * Request method of this request.  Currently supports GET, POST, PUT, DELETE, HEAD, OPTIONS,
     * TRACE, and PATCH.
     */
    private final Method mMethod;

    /** URL of this request. */
    private final String mUrl;
    protected Map<String, String> mHeaders, mParams;

    /** The redirect url to use for 3xx http responses */
    private String mRedirectUrl;

    /** The unique identifier of the request */
    private String mIdentifier;

//    /** Default tag for {@link TrafficStats}. */
//    private final int mDefaultTrafficStatsTag;

    /** Listener interface for responses. */
    protected ResponseListener<T> mListener;

    /** Sequence number of this request, used to enforce FIFO ordering. */
    private Integer mSequence;

    /** The request queue this request is associated with. */
    private RequestLauncher mLauncher;

    /** Whether or not this request has been canceled. */
    private boolean mCanceled = false;

    /** Whether or not a response has been delivered for this request yet. */
    private boolean mResponseDelivered = false;

    /** The retry policy for this request. */
    private RetryPolicy mRetryPolicy;

    /**
     * When a request can be retrieved from cache but must be refreshed from
     * the network, the cache entry will be stored here so that in the event of
     * a "Not Modified" response, we can be sure it hasn't been evicted from cache.
     */
    private Cache.Entry mCacheEntry = null;

    /** An opaque token tagging this request; used for bulk cancellation. */
    private Object mTag;

    protected PublishSubject<T> mObserver;

    /**
     * Creates a new request with the given method (one of the values from {@link Method}),
     * URL, and error listener.  Note that the normal response listener is not provided here as
     * delivery of responses is provided by subclasses, who have a better idea of how to deliver
     * an already-parsed response.
     */
    public Request(Method method, String url) {
        mMethod = method;
        mUrl = url;
        mIdentifier = createIdentifier(method, url);
        mRetryPolicy = new DefaultRetryPolicy(JoyHttp.getTimeoutMs(), JoyHttp.getRetryCount());

//        mDefaultTrafficStatsTag = findDefaultTrafficStatsTag(url);

        mObserver = PublishSubject.create();
    }

    /**
     * @param listener Listener to receive the Object response
     */
    public void setListener(ResponseListener<T> listener) {
        mListener = listener;
    }

    /**
     * Return the method for this request.  Can be one of the values in {@link Method}.
     */
    public Method getMethod() {
        return mMethod;
    }

    /**
     * Set a tag on this request. Can be used to cancel all requests with this
     * tag by {@link RequestLauncher#abort(Object)}.
     *
     * @return This Request object to allow for chaining.
     */
    public Request<?> setTag(Object tag) {
        mTag = tag;
        return this;
    }

    /**
     * Returns this request's tag.
     * @see Request#setTag(Object)
     */
    public Object getTag() {
        return mTag;
    }

//    /**
//     * @return A tag for use with {@link TrafficStats#setThreadStatsTag(int)}
//     */
//    public int getTrafficStatsTag() {
//        return mDefaultTrafficStatsTag;
//    }

//    /**
//     * @return The hashcode of the URL's host component, or 0 if there is none.
//     */
//    private static int findDefaultTrafficStatsTag(String url) {
//        if (!TextUtils.isEmpty(url)) {
//            Uri uri = Uri.parse(url);
//            if (uri != null) {
//                String host = uri.getHost();
//                if (host != null) {
//                    return host.hashCode();
//                }
//            }
//        }
//        return 0;
//    }

    /**
     * Sets the retry policy for this request.
     *
     * @return This Request object to allow for chaining.
     */
    public Request<?> setRetryPolicy(RetryPolicy retryPolicy) {
        mRetryPolicy = retryPolicy;
        return this;
    }

    /**
     * Adds an event to this request's event log; for debugging.
     */
    public void addMarker(String tag) {
        if (MarkerLog.ENABLED) {
            mEventLog.add(tag, Thread.currentThread().getName());
        }
    }

    /**
     * Notifies the request queue that this request has finished (successfully or with error).
     *
     * <p>Also dumps all events from this request's event log; for debugging.</p>
     */
    void finish(final String tag) {
        if (mLauncher != null) {
            mLauncher.finish(this);
            onFinish();
        }
        if (MarkerLog.ENABLED) {
            final String threadName = Thread.currentThread().getName();
            if (Looper.myLooper() != Looper.getMainLooper()) {
                // If we finish marking off of the main thread, we need to
                // actually do it on the main thread to ensure correct ordering.
                Handler mainThread = new Handler(Looper.getMainLooper());
                mainThread.post(new Runnable() {
                    @Override
                    public void run() {
                        mEventLog.add(tag, threadName);
                        mEventLog.finish(Request.this.toString());
                    }
                });
                return;
            }
            mEventLog.add(tag, threadName);
            mEventLog.finish(this.toString());
        }
    }

    /**
     * clear listeners when finished
     */
    protected void onFinish() {
        mListener = null;
        if (mHeaders != null) {
            mHeaders.clear();
            mHeaders = null;
        }
        if (mParams != null) {
            mParams.clear();
            mParams = null;
        }
    }

    /**
     * Associates this request with the given queue. The request queue will be notified when this
     * request has finished.
     *
     * @return This Request object to allow for chaining.
     */
    public Request<?> setLauncher(RequestLauncher launcher) {
        mLauncher = launcher;
        return this;
    }

    /**
     * Sets the sequence number of this request.  Used by {@link RequestLauncher}.
     *
     * @return This Request object to allow for chaining.
     */
    public final Request<?> setSequence(int sequence) {
        mSequence = sequence;
        return this;
    }

    /**
     * Returns the sequence number of this request.
     */
    public final int getSequence() {
        if (mSequence == null) {
            throw new IllegalStateException("getSequence called before setSequence");
        }
        return mSequence;
    }

    /**
     * Returns the URL of this request.
     */
    public String getUrl() {
        return (mRedirectUrl != null) ? mRedirectUrl : mUrl;
    }

    /**
     * Returns the URL of the request before any redirects have occurred.
     */
    public String getOriginUrl() {
    	return mUrl;
    }

    /**
     * Returns the identifier of the request.
     */
    public String getIdentifier() {
        return mIdentifier;
    }

    /**
     * Sets the redirect url to handle 3xx http responses.
     */
    public void setRedirectUrl(String redirectUrl) {
    	mRedirectUrl = redirectUrl;
    }

    /**
     * Returns the cache key for this request.  By default, this is the URL.
     */
    public String getCacheKey() {
        return mMethod + ":" + mUrl;
    }

    /**
     * Annotates this request with an entry retrieved for it from cache.
     * Used for cache coherency support.
     *
     * @return This Request object to allow for chaining.
     */
    public Request<?> setCacheEntry(Cache.Entry entry) {
        mCacheEntry = entry;
        return this;
    }

    /**
     * Returns the annotated cache entry, or null if there isn't one.
     */
    public Cache.Entry getCacheEntry() {
        return mCacheEntry;
    }

    /**
     * Mark this request as canceled.  No callback will be delivered.
     */
    public void cancel() {
        mCanceled = true;
    }

    /**
     * Returns true if this request has been canceled.
     */
    public boolean isCanceled() {
        return mCanceled;
    }

    public void setHeaders(Map<String, String> headers) {
        mHeaders = headers;
    }

    public void setParams(Map<String, String> params) {
        mParams = params;
    }

    /**
     * Returns a list of extra HTTP headers to go along with this request. Can
     * throw {@link AuthFailureError} as authentication may be required to
     * provide these values.
     * @throws AuthFailureError In the event of auth failure
     */
    public Map<String, String> getHeaders() throws AuthFailureError {
        if (mHeaders != null && !mHeaders.isEmpty()) {
            return mHeaders;
        }
        return Collections.emptyMap();
    }

    /**
     * Returns a Map of parameters to be used for a POST or PUT request.  Can throw
     * {@link AuthFailureError} as authentication may be required to provide these values.
     *
     * <p>Note that you can directly override {@link #getBody()} for custom data.</p>
     *
     * @throws AuthFailureError in the event of auth failure
     */
    protected Map<String, String> getParams() throws AuthFailureError {
        if (mParams != null && !mParams.isEmpty()) {
            return mParams;
        }
        return null;
    }

    /**
     * Returns which encoding should be used when converting POST or PUT parameters returned by
     * {@link #getParams()} into a raw POST or PUT body.
     *
     * <p>This controls both encodings:
     * <ol>
     *     <li>The string encoding used when converting parameter names and values into bytes prior
     *         to URL encoding them.</li>
     *     <li>The string encoding used when converting the URL encoded parameters into a raw
     *         byte array.</li>
     * </ol>
     */
    protected String getParamsEncoding() {
        return DEFAULT_PARAMS_ENCODING;
    }

    /**
     * Returns the content type of the POST or PUT body.
     */
    public String getBodyContentType() {
        return "application/x-www-form-urlencoded; charset=" + getParamsEncoding();
    }

    /**
     * Returns the raw POST or PUT body to be sent.
     *
     * <p>By default, the body consists of the request parameters in
     * application/x-www-form-urlencoded format. When overriding this method, consider overriding
     * {@link #getBodyContentType()} as well to match the new body format.
     *
     * @throws AuthFailureError in the event of auth failure
     */
    public byte[] getBody() throws AuthFailureError {
        Map<String, String> params = getParams();
        if (params != null && params.size() > 0) {
            return encodeParameters(params, getParamsEncoding());
        }
        return null;
    }

    /**
     * Converts <code>params</code> into an application/x-www-form-urlencoded encoded string.
     */
    private byte[] encodeParameters(Map<String, String> params, String paramsEncoding) {
        StringBuilder encodedParams = new StringBuilder();
        try {
            for (Map.Entry<String, String> entry : params.entrySet()) {
                encodedParams.append(URLEncoder.encode(entry.getKey(), paramsEncoding));
                encodedParams.append('=');
                encodedParams.append(URLEncoder.encode(entry.getValue(), paramsEncoding));
                encodedParams.append('&');
            }
            return encodedParams.toString().getBytes(paramsEncoding);
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException("Encoding not supported: " + paramsEncoding, e);
        }
    }

    /**
     * Priority values.  Requests will be processed from higher priorities to
     * lower priorities, in FIFO order.
     */
    public enum Priority {
        LOW,
        NORMAL,
        HIGH,
        IMMEDIATE
    }

    /**
     * Returns the {@link Priority} of this request; {@link Priority#NORMAL} by default.
     */
    public Priority getPriority() {
        return Priority.NORMAL;
    }

    /**
     * Returns the socket timeout in milliseconds per retry attempt. (This value can be changed
     * per retry attempt if a backoff is specified via backoffTimeout()). If there are no retry
     * attempts remaining, this will cause delivery of a {@link TimeoutError} error.
     */
    public final int getTimeoutMs() {
        return mRetryPolicy.getCurrentTimeout();
    }

    /**
     * Returns the retry policy that should be used  for this request.
     */
    public RetryPolicy getRetryPolicy() {
        return mRetryPolicy;
    }

    /**
     * Mark this request as having a response delivered on it.  This can be used
     * later in the request's lifetime for suppressing identical responses.
     */
    public void markDelivered() {
        mResponseDelivered = true;
    }

    /**
     * Returns true if this request has had a response delivered for it.
     */
    public boolean hasHadResponseDelivered() {
        return mResponseDelivered;
    }

    /**
     * Subclasses must implement this to parse the raw network response
     * and return an appropriate response type. This method will be
     * called from a worker thread.  The response will not be delivered
     * if you return null.
     * @param response Response from the network
     * @return The parsed response, or null in the case of an error
     */
    abstract protected Result<T> parseNetworkResponse(Response response);

    /**
     * Subclasses can override this method to parse 'networkError' and return a more specific error.
     *
     * <p>The default implementation just returns the passed 'networkError'.</p>
     *
     * @param error the error retrieved from the network
     * @return an NetworkError augmented with additional information
     */
    protected JoyError parseNetworkError(JoyError error) {
        return error;
    }

    /**
     * Subclasses must implement this to perform delivery of the parsed
     * response to their listeners.  The given response is guaranteed to
     * be non-null; responses that fail to parse are not delivered.
     * @param t The parsed response returned by
     * {@link #parseNetworkResponse(Response)}
     */
    protected void deliverResponse(T t) {
        if (mListener != null) {
            mListener.onSuccess(mTag, t);
        }
        mObserver.onNext(t);
        if (isFinalResponse()) {
            mObserver.onCompleted();
        }
    }

    /**
     * Delivers error message to the ErrorListener that the Request was
     * initialized with.
     *
     * @param e Error details
     */
    public void deliverError(Throwable e) {
        if (mListener != null) {
            mListener.onError(mTag, e);
        }
        mObserver.onError(e);
    }

    /**
     * Our comparator sorts from high to low priority, and secondarily by
     * sequence number to provide FIFO ordering.
     */
    @Override
    public int compareTo(Request<T> other) {
        Priority left = this.getPriority();
        Priority right = other.getPriority();

        // High-priority requests are "lesser" so they are sorted to the front.
        // Equal priorities are sorted by sequence number to provide FIFO ordering.
        return left == right ?
                this.mSequence - other.mSequence :
                right.ordinal() - left.ordinal();
    }

    @Override
    public String toString() {
//        String trafficStatsTag = "0x" + Integer.toHexString(getTrafficStatsTag());
        return (mCanceled ? "[x] " : "[ ] ") + getUrl() + " " + mTag + " "
                + getPriority() + " " + mSequence;
    }

    private static long sCounter;
    /**
     *  sha1(Request:method:url:timestamp:counter)
     * @param method http method
     * @param url               http request url
     * @return sha1 hash string
     */
    private static String createIdentifier(Method method, String url) {
        return InternalUtils.sha1Hash("Request:" + method + ":" + url +
                ":" + System.currentTimeMillis() + ":" + (sCounter++));
    }

    protected Observable<T> observable() {
        return mObserver;
    }

    protected LaunchMode mLaunchMode = LaunchMode.REFRESH_ONLY;

    public LaunchMode getLaunchMode() {
        return mLaunchMode;
    }

    public void setLaunchMode(LaunchMode mode) {
        mLaunchMode = mode;
    }

    /** True if this response was a soft-expired one and a second one MAY be coming. */
    public boolean intermediate = false;
    public final boolean isFinalResponse() {
        return !intermediate;
    }

    public final String toString(Response response) throws IOException, ServerError {
        final InputStream is = response.data;
        if (is == null) {
            throw new ServerError();
        }
        BufferedReader br = new BufferedReader(new InputStreamReader(is, Charset.defaultCharset()));
        StringBuilder sb = new StringBuilder();
        char[] buffer = new char[4096];// 4k
        int n;

        if (response instanceof NetworkResponse) {
            final String cacheKey = getCacheKey();
            BufferedWriter bw = null;
            if (mLaunchMode != LaunchMode.REFRESH_ONLY) {
                File file = mLauncher.getCache().getFileForKey(cacheKey);
                if (file != null) {
                    bw = new BufferedWriter(new FileWriter(file));
                }
            }
            while ((n = br.read(buffer)) != -1) {
                if (mCanceled) {
                    br.close();
                    if (bw != null) {
                        bw.close();
                    }
                    return null;
                }
                sb.append(new String(buffer, 0, n));
                if (bw != null) {
                    bw.write(buffer, 0, n);
                }
            }
            br.close();
            if (bw != null) {
                bw.close();
                mLauncher.getCache().put(cacheKey);
                addMarker("network-cache-written");
            }
        } else {// CacheResponse
            while ((n = br.read(buffer)) != -1) {
                if (mCanceled) {
                    br.close();
                    return null;
                }
                sb.append(new String(buffer, 0, n));
            }
            br.close();
        }
        return sb.toString();
    }

    public final boolean hasCache() {
        RequestLauncher launcher = JoyHttp.getLauncher();
        if (launcher == null) {
            return false;
        }
        Cache cache = launcher.getCache();
        return cache != null && cache.hasCache(getCacheKey());
    }
}
