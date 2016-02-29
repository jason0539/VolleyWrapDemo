package com.lzh.volleywrap.baseframe;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;
import com.lzh.volleywrap.baseframe.http.BaseRequestWrapper;
import com.lzh.volleywrap.baseframe.http.RequestBuilder;
import com.lzh.volleywrap.baseframe.http.RequestInterface;

import android.content.Context;

public class VolleyClient {
    private RequestQueue mRequestQueue;
    private RequestBuilder mBuilder;
    private volatile static VolleyClient instance;

    public static final VolleyClient getInstance(Context context) {
        if (instance == null) {
            synchronized(VolleyClient.class) {
                if (instance == null) {
                    instance = new VolleyClient(context);
                }
            }
        }
        return instance;
    }

    private VolleyClient(Context context) {
        mRequestQueue = Volley.newRequestQueue(context);
        mBuilder = new RequestBuilder();
    }

    public RequestQueue getRequestQueue() {
        return mRequestQueue;
    }

    public RequestBuilder getRequestBuilder() {
        return mBuilder;
    }

    public void sendRequest(RequestInterface requestInterface) {
        Request request = requestInterface.create();
        mRequestQueue.add(request);
    }

    public void cancelPendingRequest() {
        if (mRequestQueue != null) {
            mRequestQueue.cancelAll(BaseRequestWrapper.DEFAULT_TAG);
        }
    }

    public void cancelPendingRequest(String tag) {
        if (mRequestQueue != null) {
            mRequestQueue.cancelAll(tag);
        }
    }
}
