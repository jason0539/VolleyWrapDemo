package com.lzh.volleywrap.baseframe.volley;

import java.util.Map;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.toolbox.ImageRequest;

import android.graphics.Bitmap;
import android.widget.ImageView;

public class ImageRequestWrapper extends BaseRequestWrapper<Bitmap> implements ImageRequestInterface {

    private int mBitmapWidth;
    private int mBitmapHeight;
    private ImageView.ScaleType mBitmapScaleType;
    private Bitmap.Config mBitmapConfig;

    ImageRequestWrapper(String url, HttpResponseListener<Bitmap> listener) {
        super(url, listener);
        mBitmapScaleType = ImageView.ScaleType.CENTER_INSIDE;
    }

    @Override
    public Request create() {

        Request request = new ImageRequest(mURL, mResponseListener,
                mBitmapWidth, mBitmapHeight, mBitmapScaleType, mBitmapConfig, mErrorListener) {

            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                if (mHeaders == null || mHeaders.isEmpty()) {
                    return super.getHeaders();
                } else {
                    return mHeaders;
                }
            }
        };

        request.setTag(mTag);
        request.setPriority(mPriority);
        request.setShouldCache(mShouldCache);

        if (mTimeout != -1 || mRetries != -1) {
            mTimeout = mTimeout == -1 ? 1000 : mTimeout;
            mRetries = mRetries == -1 ? 2 : mRetries;
            request.setRetryPolicy(new DefaultRetryPolicy(mTimeout, mRetries, 2f));
        }

        return request;
    }

    /**
     * 只能用于Get请求
     */
    @Override
    public void setMethod(int method) {
    }

    @Override
    public void setBitmapDecodeConfig(Bitmap.Config config) {
        mBitmapConfig = config;
    }

    @Override
    public void setBitmapScaleType(ImageView.ScaleType scaleType) {
        mBitmapScaleType = scaleType;
    }

    @Override
    public void setBitmapMaxWidth(int width) {
        mBitmapWidth = width;
    }

    @Override
    public void setBitmapMaxHeight(int height) {
        mBitmapHeight = height;
    }
}
