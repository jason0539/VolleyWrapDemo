package com.lzh.volleywrap.baseframe.utils;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.os.Bundle;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.view.Display;
import android.view.WindowManager;

/**
 * 手机和APP信息
 */
public class SysOSAPI {
    private static final String TAG = SysOSAPI.class.getSimpleName();

    private static Context myContext;

    //手机硬件信息
    private static int myScreenWidth;
    private static int myScreenHeight;
    private static int myDensityDpi;
    private static String myDeviceId;
    private static String myDeviceModel;
    private static String myOSVersion;
    private static String myDeviceMac;
    private static String myDeviceBrand;

    //APP信息
    private static String myAppName;
    private static String myPackageName;
    private static String myAppVersionName;
    private static byte[] myAppPackageSignature;
    private static int myAppVersionCode;

    public static void init(Context context) {
        myContext = context;
        myDeviceBrand = Build.MANUFACTURER;
        myDeviceModel = Build.MODEL;
        myOSVersion = "Android" + Build.VERSION.SDK;
        myPackageName = context.getPackageName();
        initDeviceId(context);
        initPackageSignature(context);
        initDpiInfo(context);
        initMacNum(context);
        initAppName(context);
        initAppVersion(context);
    }

    private static void initDeviceId(Context context) {
        String deviceId = null;
        try {
            TelephonyManager tm = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
            if (tm != null) {
                deviceId = tm.getDeviceId();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        myDeviceId = TextUtils.isEmpty(deviceId) ? "000000000000000" : deviceId;
    }

    private static void initPackageSignature(Context context) {
        byte[] data = null;
        try {
            Signature[] sigs = context.getPackageManager()
                    .getPackageInfo(context.getPackageName(), PackageManager.GET_SIGNATURES).signatures;
            data = sigs[0].toByteArray();
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        myAppPackageSignature = data == null ? null : data;
    }

    private static void initDpiInfo(Context context) {
        WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        DisplayMetrics outMetrics = new DisplayMetrics();
        Display display = null;
        if (wm != null) {
            display = wm.getDefaultDisplay();
        }

        if (display != null) {
            myScreenWidth = context.getResources().getDisplayMetrics().widthPixels;
            myScreenHeight = context.getResources().getDisplayMetrics().heightPixels;
            display.getMetrics(outMetrics);
            myDensityDpi = outMetrics.densityDpi;
        }
    }

    private static void initMacNum(Context context) {
        TelephonyManager manager = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
        if (manager != null) {
            WifiManager wifi = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
            WifiInfo info = wifi.getConnectionInfo();
            myDeviceMac = info.getMacAddress();
        }
    }

    private static void initAppName(Context context) {
        PackageManager packageManager = null;
        ApplicationInfo applicationInfo = null;
        try {
            packageManager = context.getPackageManager();
            applicationInfo = packageManager.getApplicationInfo(getPackageName(), 0);
        } catch (PackageManager.NameNotFoundException e) {
            applicationInfo = null;
        }
        myAppName = (String) packageManager.getApplicationLabel(applicationInfo);
    }

    private static void initAppVersion(Context context) {
        PackageManager manager = context.getPackageManager();
        try {
            PackageInfo info = manager.getPackageInfo(context.getPackageName(), 0);
            myAppVersionName = info.versionName;
            myAppVersionCode = info.versionCode;
        } catch (PackageManager.NameNotFoundException e) {
            myAppVersionName = "1.0.0";
            myAppVersionCode = 1;
        }
    }

    public static Bundle getPhoneInfoBundle() {
        Bundle b = new Bundle();
        b.putString("dm", getDeviceModel());
        b.putString("dos", getOSVersion());
        b.putString("did", getDeviceId());
        b.putString("dbd", getDeviceBrand());
        b.putString("dmc", getDeviceMac());
        b.putInt("dpi", getDensityDpi());
        b.putString("net", NetworkUtil.getNetworkStatus(myContext));
        b.putInt("dsc_x", SysOSAPI.getScreenWidth());
        b.putInt("dsc_y", SysOSAPI.getScreenHeight());
        return b;
    }

    public static Bundle getAppInfoBundle() {
        Bundle b = new Bundle();
        b.putString("avn", getAppVersionName());
        b.putByteArray("asign", getPackageSignature());
        b.putString("apcn", getPackageName());
        b.putString("aname", getAppName());
        b.putInt("avc", getAppVersionCode());
        return b;
    }

    public static byte[] getPackageSignature() {
        return myAppPackageSignature;
    }

    public static String getDeviceId() {
        return myDeviceId;
    }

    /**
     * 手机型号
     */
    public static String getDeviceModel() {
        return myDeviceModel;
    }

    /**
     * 手机生产商
     */
    public static String getDeviceBrand() {
        return myDeviceBrand;
    }

    /**
     * APP版本号
     */
    public static String getAppVersionName() {
        return myAppVersionName;
    }

    /**
     * 系统版本号
     */
    public static String getOSVersion() {
        return myOSVersion;
    }

    public static int getDensityDpi() {
        return myDensityDpi;
    }

    public static int getScreenWidth() {
        return myScreenWidth;
    }

    public static int getScreenHeight() {
        return myScreenHeight;
    }

    public static String getPackageName() {
        return myPackageName;
    }

    public static String getDeviceMac() {
        return myDeviceMac;
    }

    public static String getAppName() {
        return myAppName;
    }

    public static int getAppVersionCode() {
        return myAppVersionCode;
    }

}

