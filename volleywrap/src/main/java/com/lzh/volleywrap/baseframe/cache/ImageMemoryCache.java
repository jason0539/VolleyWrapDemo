package com.lzh.volleywrap.baseframe.cache;

import android.graphics.Bitmap;
import android.util.LruCache;

public class ImageMemoryCache {

    static final int CACHE_SIZE = 10 * 1024 * 1024;//10M
    private LruCache<String, Bitmap> mMemoryCache;
    private static volatile ImageMemoryCache imageMemoryCache;

    private ImageMemoryCache() {
        mMemoryCache = new LruCache<String, Bitmap>(CACHE_SIZE) {
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

