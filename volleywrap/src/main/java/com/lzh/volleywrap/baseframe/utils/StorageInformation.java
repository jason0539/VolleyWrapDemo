package com.lzh.volleywrap.baseframe.utils;

import android.annotation.SuppressLint;
import android.os.Build;
import android.os.Environment;
import android.os.StatFs;

public class StorageInformation {
    private static final String TAG = StorageInformation.class.getSimpleName();

    private final boolean removeable;
    /**
     * 存储空间的根目录
     */
    private final String rootPath;

    /**
     * 存储空间的label
     */
    private final String label;

    public StorageInformation(String rootPath, boolean removeable, String label) {
        this.rootPath = rootPath;
        this.removeable = removeable;
        this.label = label;
    }

    public StorageInformation() {
        this.removeable = false;
        this.rootPath = Environment.getExternalStorageDirectory().getAbsolutePath();
        this.label = "";
    }

    public boolean isRemoveable() {
        return removeable;
    }

    /**
     * 获取sdcard跟目录
     */
    public String getRootPath() {
        return rootPath;
    }

    /**
     * 存储的Label。
     */
    public String getLabel() {
        return label;
    }

    /**
     * 获取可用存储空间大小。
     */
    @SuppressWarnings("deprecation")
    @SuppressLint("NewApi")
    public long getAvailableBytes() {
        try {
            StatFs stat = new StatFs(rootPath);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR2) {
                return stat.getAvailableBytes();
            } else {
                return ((long) stat.getBlockSize()) * stat.getAvailableBlocks();
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            return -1;
        }
    }

    @Override
    public boolean equals(Object other) {
        if (other == null || !StorageInformation.class.isInstance(other)) {
            return false;
        }
        StorageInformation storage = (StorageInformation) other;
        return this.rootPath.equals(storage.rootPath);
    }
}
