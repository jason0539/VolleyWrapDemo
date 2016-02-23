package com.lzh.volleywrap.baseframe.volley;

import org.json.JSONObject;

import android.graphics.Bitmap;

public class RequestBuilder {
    public RequestBuilder() {
    }

    public RequestInterface createJsonObjectRequest(String url, HttpResponseListener<JSONObject> listener) {
        return new JsonObjectRequestWrapper(url, listener);
    }

    public RequestInterface createStringRequest(String url, HttpResponseListener<String> listener) {
        return new StringRequestWrapper(url, listener);
    }

    public ImageRequestInterface createImageRequest(String url, HttpResponseListener<Bitmap> listener) {
        return new ImageRequestWrapper(url, listener);
    }

    public MultipartRequestInterface createMultipartFileRequest(String url, HttpResponseListener<JSONObject> listener) {
        return new MultipartRequestWrapper(url, listener);
    }

}
