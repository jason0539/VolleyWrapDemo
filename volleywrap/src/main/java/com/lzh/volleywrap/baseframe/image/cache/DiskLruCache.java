package com.lzh.volleywrap.baseframe.image.cache;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FilenameFilter;
import java.io.OutputStream;
import java.net.URLEncoder;
import java.util.LinkedHashMap;
import java.util.Map;

import com.lzh.volleywrap.baseframe.utils.MLog;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Environment;
import android.text.TextUtils;

public class DiskLruCache {
    private static final String TAG = DiskLruCache.class.getSimpleName();

    private static final String CACHE_FILENAME_PREFIX = "cache_";
    private static final String CACHE_FOLDER = "ImageCache";

    private static final int INITIAL_CAPACITY = 32;
    private static final float LOAD_FACTOR = 0.75f;
    private static final int MAX_REMOVALS = 4;
    private static final int IO_BUFFER_SIZE = 24 * 1024;//24K

    private int cacheByteSize = 0;
    private long maxCacheByteSize = 1024 * 1024 * 100; // 100MB

    private final File mCacheDir;
    private int mCompressQuality = 70;
    private final Object mDiskCacheLock = new Object();
    private Bitmap.CompressFormat mCompressFormat = Bitmap.CompressFormat.JPEG;

    private final Map<String, String> mLinkedHashMap = new LinkedHashMap<>(INITIAL_CAPACITY, LOAD_FACTOR, true);

    public static DiskLruCache openCache(Context context, long maxByteSize) {
        return new DiskLruCache(getDiskCacheDir(context, CACHE_FOLDER), maxByteSize);
    }

    private DiskLruCache(File cacheDir, long maxByteSize) {
        mCacheDir = cacheDir;
        maxCacheByteSize = maxByteSize;
    }

    /**
     * 将文件缓存到本地
     */
    public void put(String fileName, Bitmap data) {
        synchronized(mDiskCacheLock) {
            if (contains(fileName)) {
                return;
            }
            String file = generateFilePath(fileName);
            if (writeBitmapToFile(data, file)) {
                addToHashMap(fileName, file);
                flushCache();
            }
        }
    }

    /**
     * 文件已缓存，加入缓存目录
     */
    private void addToHashMap(String fileName, String filePath) {
        mLinkedHashMap.put(fileName, filePath);
        cacheByteSize += new File(filePath).length();
    }

    private void removeFromHashMap(String key) {
        mLinkedHashMap.remove(key);
    }

    private boolean writeBitmapToFile(Bitmap bitmap, String file) {
        MLog.d(TAG, " writeBitmapToFile path = " + file);
        OutputStream out = null;
        boolean success = false;
        try {
            out = new BufferedOutputStream(new FileOutputStream(file), IO_BUFFER_SIZE);
            success = bitmap.compress(mCompressFormat, mCompressQuality, out);
        } catch (Exception e) {
            e.printStackTrace();
            success = false;
        } finally {
            if (out != null) {
                try {
                    out.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
        return success;
    }

    /**
     * 获取缓存的文件
     */
    public Bitmap get(String fileName) {
        MLog.d(TAG, " get:fileName = " + fileName);
        synchronized(mDiskCacheLock) {
            if (contains(fileName)) {
                String filePath = mLinkedHashMap.get(fileName);
                if (TextUtils.isEmpty(filePath)) {
                    filePath = generateFilePath(fileName);
                    if (isFileExits(filePath)) {
                        MLog.d(TAG, " file exists = " + filePath);
                        addToHashMap(fileName, filePath);
                        return BitmapFactory.decodeFile(filePath);
                    }
                } else {
                    MLog.d(TAG, " file != null");
                    if (isFileExits(filePath)) {
                        return BitmapFactory.decodeFile(filePath);
                    } else {
                        removeFromHashMap(fileName);
                    }
                }
            }
            return null;
        }
    }

    private boolean isFileExits(String path) {
        return new File(path).exists();
    }

    /**
     * 是否已缓存文件
     */
    private boolean contains(String fileName) {
        boolean has = false;
        String file = mLinkedHashMap.get(fileName);
        if (file != null) {
            has = true;
        } else {
            String filePath = generateFilePath(fileName);
            if (isFileExits(filePath)) {
                has = true;
                addToHashMap(fileName, filePath);
            } else {
                has = false;
            }
        }
        return has;
    }

    private String generateFilePath(String key) {
        try {
            return mCacheDir.getAbsolutePath() + File.separator + CACHE_FILENAME_PREFIX + URLEncoder
                    .encode(key.replace("*", ""), "UTF-8");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    // TODO: 16/2/23 对于之前缓存不在mLinkedHashMap中的磁盘垃圾需要清理
    private void flushCache() {
        Map.Entry<String, String> eldestEntry;
        File eldestFile;
        long eldestFileSize;
        int count = 0;

        while (count < MAX_REMOVALS && (cacheByteSize > maxCacheByteSize)) {
            eldestEntry = mLinkedHashMap.entrySet().iterator().next();
            eldestFile = new File(eldestEntry.getValue());
            eldestFileSize = eldestFile.length();
            mLinkedHashMap.remove(eldestEntry.getKey());
            eldestFile.delete();
            cacheByteSize -= eldestFileSize;
            count++;
        }
    }

    public void clearCache() {
        clearCache(mCacheDir);
    }

    private void clearCache(File cacheDir) {
        synchronized(mDiskCacheLock) {
            File[] files = cacheDir.listFiles(cacheFileFilter);
            for (int i = 0; i < files.length; i++) {
                files[i].delete();
            }
        }
    }

    private static final FilenameFilter cacheFileFilter = new FilenameFilter() {
        @Override
        public boolean accept(File dir, String filename) {
            return filename.startsWith(CACHE_FILENAME_PREFIX);
        }
    };

    /**
     * 获取缓存目录，优先使用外置SD卡
     */
    private static File getDiskCacheDir(Context context, String uniqueName) {
        //检查外置SD卡挂载或者内置则使用，否则使用内部存储缓存，关于Android目录http://www.tuicool.com/articles/AvUnqiy
        String cachePath = Environment.getExternalStorageState() == Environment.MEDIA_MOUNTED || !Environment
                .isExternalStorageRemovable()
                ? context.getExternalCacheDir().getPath() : context.getCacheDir().getPath();
        File cacheFile = new File(cachePath + File.separator + uniqueName);
        if (!cacheFile.exists()) {
            cacheFile.mkdirs();
        }
        return cacheFile;
    }

    public void setCompressParams(Bitmap.CompressFormat compressFormat, int quality) {
        mCompressFormat = compressFormat;
        mCompressQuality = quality;
    }
}
