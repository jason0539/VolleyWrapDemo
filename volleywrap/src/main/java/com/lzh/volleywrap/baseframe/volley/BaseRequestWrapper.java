package com.lzh.volleywrap.baseframe.volley;

import java.util.HashMap;
import java.util.Map;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;

public abstract class BaseRequestWrapper<T> implements RequestInterface {
    static final String DEFAULT_TAG = "default_tag";
    private final String DEFAULT_CHARSET = "UTF-8";

    protected String mURL;
    protected Response.Listener<T> mResponseListener;
    protected Response.ErrorListener mErrorListener;
    protected String mTag;
    protected int mMethod;
    protected String mCharset;
    protected Request.Priority mPriority;
    protected boolean mShouldCache;
    protected int mTimeout;
    protected int mRetries;
    protected Map mParams;
    protected Map<String, String> mHeaders;

    BaseRequestWrapper(String url, final HttpResponseListener<T> listener) {
        mURL = url;
        mResponseListener = new Response.Listener<T>() {
            @Override
            public void onResponse(T response) {
                if (listener != null) {
                    listener.onResponse(response);
                }
            }
        };

        mErrorListener = new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                if (listener != null) {
                    listener.onError(error.getMessage());
                }
            }
        };

        mTag = DEFAULT_TAG;
        mMethod = Request.Method.GET;
        mCharset = DEFAULT_CHARSET;
        mPriority = Request.Priority.NORMAL;
        mShouldCache = true;
        mTimeout = -1;
        mRetries = -1;
    }

    @Override
    public void setTag(String tag) {
        mTag = tag;
    }

    @Override
    public void setMethod(int method) {
        mMethod = method;
    }

    @Override
    public void setCharset(String charset) {
        mCharset = charset;
    }

    @Override
    public void setRequestPriority(Request.Priority priority) {
        mPriority = priority;
    }

    @Override
    public void shouldCache(boolean cache) {
        mShouldCache = cache;
    }

    @Override
    public void setTimeout(int timeout) {
        mTimeout = timeout;
    }

    @Override
    public void setReties(int reties) {
        mRetries = reties;
    }

    @Override
    public void setPostParam(Map param) {
        mParams = param;
    }

    @Override
    public void setHeaders(Map<String, String> headers) {
        mHeaders = new HashMap<>();
        mHeaders.putAll(headers);
    }

    @Override
    abstract public Request create();
}

