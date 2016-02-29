package com.lzh.volleywrap.baseframe.image.cache;

import com.android.volley.toolbox.ImageLoader;

import android.content.Context;
import android.graphics.Bitmap;

public class VolleyCacheManager implements ImageLoader.ImageCache {

    private Context mContext;

    public VolleyCacheManager(Context context) {
        mContext = context;
    }

    @Override
    public Bitmap getBitmap(String s) {
        Bitmap bitmap = ImageMemoryCache.getInstance().get(s);
        if (bitmap == null) {
            bitmap = ImageFileCache.getInstance(mContext).getBitmapFromDiskCache(s);
        }
        return bitmap;
    }

    @Override
    public void putBitmap(String s, Bitmap bitmap) {
        ImageFileCache.getInstance(mContext).addBitmapToCache(s, bitmap);
        ImageMemoryCache.getInstance().put(s, bitmap);
    }

}
