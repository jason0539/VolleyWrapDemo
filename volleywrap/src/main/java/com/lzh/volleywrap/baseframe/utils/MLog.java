package com.lzh.volleywrap.baseframe.utils;

public class MLog {
    public static String TAG = "lzh";
    private static boolean DEBUG = false;
    public static void init(boolean debug){
        DEBUG = debug;
    }
    public static void v(String msg) {
        if (DEBUG) {
            android.util.Log.v(TAG, msg);
        }
    }

    public static void v(String subTag, String msg) {
        if (DEBUG) {
            android.util.Log.v(TAG, subTag + ":\t" + msg);
        }
    }

    public static void d(String msg) {
        if (DEBUG) {
            android.util.Log.d(TAG, msg);
        }
    }

    public static void d(String subTag, String msg) {
        if (DEBUG) {
            android.util.Log.d(TAG, subTag + ":\t" + msg);
        }
    }

    public static void d(String msg, Throwable tr) {
        if (DEBUG) {
            android.util.Log.d(TAG, msg, tr);
        }
    }

    public static void i(String msg) {
        if (DEBUG) {
            android.util.Log.i(TAG, msg);
        }
    }

    public static void i(String subTag, String msg) {
        if (DEBUG) {
            android.util.Log.i(TAG, subTag + ":\t" + msg);
        }
    }

    public static void w(String msg) {
        if (DEBUG) {
            android.util.Log.w(TAG, msg);
        }
    }

    public static void w(String subTag, String msg) {
        if (DEBUG) {
            android.util.Log.w(TAG, subTag + ":\t" + msg);
        }
    }

    public static void w(String msg, Throwable tr) {
        if (DEBUG) {
            android.util.Log.w(TAG, msg, tr);
        }
    }

    public static void e(String msg) {
        if (DEBUG) {
            android.util.Log.e(TAG, msg);
        }
    }

    public static void e(String subTag, String msg) {
        if (DEBUG) {
            android.util.Log.e(TAG, subTag + ":\t" + msg);
        }
    }

    public static void e(String msg, Throwable tr) {
        if (DEBUG) {
            android.util.Log.e(TAG, msg, tr);
        }
    }

}