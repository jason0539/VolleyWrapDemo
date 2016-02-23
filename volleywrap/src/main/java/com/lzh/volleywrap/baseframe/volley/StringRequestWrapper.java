package com.lzh.volleywrap.baseframe.volley;

import java.io.UnsupportedEncodingException;
import java.util.Map;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.NetworkResponse;
import com.android.volley.ParseError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.toolbox.HttpHeaderParser;
import com.android.volley.toolbox.StringRequest;

public class StringRequestWrapper extends BaseRequestWrapper<String> {

    StringRequestWrapper(String url, HttpResponseListener<String> listener) {
        super(url, listener);
    }

    @Override
    public Request create() {

        Request request = new StringRequest(mMethod, mURL, mResponseListener, mErrorListener) {

            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                if (mHeaders==null || mHeaders.isEmpty()){
                    return super.getHeaders();
                } else {
                    return mHeaders;
                }
            }

            @Override
            protected Response<String> parseNetworkResponse(NetworkResponse response) {
                try {
                    String string = new String(response.data, mCharset);
                    return Response.success(string, HttpHeaderParser.parseCacheHeaders(response));
                } catch (UnsupportedEncodingException e) {
                    return Response.error(new ParseError(e));
                } catch (Exception je) {
                    return Response.error(new ParseError(je));
                }
            }

            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                if (mParams == null||mParams.isEmpty()) {
                    return super.getParams();
                }else {
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
