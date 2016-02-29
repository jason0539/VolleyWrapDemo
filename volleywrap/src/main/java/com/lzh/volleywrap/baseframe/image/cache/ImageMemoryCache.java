package com.lzh.volleywrap.baseframe.image.cache;

import com.lzh.volleywrap.baseframe.utils.MLog;

import android.graphics.Bitmap;
import android.util.LruCache;

public class ImageMemoryCache {
    private static final String TAG = ImageMemoryCache.class.getSimpleName();

    private LruCache<String, Bitmap> mMemoryCache;
    private static volatile ImageMemoryCache imageMemoryCache;

    private ImageMemoryCache() {
        int maxMemorySize = (int) (Runtime.getRuntime().maxMemory() / 3);
        MLog.d(TAG, " ImageMemoryCache maxMemorySize=" + maxMemorySize);
        mMemoryCache = new LruCache<String, Bitmap>(maxMemorySize) {
            @Override
            protected int sizeOf(String key, Bitmap bitmap) {
                return bitmap.getRowBytes() * bitmap.getHeight();
            }
        };
    }

    public static ImageMemoryCache getInstance() {
        if (imageMemoryCache == null) {
            synchronized(ImageMemoryCache.class) {
                if (imageMemoryCache == null) {
                    imageMemoryCache = new ImageMemoryCache();
                }
            }
        }
        return imageMemoryCache;
    }

    public void put(String url, Bitmap bitmap) {
        if (url != null) {
            mMemoryCache.put(url, bitmap);
        }
    }

    public Bitmap get(String url) {
        return mMemoryCache.get(url);
    }

    public int getSize() {
        return mMemoryCache.size();
    }

    public void clear() {
        mMemoryCache.evictAll();
    }

}

