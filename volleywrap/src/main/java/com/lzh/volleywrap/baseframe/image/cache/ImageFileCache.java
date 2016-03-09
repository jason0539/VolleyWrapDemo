package com.lzh.volleywrap.baseframe.image.cache;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import com.lzh.volleywrap.baseframe.utils.MLog;

import android.content.Context;
import android.graphics.Bitmap;

/**
 * 文件缓存类，提供位于磁盘（SD卡）的文件缓存
 */
public class ImageFileCache {
    private static final String TAG = ImageFileCache.class.getSimpleName();

    private static final int DISK_CACHE_SIZE = 1024 * 1024 * 100;//100M
    private DiskLruCache mDiskLruCache = null;
    private static volatile ImageFileCache instance = null;

    private ImageFileCache(Context context) {
        mDiskLruCache = DiskLruCache.openCache(context, DISK_CACHE_SIZE);
    }

    public static final ImageFileCache getInstance(Context context) {
        if (instance == null) {
            synchronized(ImageFileCache.class) {
                if (instance == null) {
                    instance = new ImageFileCache(context);
                }
            }
        }
        return instance;
    }

    /**
     * 添加位图到文件缓存，同步方法
     */
    public void addBitmapToDiscCache(String url, Bitmap bitmap) {
        MLog.d(TAG, " addBitmapToCache:url = " + url + ",localName = " + generateLocalFileName(url));
        mDiskLruCache.put(generateLocalFileName(url), bitmap);
    }

    /**
     * 从文件缓存中获得位图实例，同步方法
     */
    public Bitmap getBitmapFromDiskCache(String url) {
        MLog.d(TAG, " getBitmapFromDiskCache:url = " + url);
        return mDiskLruCache.get(generateLocalFileName(url));
    }

    /**
     * 生成文件名
     */
    protected String generateLocalFileName(String string) {
        if (string == null) {
            return null;
        }
        String result = "";
        try {
            MessageDigest digest = MessageDigest.getInstance("MD5");
            byte[] s = digest.digest(string.getBytes());
            for (int i = 0; i < s.length; i++) {
                result += Integer.toHexString((0x000000ff & s[i]) | 0xffffff00).substring(6);
            }
        } catch (NoSuchAlgorithmException e) {
            // 如果未找到MD5算法，则取最后一个‘/’之后的串作为文件名称
            String[] array = string.split("/");
            if (array != null && array.length > 0) {
                result = array[array.length - 1];
            }
        }
        return result;
    }

    /**
     * 设置缓存格式
     */
    public void setCompressParams(Bitmap.CompressFormat format) {
        if (mDiskLruCache != null) {
            mDiskLruCache.setCompressParams(format, 100);
        }
    }
}

