package com.lzh.volleywrap.baseframe.http;

import java.io.UnsupportedEncodingException;
import java.util.Map;

import org.json.JSONObject;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.NetworkResponse;
import com.android.volley.ParseError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.toolbox.HttpHeaderParser;
import com.android.volley.toolbox.JsonObjectRequest;

public class JsonObjectRequestWrapper extends BaseRequestWrapper<JSONObject> {

    JsonObjectRequestWrapper(String url, HttpResponseListener<JSONObject> listener) {
        super(url, listener);
    }

    public Request create() {

        Request request = new JsonObjectRequest(mMethod, mURL, mParams == null ? null : new JSONObject(mParams),
                mResponseListener, mErrorListener) {

            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                if (mHeaders == null || mHeaders.isEmpty()) {
                    return super.getHeaders();
                } else {
                    return mHeaders;
                }
            }

            @Override
            protected Response<JSONObject> parseNetworkResponse(NetworkResponse response) {
                try {
                    JSONObject jsonObject = new JSONObject(new String(response.data, mCharset));
                    return Response.success(jsonObject, HttpHeaderParser.parseCacheHeaders(response));
                } catch (UnsupportedEncodingException e) {
                    return Response.error(new ParseError(e));
                } catch (Exception je) {
                    return Response.error(new ParseError(je));
                }
            }

            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                if (mParams == null || mParams.isEmpty()) {
                    return super.getParams();
                } else {
                    return mParams;
                }
            }
        };

        request.setTag(mTag);
        request.setPriority(mPriority);
        request.setShouldCache(mShouldCache);
        if (mTimeout != -1 || mRetries != -1) {
            mTimeout = mTimeout == -1 ? DefaultRetryPolicy.DEFAULT_TIMEOUT_MS : mTimeout;
            mRetries = mRetries == -1 ? DefaultRetryPolicy.DEFAULT_MAX_RETRIES : mRetries;
            request.setRetryPolicy(new DefaultRetryPolicy(mTimeout, mRetries, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        }

        return request;
    }

}
