package com.lzh.volleywrap.baseframe.cache;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Environment;

public class DiskLruCache {
    private static final String CACHE_FILENAME_PREFIX = "cache_";
    private static final String CACHE_FOLDER = "ImageCache";

    private static final int INITIAL_CAPACITY = 32;
    private static final float LOAD_FACTOR = 0.75f;
    private static final int MAX_REMOVALS = 4;
    private static final int IO_BUFFER_SIZE = 8 * 1024;//8K

    private int cacheSize = 0;
    private int cacheByteSize = 0;
    private final int maxCacheItemSize = 100;
    private long maxCacheByteSize = 1024 * 1024 * 50; // 50MB

    private final File mCacheDir;
    private int mCompressQuality = 70;
    private Bitmap.CompressFormat mCompressFormat = Bitmap.CompressFormat.JPEG;

    private final Map<String, String> mLinkedHashMap = Collections.synchronizedMap(new LinkedHashMap<String, String>(
            INITIAL_CAPACITY, LOAD_FACTOR, true));

    public static DiskLruCache openCache(Context context, long maxByteSize) {
        return new DiskLruCache(getDiskCacheDir(context, CACHE_FOLDER), maxByteSize);
    }

    private DiskLruCache(File cacheDir, long maxByteSize) {
        mCacheDir = cacheDir;
        maxCacheByteSize = maxByteSize;
    }

    public void put(String key, Bitmap data) {
        synchronized(mLinkedHashMap) {
            if (mLinkedHashMap.get(key) == null) {
                try {
                    final String file = generateFilePath(key);
                    if (writeBitmapToFile(data, file)) {
                        put(key, file);
                        flushCache();
                    }
                } catch (IOException e) {
                }
            }
        }
    }

    private String generateFilePath(String key) {
        try {
            return mCacheDir.getAbsolutePath() + File.separator +
                    CACHE_FILENAME_PREFIX + URLEncoder.encode(key.replace("*", ""), "UTF-8");
        } catch (final UnsupportedEncodingException e) {

        }
        return null;
    }

    private boolean writeBitmapToFile(Bitmap bitmap, String file) throws IOException {
        OutputStream out = null;
        try {
            out = new BufferedOutputStream(new FileOutputStream(file), IO_BUFFER_SIZE);
            return bitmap.compress(mCompressFormat, mCompressQuality, out);
        } finally {
            if (out != null) {
                out.close();
            }
        }
    }

    private void put(String key, String file) {
        mLinkedHashMap.put(key, file);
        cacheSize = mLinkedHashMap.size();
        cacheByteSize += new File(file).length();
    }

    // TODO: 16/2/23 对于之前缓存不在mLinkedHashMap中的磁盘垃圾需要清理
    private void flushCache() {
        Map.Entry<String, String> eldestEntry;
        File eldestFile;
        long eldestFileSize;
        int count = 0;

        while (count < MAX_REMOVALS && (cacheSize > maxCacheItemSize || cacheByteSize > maxCacheByteSize)) {
            eldestEntry = mLinkedHashMap.entrySet().iterator().next();
            eldestFile = new File(eldestEntry.getValue());
            eldestFileSize = eldestFile.length();
            mLinkedHashMap.remove(eldestEntry.getKey());
            eldestFile.delete();
            cacheSize = mLinkedHashMap.size();
            cacheByteSize -= eldestFileSize;
            count++;
        }
    }

    public Bitmap get(String key) {
        synchronized(mLinkedHashMap) {
            final String file = mLinkedHashMap.get(key);
            if (file != null) {
                return BitmapFactory.decodeFile(file);
            } else {
                final String existingFile = generateFilePath(key);
                if (new File(existingFile).exists()) {
                    put(key, existingFile);
                    return BitmapFactory.decodeFile(existingFile);
                }
            }
            return null;
        }
    }

    public void clearCache() {
        clearCache(mCacheDir);
    }

    private static void clearCache(File cacheDir) {
        final File[] files = cacheDir.listFiles(cacheFileFilter);
        for (int i = 0; i < files.length; i++) {
            files[i].delete();
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
        return new File(cachePath + File.separator + uniqueName);
    }

    public void setCompressParams(Bitmap.CompressFormat compressFormat, int quality) {
        mCompressFormat = compressFormat;
        mCompressQuality = quality;
    }
}
