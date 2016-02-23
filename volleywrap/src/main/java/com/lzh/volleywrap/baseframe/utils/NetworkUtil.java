package com.lzh.volleywrap.baseframe.utils;

import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.Enumeration;

import org.apache.http.conn.util.InetAddressUtils;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiManager;
import android.telephony.TelephonyManager;

public class NetworkUtil {
    private static final String TAG = NetworkUtil.class.getSimpleName();

    public static final int NETYPE_NOCON = -1; //无连接，用于区分断网和未知类型，方便统计
    public static final int NETYPE_UNKNOWN = 0; //未知网络类型
    public static final int NETYPE_WIFI = 1; //WiFi连接
    public static final int NETYPE_2G = 2; //2G
    public static final int NETYPE_3G = 3; //3G
    public static final int NETYPE_4G = 4; //4G
    public static final int NETYPE_TELECOM_2G = 5; //电信2G(IS95A或者IS95B)
    public static final int NETYPE_MOBILE_UNICOM_2G = 6; //移动或联通2G
    public static final int NETYPE_TELECOM_3G = 7; //电信3G
    public static final int NETYPE_MOBILE_3G = 8; //移动3G
    public static final int NETYPE_UNICOM_3G = 9; //联通3G
    public static final int NETYPE_4G_UNKNOWN = 10;//4G?

    /*
    public static final int NETWORK_TYPE_UNKNOWN = 0;
    public static final int NETWORK_TYPE_GPRS = 1;
    public static final int NETWORK_TYPE_EDGE = 2;
    public static final int NETWORK_TYPE_UMTS = 3;
    public static final int NETWORK_TYPE_CDMA = 4;
    public static final int NETWORK_TYPE_EVDO_0 = 5;
    public static final int NETWORK_TYPE_EVDO_A = 6;
    public static final int NETWORK_TYPE_1xRTT = 7;
    public static final int NETWORK_TYPE_HSDPA = 8;
    public static final int NETWORK_TYPE_HSUPA = 9;
    public static final int NETWORK_TYPE_HSPA = 10;
    public static final int NETWORK_TYPE_IDEN = 11;
    public static final int NETWORK_TYPE_EVDO_B = 12;
    public static final int NETWORK_TYPE_LTE = 13;
    public static final int NETWORK_TYPE_EHRPD = 14;
    public static final int NETWORK_TYPE_HSPAP = 15;
     */

    //wap 代理
    public static boolean mUseProxy = false;
    public static String mProxyHost = "";
    public static int mProxyPort = 0;

    public static int getCurrentNetModeInInteger(Context context) {
        int netType = NETYPE_NOCON;

        NetworkInfo info = NetworkUtil.getActiveNetworkInfo(context);
        if (null != info) {
            if (info.getType() == ConnectivityManager.TYPE_WIFI) {
                netType = NETYPE_WIFI;
            } else {
                TelephonyManager tm = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
                int type = tm.getNetworkType();
                switch (type) {
                    case TelephonyManager.NETWORK_TYPE_IDEN:
                        netType = NETYPE_2G;
                        break;
                    case TelephonyManager.NETWORK_TYPE_LTE:
                        netType = NETYPE_4G;
                        break;
                    case TelephonyManager.NETWORK_TYPE_CDMA:
                        netType = NETYPE_TELECOM_2G;
                        break;
                    case TelephonyManager.NETWORK_TYPE_EDGE:
                    case TelephonyManager.NETWORK_TYPE_GPRS:
                        netType = NETYPE_MOBILE_UNICOM_2G;
                        break;
                    case TelephonyManager.NETWORK_TYPE_1xRTT:
                    case TelephonyManager.NETWORK_TYPE_EVDO_0:
                    case TelephonyManager.NETWORK_TYPE_EVDO_A:
                    case TelephonyManager.NETWORK_TYPE_EVDO_B:
                        netType = NETYPE_TELECOM_3G;
                        break;
                    case TelephonyManager.NETWORK_TYPE_HSDPA:
                        netType = NETYPE_MOBILE_3G;
                        break;
                    case TelephonyManager.NETWORK_TYPE_HSPA:
                    case TelephonyManager.NETWORK_TYPE_HSUPA:
                    case TelephonyManager.NETWORK_TYPE_UMTS:
                    case TelephonyManager.NETWORK_TYPE_HSPAP:
                        netType = NETYPE_UNICOM_3G;
                        break;
                    case TelephonyManager.NETWORK_TYPE_EHRPD:
                        netType = NETYPE_4G_UNKNOWN;
                        break;
                    default:
                        netType = NETYPE_UNKNOWN;
                        break;
                }
            }
        } else {
            netType = NETYPE_NOCON;
        }
        return netType;
    }

    /**
     * 获取当前活动的网络连接
     *
     * @return 活动的连接信息，可能为null
     */
    public static NetworkInfo getActiveNetworkInfo(Context context) {
        if (context == null) {
            return null;
        }
        ConnectivityManager manager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeInfo = null;
        try {
            activeInfo = manager.getActiveNetworkInfo();
        } catch (Exception e) {
        }
        return activeInfo;
    }

    /**
     * 获取所有NetworkInfo
     *
     * @return
     */
    public static NetworkInfo[] getAllNetworkInfo(Context context) {
        if (context == null) {
            return null;
        }
        ConnectivityManager manager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfos[] = null;
        try {
            networkInfos = manager.getAllNetworkInfo();
        } catch (Exception e) {
        }
        return networkInfos;
    }

    public static String getCurrentNetMode(Context context) {
        return Integer.toString(getCurrentNetModeInInteger(context));
    }

    public static String getNetworkStatus(Context context) {
        String status = "";
        int mode = NetworkUtil.getCurrentNetModeInInteger(context);
        switch (mode) {
            case NetworkUtil.NETYPE_WIFI:
                status += "1";
                break;
            case NetworkUtil.NETYPE_2G:
            case NetworkUtil.NETYPE_MOBILE_UNICOM_2G:
            case NetworkUtil.NETYPE_TELECOM_2G:
                status += "2";
                break;
            case NetworkUtil.NETYPE_TELECOM_3G:
            case NetworkUtil.NETYPE_MOBILE_3G:
            case NetworkUtil.NETYPE_UNICOM_3G:
                status += "3";
                break;
            case NetworkUtil.NETYPE_4G:
            case NetworkUtil.NETYPE_4G_UNKNOWN:
                status += "4";
                break;
            default:
                status += "0";
        }
        return status;
    }

    /**
     * 判断网络是否可用
     */
    public static boolean isNetworkAvailable(Context context) {
        try {
            if (isWifiState(context)) {
                return true;
            } else {
                ConnectivityManager cManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
                NetworkInfo info = cManager.getActiveNetworkInfo();
                return info != null && info.isConnectedOrConnecting();
            }
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * WIFI是否连接
     */
    public static boolean isWifiConnected(Context context) {

        if (context == null) {
            return false;
        }

        boolean isWifiConnected = false;
        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connectivityManager != null) {
                isWifiConnected = isWifiConnected(connectivityManager.getActiveNetworkInfo());
        }
        return isWifiConnected;
    }

    /**
     * 判断传入的 NetWorkInfo是否是wifi已连接
     * @return 是WIFI且已连接 返回true，否则false
     */
    private static boolean isWifiConnected(NetworkInfo activeNetInfo) {
        boolean isWifiConnected = false;
        try {
            if (activeNetInfo != null) {
                isWifiConnected =
                        ConnectivityManager.TYPE_WIFI == activeNetInfo.getType() && activeNetInfo.isConnected();
            }
        } catch (Exception e) {
            // ignore
        }
        return isWifiConnected;
    }

    /**
     * 判断wifi状态
     */
    public static boolean isWifiState(Context context) {
        if (context == null) {
            return false;
        }
        WifiManager wifiManager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
        int wifiState = -1;
        try {
            wifiState = wifiManager.getWifiState();
        } catch (Exception e) {
            // 防止 NPE 错误
        }
        return wifiState == WifiManager.WIFI_STATE_ENABLED;
    }

    /**
     * 获取ip地址
     */
    public static String getIpAddress() {
        try {
            for (Enumeration<NetworkInterface> en = NetworkInterface.getNetworkInterfaces(); en.hasMoreElements(); ) {
                NetworkInterface intf = en.nextElement();
                for (Enumeration<InetAddress> enumIpAddr = intf
                        .getInetAddresses(); enumIpAddr.hasMoreElements(); ) {
                    InetAddress inetAddress = enumIpAddr.nextElement();
                    MLog.d(TAG, " getIpAddress :" + inetAddress.getHostAddress());
                    if (!inetAddress.isLoopbackAddress()&& InetAddressUtils.isIPv4Address(inetAddress.getHostAddress())) {
                        MLog.d(TAG, " get Ip = "+inetAddress.getHostAddress());
                        return inetAddress.getHostAddress().toString();
                    }
                }
            }
        } catch (SocketException ex) {
            return "127.0.0.1";
        }
        return "127.0.0.1";
    }
}