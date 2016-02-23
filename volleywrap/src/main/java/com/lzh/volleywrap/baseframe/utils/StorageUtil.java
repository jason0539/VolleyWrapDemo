package com.lzh.volleywrap.baseframe.utils;

import java.io.File;

import android.content.Context;
import android.text.TextUtils;

/**
 * 存储工具类
 */
public class StorageUtil {
    private static final String TAG = StorageUtil.class.getSimpleName();

    private StorageSettings mStorageSetting;

    private String mAppFolder;//应用根目录
    private String mTempFolder;//临时目录
    private String mDownloadFolder;//下载目录
    private String mImageCacheFolder;//图片缓存

    public void init(Context context) {
        mStorageSetting = new StorageSettings(context, AppConstant.APP_FOLDER_NAME, AppConstant.PREFERENCE_FOLDER_NAME);
    }

    public String getAppFolder() {
        if (TextUtils.isEmpty(mAppFolder)) {
            mAppFolder = mStorageSetting.getCurrentStorage().getRootPath();
            mAppFolder += File.separator + AppConstant.APP_FOLDER_NAME;
            ensureFolderExists(mAppFolder);
        }
        return mAppFolder;
    }

    public String getTempFolder() {
        if (TextUtils.isEmpty(mTempFolder)) {
            mTempFolder = getAppFolder();
            mTempFolder += File.separator + AppConstant.CACHE_FOLDER_NAME;
            ensureFolderExists(mTempFolder);
        }
        return mTempFolder;
    }

    public String getImageCacheFolder() {
        if (TextUtils.isEmpty(mImageCacheFolder)) {
            mImageCacheFolder = getAppFolder();
            mImageCacheFolder += File.separator + AppConstant.IMAGE_CACHE_FOLDER_NAME;
            ensureFolderExists(mImageCacheFolder);
        }
        return mImageCacheFolder;
    }

    public String getDownloadFolder() {
        if (TextUtils.isEmpty(mDownloadFolder)) {
            mDownloadFolder = getAppFolder();
            mDownloadFolder += File.separator + AppConstant.DOWNLOAD_FOLDER_NAME;
            ensureFolderExists(mDownloadFolder);
        }
        return mDownloadFolder;
    }

    public long getAvailableBytes() {
        return mStorageSetting.getCurrentStorage().getAvailableBytes();
    }

    /**
     * 根据路径删除指定的目录或文件
     */
    public boolean delete(String sPath) {
        File file = new File(sPath);
        if (!file.exists()) {
            return true;
        } else {
            return file.isFile() ? deleteFile(sPath) : deleteDirectory(sPath);
        }
    }

    /**
     * 删除目录（文件夹）以及目录下的文件
     */
    private boolean deleteDirectory(String sPath) {
        if (!sPath.endsWith(File.separator)) {
            sPath = sPath + File.separator;
        }
        File dirFile = new File(sPath);
        if (!dirFile.exists()) {
            return true;
        }
        File[] files = dirFile.listFiles();
        //不是文件夹或者空文件夹
        if (files == null || files.length <= 0) {
            return dirFile.delete();
        }
        boolean flag = true;
        for (int i = 0; i < files.length; i++) {
            if (files[i].isFile()) {
                flag = deleteFile(files[i].getAbsolutePath());
                if (!flag) {
                    break;
                }
            } else {
                flag = deleteDirectory(files[i].getAbsolutePath());
                if (!flag) {
                    break;
                }
            }
        }
        return flag;
    }

    /**
     * 删除单个文件
     */
    private boolean deleteFile(String sPath) {
        File file = new File(sPath);
        if (!file.exists()) {
            return true;
        }
        boolean flag = false;
        if (file.isFile()) {
            flag = file.delete();
        }
        return flag;
    }

    /**
     * 检查文件夹是否存在，不在则创建
     */
    public void ensureFolderExists(String path) {
        File folder = new File(path);
        if (!folder.exists()) {
            folder.mkdirs();
        }
    }

    private static volatile StorageUtil instance = null;

    public static StorageUtil getInstance() {
        if (instance == null) {
            synchronized(StorageUtil.class) {
                if (instance == null) {
                    instance = new StorageUtil();
                }
            }
        }
        return instance;
    }

    private StorageUtil() {
    }
}

