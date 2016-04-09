package com.lzh.volleywrap.middleframe;

/**
 * 业务逻辑相关URL
 */
public class ServerAddressManager {

    // 图片上传地址
    private final String URL_GET_WEATHER = "http://apis.baidu.com/heweather/weather/free/";
    private final String URL_GET_WEATHER_TEST = "http://apis.baidu.com/heweather/weather/free/";

    private boolean mDebug = false;
    private ServerAddressManager() {
    }

    private static final class LazyHolder {
        public static final ServerAddressManager INSTANCE = new ServerAddressManager();
    }

    public static final ServerAddressManager getInstance() {
        return LazyHolder.INSTANCE;
    }

    public void init(boolean debug) {
        mDebug = debug;
    }

    public String getWeatherUrl() {
        if (mDebug) {
            return URL_GET_WEATHER_TEST;
        } else {
            return URL_GET_WEATHER;
        }
    }
}
