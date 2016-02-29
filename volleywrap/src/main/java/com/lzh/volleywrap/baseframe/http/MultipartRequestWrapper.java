package com.lzh.volleywrap.baseframe.http;

import java.io.File;
import java.util.Map;

import org.json.JSONObject;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.toolbox.MultipartFileRequest;

public class MultipartRequestWrapper extends BaseRequestWrapper<JSONObject> implements MultipartRequestInterface{

    private String mKey;
    private File mFile;
    private String mMimeType;
    private Map<String, String> mParam;

    MultipartRequestWrapper(String url, HttpResponseListener<JSONObject> listener) {
        super(url, listener);
    }

    @Override
    public Request create() {
        MultipartFileRequest request = new MultipartFileRequest(mURL, mResponseListener, mErrorListener);
        request.setHeader(mHeaders);
        request.setFileBody(mKey, mFile, mMimeType);
        request.setPriority(mPriority);
        request.setShouldCache(mShouldCache);

        for (Map.Entry<String, String> entry : mParam.entrySet()) {
            request.setStringBody(entry.getKey(), entry.getValue());
        }

        if (mTimeout != -1 || mRetries != -1) {
            mTimeout = mTimeout == -1 ? DefaultRetryPolicy.DEFAULT_TIMEOUT_MS : mTimeout;
            mRetries = mRetries == -1 ? DefaultRetryPolicy.DEFAULT_MAX_RETRIES : mRetries;
            request.setRetryPolicy(new DefaultRetryPolicy(mTimeout, mRetries, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        }

        return request;
    }

    @Override
    public void setFileBody(String key, File file, String mimeType) {
        mKey = key;
        mFile = file;
        mMimeType = mimeType;
    }

    @Override
    public void setStringBody(Map<String, String> param) {
        mParam = param;
    }
}
