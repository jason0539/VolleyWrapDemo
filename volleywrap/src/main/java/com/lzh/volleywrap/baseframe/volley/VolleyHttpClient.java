package com.lzh.volleywrap.baseframe.volley;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.Volley;

import android.content.Context;

public class VolleyHttpClient {
    private RequestQueue mRequestQueue;
    private ImageLoader mImageLoad;
    private RequestBuilder mBuilder;

    public VolleyHttpClient(Context context) {
        mRequestQueue = Volley.newRequestQueue(context);
        mImageLoad = new ImageLoader(mRequestQueue, new VolleyCacheManager(context));
        mBuilder = new RequestBuilder();
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

    public ImageLoader getImageLoader() {
        return mImageLoad;
    }
}
