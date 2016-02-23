package com.lzh.volleywrapdemo.model;

import org.json.JSONObject;

import com.lzh.volleywrap.baseframe.volley.HttpResponseListener;
import com.lzh.volleywrap.middleframe.HttpClientWrapper;
import com.lzh.volleywrapdemo.utils.DemoConstant;

/**
 * liuzhenhui 16/2/22.下午8:59
 */
public class WeatherModel {
    private static final String TAG = WeatherModel.class.getSimpleName();

    public void getWeather(String city,final WeatherCallback callback) {
        HttpResponseListener<JSONObject> listener = new HttpResponseListener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                callback.success(response.toString());
            }

            @Override
            public void onError(String msg) {
                callback.fail(msg);
            }
        };
        HttpClientWrapper.getInstance().getWeather(DemoConstant.API_KEY, city, listener);
    }

    public interface WeatherCallback {
        void fail(String msg);

        void success(String msg);
    }
}
