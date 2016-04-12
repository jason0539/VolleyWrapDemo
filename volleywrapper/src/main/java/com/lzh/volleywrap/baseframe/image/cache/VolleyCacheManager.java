package com.lzh.volleywrap.baseframe.image.cache;

import com.android.volley.toolbox.ImageLoader;

import android.content.Context;
import android.graphics.Bitmap;

public class VolleyCacheManager implements ImageLoader.ImageCache {
    public static final String TAG = VolleyCacheManager.class.getSimpleName();

    private Context mContext;

    public VolleyCacheManager(Context context) {
        mContext = context;
    }

    @Override
    public Bitmap getBitmap(String s) {
//        MLog.d(TAG, " getBitmap:url = " + s);
        return ImageMemoryCache.getInstance().get(s) == null ?
                ImageFileCache.getInstance(mContext).getBitmapFromDiskCache(s) : ImageMemoryCache.getInstance().get(s);
    }

    @Override
    public void putBitmap(String s, Bitmap bitmap) {
//        MLog.d(TAG, " putBitmap:url = " + s);
        ImageMemoryCache.getInstance().put(s, bitmap);
        ImageFileCache.getInstance(mContext).addBitmapToDiscCache(s, bitmap);
    }

}
