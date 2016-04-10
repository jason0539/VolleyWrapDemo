package com.lzh.volleywrap;

import com.lzh.volleywrap.baseframe.utils.MLog;
import com.lzh.volleywrap.baseframe.utils.StorageUtil;
import com.lzh.volleywrap.baseframe.utils.SysOSAPI;
import com.lzh.volleywrap.middleframe.HttpClientWrapper;
import com.lzh.volleywrap.middleframe.ImageLoaderWrapper;
import com.lzh.volleywrap.middleframe.ServerAddressManager;

import android.content.Context;

public class VolleyWrapperInitiator {

    public static void init(Context context, boolean debug) {
        MLog.init(debug);
        SysOSAPI.init(context);
        StorageUtil.getInstance().init(context);
        ServerAddressManager.getInstance().init(debug);
        HttpClientWrapper.getInstance().init(context);
        ImageLoaderWrapper.getInstance().init(context);
    }
}
