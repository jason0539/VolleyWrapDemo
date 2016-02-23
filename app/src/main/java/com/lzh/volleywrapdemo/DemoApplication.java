package com.lzh.volleywrapdemo;

import com.lzh.volleywrap.VolleyWrapInitiator;

import android.app.Application;

/**
 * liuzhenhui 16/2/18.上午10:13
 */
public class DemoApplication extends Application {
    private static final String TAG = DemoApplication.class.getSimpleName();

    @Override
    public void onCreate() {
        super.onCreate();
        VolleyWrapInitiator.init(this, BuildConfig.DEBUG);
    }
}
