package com.lzh.volleywrap.middleframe;

import java.util.HashMap;

import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageLoader;
import com.lzh.volleywrap.baseframe.VolleyClient;
import com.lzh.volleywrap.baseframe.image.ImageLoaderOption;
import com.lzh.volleywrap.baseframe.image.cache.VolleyCacheManager;
import com.lzh.volleywrap.baseframe.image.displayer.SimpleBitmapDisplayer;
import com.lzh.volleywrap.baseframe.utils.MLog;

import android.content.Context;
import android.widget.ImageView;

/**
 * liuzhenhui 16/2/23.下午10:44
 */
public class ImageLoaderWrapper {
    private static final String TAG = ImageLoaderWrapper.class.getSimpleName();

    private ImageLoader mImageLoader = null;
    private ImageLoaderOption mDefaultLoaderOption = null;
    private HashMap<Integer, String> imageViewHashMap;

    private ImageLoaderWrapper() {
        imageViewHashMap = new HashMap<>();
    }

    public static ImageLoaderWrapper getInstance() {
        return LazyHolder.INSTANCE;
    }

    private static class LazyHolder {
        public static ImageLoaderWrapper INSTANCE = new ImageLoaderWrapper();
    }

    public void init(Context context) {
        mImageLoader =
                new ImageLoader(VolleyClient.getInstance(context).getRequestQueue(), new VolleyCacheManager(context));
        mDefaultLoaderOption = new ImageLoaderOption.Builder()
                .setBitmapDisplayer(new SimpleBitmapDisplayer())
                .build();
    }

    public void displayImage(String url, final ImageView imageView) {
        MLog.d(TAG, " displayImage:url = " + url + ",imageview.hash = " + imageView.hashCode());
        if (mDefaultLoaderOption.isResetImageViewBeforeLoad()) {
            imageView.setImageBitmap(null);
        }
        imageViewHashMap.put(imageView.hashCode(), url);
        mImageLoader.get(url, new ImageLoader.ImageListener() {

            @Override
            public void onResponse(ImageLoader.ImageContainer imageContainer, boolean b) {
                if (imageViewHashMap.get(imageView.hashCode()).equals(imageContainer.getRequestUrl())) {
                    mDefaultLoaderOption.getDisplayer().display(imageContainer.getBitmap(), imageView);
                }
            }

            @Override
            public void onErrorResponse(VolleyError volleyError) {
                MLog.d(TAG, " onErrorResponse VolleyError:" + volleyError.toString());
            }

        });
    }

    public void displayImage(String url, final ImageView imageView, int width, int height) {

    }

}
