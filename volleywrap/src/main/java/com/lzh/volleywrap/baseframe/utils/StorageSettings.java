package com.lzh.volleywrap.baseframe.utils;

import java.io.File;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Environment;
import android.os.storage.StorageManager;
import android.text.TextUtils;

public class StorageSettings {
    private static final String TAG = StorageSettings.class.getSimpleName();

    private static final String PREFFERED_SD_CARD = "PREFFERED_SD_CARD";

    /**
     * 是否已初始化。
     */
    private boolean initialized = false;

    /**
     * 全部可用的存储空间，列表的第一个元素固定为内置存储卡（Android 4.0以上一定为内置SD卡，以下无法保证），如设备有更多可用的存储卡，在后面依次列出。
     */
    private final List<StorageInformation> allStorages = new ArrayList<>();

    /**
     * 当前使用的存储空间。
     */
    private StorageInformation currentStorage = null;

    /**
     * 从Android 4.4版本起，不允许APP写外置SD卡 根目录。<p>
     * 因此，在Android 4.4以上版本启动时，若检测到使用的首选存储空间为外置SD卡根目录，且该目录（如/storage/extSdCard）下有数据文件，
     * 将其拷贝至新的目录（如/storage/extSdCard/Android/data/com.lzh.demo/files）。
     */
    private String deprecatedDataPath;
    private String appFolder;
    private String prefFolder;

    public StorageSettings(Context context, String appFolder, String prefFolder) {
        initialize(context, appFolder, prefFolder);
    }

    /**
     * 初始化。
     */
    public void initialize(Context context, String appFolder, String prefFolder) {
        if (initialized) {
            return;
        }
        initialized = true;

        this.appFolder = appFolder;
        this.prefFolder = prefFolder;

        // 获取全部存储空间列表。
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH) {
                getAllStoragesV14(context);
            } else {
                getAllStoragesV7();
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }

        try {
            if (allStorages.size() > 0) {
                StorageInformation dataStorage = null;
                int dataCount = 0;
                for (StorageInformation storage : allStorages) {
                    StringBuilder sb = new StringBuilder(storage.getRootPath());
                    sb.append(File.separator).append(appFolder);
                    if (new File(sb.toString()).exists()) {
                        dataCount++;
                        dataStorage = storage;
                    }
                }

                if (dataCount == 0) {
                    currentStorage = getPreferredStorage(context);
                    if (currentStorage == null) {
                        for (StorageInformation storage : allStorages) {
                            if (setPreferredStorage(context, storage)) {
                                currentStorage = storage;
                                break;
                            }
                        }
                    }
                } else if (dataCount == 1) {
                    if (setPreferredStorage(context, dataStorage)) {
                        currentStorage = dataStorage;
                    }
                } else {
                    currentStorage = getPreferredStorage(context);
                }
                if (currentStorage == null) {
                    currentStorage = allStorages.get(0);
                }
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    /**
     * 由于从Android 4.4版本开始，APP只能写外置SD卡的指定目录，故：
     * <li>对Android 4.0以上4.4以下的版本，所有存储路径都通过StorageManager.getVolumeList()获取；
     * <li>对Android 4.4及以上的版本，内置SD卡路径通过StorageManager.getVolumeList()获取，外置SD卡路径通过Context.getExternalFilesDirs()获取；
     * <li>对Android 4.4及以上的版本，还需要检测当前是否首选使用外置SD卡根目录（如/storage/extSdCard）且该目录有数据文件，如是则需要将其移动至新的目录（如/storage/extSdCard
     * /Android/data/com.lzh.demo/files）。
     *
     * @param context
     */
    @SuppressLint("NewApi")
    @TargetApi(14)
    private void getAllStoragesV14(Context context) {
        try {
            //反射获取内置SD卡
            StorageManager manager = (StorageManager) context.getSystemService(Context.STORAGE_SERVICE);
            Method getVolumeList = manager.getClass().getMethod("getVolumeList");
            Method getVolumeState = manager.getClass().getMethod("getVolumeState", String.class);
            Class<?> storageVolume = Class.forName("android.os.storage.StorageVolume");
            Method isRemovable = storageVolume.getMethod("isRemovable");
            Method getPath = storageVolume.getMethod("getPath");
            Object[] volumes = (Object[]) getVolumeList.invoke(manager);
            if (volumes != null) {
                for (Object volume : volumes) {
                    String path = (String) getPath.invoke(volume);
                    if (path != null && path.length() > 0 && "mounted".equals(getVolumeState.invoke(manager, path))) {
                        boolean isPrimary = !((Boolean) isRemovable.invoke(volume)).booleanValue();
                        if ((Build.VERSION.SDK_INT <= Build.VERSION_CODES.KITKAT || isPrimary) && isWritable(path)) {
                            // 有些手机上（如中兴nubia系统），通过反射可以获取到外置，也可写，但是通过api获取不到，所以这里只要可写，就都读出来，后面再跟api获取的合并。
                            allStorages.add(new StorageInformation(path, !isPrimary, (isPrimary ? "内置存储卡" : "外置存储卡")));
                        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                            if (new File(path + File.separator + appFolder).exists()
                                    && path.equals(context.getSharedPreferences(prefFolder, Context.MODE_PRIVATE)
                                    .getString(PREFFERED_SD_CARD, ""))) {
                                deprecatedDataPath = path + File.separator + appFolder;
                            }
                        }
                    }
                }
                //通过API获取外置SD卡m
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                    File externalStorageFiles[] = context.getExternalFilesDirs(null);
                    List<StorageInformation> tmp = new ArrayList<>();
                    tmp.addAll(allStorages);
                    // 有些手机上（如中兴nubia系统），通过反射可以获取到外置，也可写，但是通过api获取不到，所以这里合并一下。
                    for (int i = 0; i < externalStorageFiles.length; i++) {
                        if (externalStorageFiles[i] == null) {
                            break;
                        }
                        String path = externalStorageFiles[i].getAbsolutePath();
                        boolean exists = false;
                        for (StorageInformation storage : allStorages) {
                            if (path.startsWith(storage.getRootPath())) {
                                exists = true;
                                break;
                            }
                        }
                        if (!exists && path.indexOf(AppConstant.APP_PACKAGE_NAME) != -1) {
                            tmp.add(new StorageInformation(path, true, "外置存储卡"));
                        }
                    }
                    allStorages.clear();
                    allStorages.addAll(tmp);
                }
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    /**
     * 对Android 4.0以下的版本，读取系统/proc/mounts和/system/etc/vold.fstab文件确定SD卡路径。
     */
    private void getAllStoragesV7() {
        Scanner scanner = null;
        List<String> mounts = new ArrayList<>();
        List<String> volds = new ArrayList<>();
        try {
            // 读取/proc/mounts文件
            File mountsFile = new File("/proc/mounts");
            if (mountsFile.exists()) {
                scanner = new Scanner(mountsFile);
                while (scanner.hasNext()) {
                    String line = scanner.nextLine();
                    if (line.startsWith("/dev/block/vold/")) {
                        line = line.replace('\t', ' ');
                        String[] lineElements = line.split(" ");
                        if (lineElements != null && lineElements.length > 0) {
                            mounts.add(lineElements[1]);
                        }
                    }
                }
                scanner.close();
                scanner = null;
            }
            // 读取/system/etc/vold.fstab文件
            File voldFile = new File("/system/etc/vold.fstab");
            if (voldFile.exists()) {
                scanner = new Scanner(voldFile);
                while (scanner.hasNext()) {
                    String line = scanner.nextLine();
                    if (line.startsWith("dev_mount")) {
                        line = line.replace('\t', ' ');
                        String[] lineElements = line.split(" ");
                        if (lineElements != null && lineElements.length > 0) {
                            String element = lineElements[2];
                            if (element.contains(":")) {
                                element = element.substring(0, element.indexOf(":"));
                            }
                            volds.add(element);
                        }
                    }
                }
                scanner.close();
                scanner = null;
            }
            // 内置SD卡选择Environment.getExternalStorageDirectory()读到的目录，外置SD卡选择/proc/mounts和/system/etc/vold.fstab中都读到的目录
            String primaryStoragePath = Environment.getExternalStorageDirectory().getAbsolutePath();
            allStorages.add(new StorageInformation(primaryStoragePath, false, "Auto"));
            for (String path : mounts) {
                if (volds.contains(path) && !path.equals(primaryStoragePath)) {
                    File file = new File(path);
                    if (file.exists() && file.isDirectory() && file.canWrite()) {
                        allStorages.add(new StorageInformation(path, false, "Auto"));
                    }
                }
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        } finally {
            if (scanner != null) {
                scanner.close();
            }
        }
    }

    /**
     * 获取当前使用的存储空间，不为<code>null</code>。
     *
     * @return
     */
    public StorageInformation getCurrentStorage() {
        return currentStorage;
    }

    /**
     * 获取全部可用的存储空间，列表的第一个元素固定为内置存储卡（Android 4.0以上一定为内置SD卡，以下无法保证），如设备有更多可用的存储卡，在后面依次列出。
     */
    public List<StorageInformation> getAllStorages() {
        return allStorages;
    }

    public String getDeprecatedDataPath() {
        return deprecatedDataPath;
    }

    /**
     * 获取首选的存储空间，可能为<code>null</code>。
     *
     * @return 之前设置的首选存储空间，如未设置或设置不可用，返回<code>null</code>。
     */
    public StorageInformation getPreferredStorage(Context context) {
        SharedPreferences preferences = context.getSharedPreferences(prefFolder, Context.MODE_PRIVATE);
        String path = preferences.getString(PREFFERED_SD_CARD, "");
        if (!TextUtils.isEmpty(path)) {
            for (StorageInformation storage : allStorages) {
                StringBuilder sb = new StringBuilder(storage.getRootPath());
                sb.append(File.separator).append(appFolder);
                if (sb.toString().equals(path)) {
                    return storage;
                }
            }
        }
        return null;
    }

    /**
     * 设置首选的存储空间。
     *
     * @return 设置成功返回true，失败返回false。
     */
    public boolean setPreferredStorage(Context context, StorageInformation preferredStorage) {
        String path = preferredStorage.getRootPath() + File.separator + appFolder;
        File file = new File(path);
        if (!file.exists() && !file.mkdirs()) {
            return false;
        }
        if (!isWritable(path)) {
            return false;
        }
        SharedPreferences preferences = context.getSharedPreferences(prefFolder, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString(PREFFERED_SD_CARD, path);
        return editor.commit();
    }

    private boolean isWritable(String path) {
        boolean isCanWrite = false;
        try {
            File testFile = new File(path + "/test.0");
            if (testFile.exists()) {
                testFile.delete();
            }
            isCanWrite = testFile.createNewFile();
            if (testFile.exists()) {
                testFile.delete();
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return isCanWrite;
    }
}

