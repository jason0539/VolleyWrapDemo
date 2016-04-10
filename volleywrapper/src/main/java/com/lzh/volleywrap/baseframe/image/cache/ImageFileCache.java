package com.lzh.volleywrap.baseframe.image.cache;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import com.lzh.volleywrap.baseframe.utils.MLog;
import com.lzh.volleywrap.baseframe.utils.SysOSAPI;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Environment;
import libcore.io.DiskLruCache;

/**
 * 文件缓存类，提供位于磁盘（SD卡）的文件缓存
 */
public class ImageFileCache {
    private static final String TAG = ImageFileCache.class.getSimpleName();

    private static final String CACHE_FOLDER = "ImageCache";
    private static final int DISK_CACHE_SIZE = 1024 * 1024 * 100;//100M

    private int mCompressQuality = 100;
    private Bitmap.CompressFormat mCompressFormat = Bitmap.CompressFormat.JPEG;

    private DiskLruCache mDiskLruCache = null;
    private static volatile ImageFileCache instance = null;

    private ImageFileCache(Context context) {
        try {
            File cacheDir = getDiskCacheDir(context, CACHE_FOLDER);
            if (!cacheDir.exists()) {
                cacheDir.mkdirs();
            }
            mDiskLruCache = DiskLruCache.open(cacheDir, SysOSAPI.getAppVersionCode(), 1, DISK_CACHE_SIZE);
        } catch (IOException e) {
            e.printStackTrace();
        }
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
        try {
            String key = generateLocalFileName(url);
            MLog.d(TAG, " addBitmapToCache:url = " + url + ",localName = " + key);
            DiskLruCache.Editor editor = mDiskLruCache.edit(key);
            if (editor != null) {
                OutputStream outputStream = editor.newOutputStream(0);
                if (writeBitmapToFile(bitmap, outputStream)) {
                    MLog.d(TAG, "->addBitmapToDiscCache:success");
                    editor.commit();
                } else {
                    MLog.d(TAG, "->addBitmapToDiscCache:failed");
                    editor.abort();
                }
            }
            mDiskLruCache.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private boolean writeBitmapToFile(Bitmap bitmap, OutputStream outputStream) {
        boolean success = false;
        try {
            success = bitmap.compress(mCompressFormat, mCompressQuality, outputStream);
        } catch (Exception e) {
            e.printStackTrace();
            success = false;
        } finally {
            if (outputStream != null) {
                try {
                    outputStream.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
        return success;
    }
    /**
     * 从文件缓存中获得位图实例，同步方法
     */
    public Bitmap getBitmapFromDiskCache(String url) {
        Bitmap bitmap = null;
        try {
            MLog.d(TAG, " getBitmapFromDiskCache:url = " + url);
            String key = generateLocalFileName(url);
            DiskLruCache.Snapshot snapshot = mDiskLruCache.get(key);
            if (snapshot != null) {
                InputStream is = snapshot.getInputStream(0);
                bitmap = BitmapFactory.decodeStream(is);
            }
        } catch (IOException e) {
            e.printStackTrace();
            bitmap = null;
        }
        return bitmap;
    }

    /**
     * 生成文件名
     */
    private String generateLocalFileName(String string) {
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
     * 获取缓存目录，优先使用外置SD卡
     */
    private static File getDiskCacheDir(Context context, String uniqueName) {
        //检查外置SD卡挂载或者内置则使用，否则使用内部存储缓存，关于Android目录http://www.tuicool.com/articles/AvUnqiy
        String cachePath = Environment.getExternalStorageState() == Environment.MEDIA_MOUNTED
                || !Environment.isExternalStorageRemovable()
                ? context.getExternalCacheDir().getPath() : context.getCacheDir().getPath();
        return new File(cachePath + File.separator + uniqueName);
    }

    /**
     * 设置缓存格式
     */
    public void setCacheFormat(Bitmap.CompressFormat format) {
        mCompressFormat = format;
    }

    /**
     * 设置缓存质量
     */
    public void setCacheQuality(int quality) {
        mCompressQuality = quality;
    }
}

