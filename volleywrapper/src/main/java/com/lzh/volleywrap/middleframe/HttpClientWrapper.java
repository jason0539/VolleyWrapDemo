package com.lzh.volleywrap.middleframe;

import java.util.HashMap;
import java.util.Map;

import org.json.JSONObject;

import com.android.volley.Request;
import com.lzh.volleywrap.baseframe.utils.HttpUtils;
import com.lzh.volleywrap.baseframe.utils.MLog;
import com.lzh.volleywrap.baseframe.utils.MToast;
import com.lzh.volleywrap.baseframe.utils.NetworkUtil;
import com.lzh.volleywrap.baseframe.utils.SysOSAPI;
import com.lzh.volleywrap.baseframe.http.HttpResponseListener;
import com.lzh.volleywrap.baseframe.http.RequestInterface;
import com.lzh.volleywrap.baseframe.VolleyClient;

import android.content.Context;

/**
 * 业务逻辑的网络请求
 */
public class HttpClientWrapper {
    private static final String TAG = HttpClientWrapper.class.getSimpleName();

    private Context mContext;
    private VolleyClient mVolley;
    private Map<String, String> mBaseParam;

    public static HttpClientWrapper getInstance() {
        return LazyHolder.INSTANCE;
    }

    private static final class LazyHolder {
        private static final HttpClientWrapper INSTANCE = new HttpClientWrapper();
    }

    public void init(Context context) {
        mContext = context;
        mVolley = VolleyClient.getInstance(context);
    }

    private HttpClientWrapper() {
        mBaseParam = new HashMap<>();
        mBaseParam.put("ver", SysOSAPI.getAppVersionName());
        mBaseParam.put("os", SysOSAPI.getOSVersion());
        mBaseParam.put("pn", SysOSAPI.getPackageName());
    }

    private Map<String, String> getBaseParam() {
        HashMap<String, String> temp = new HashMap<>();
        temp.putAll(mBaseParam);
        temp.put("nt", NetworkUtil.getNetworkStatus(mContext));
        return temp;
    }

    public void getWeather(String apiKey, HttpResponseListener listener) {
        if (!checkNetworkAvailable()) {
            return;
        }
        Map<String, String> tempParams = new HashMap<>();
        tempParams.put("cityip", NetworkUtil.getIpAddress());
        String param = HttpUtils.Map2Query(tempParams, false);

        String url = ServerAddressManager.getInstance().getWeatherUrl() + param;
        MLog.d(TAG, " getWeather url = " + url);
        RequestInterface requestInterface = mVolley.getRequestBuilder().createJsonObjectRequest(url, listener);

        Map<String, String> header = new HashMap<>();
        header.put("apikey", apiKey);
        requestInterface.setHeaders(header);

        requestInterface.setMethod(Request.Method.GET);
        mVolley.sendRequest(requestInterface);
    }

    public void postInfo(String apiKey, String address, final HttpResponseListener<JSONObject> listener) {
        if (!checkNetworkAvailable()) {
            return;
        }
        Map<String, String> temp = getBaseParam();
        temp.put("address", address);

        Map<String, String> header = new HashMap<>();
        header.put("apikey", apiKey);

        String url = ServerAddressManager.getInstance().getWeatherUrl();
        RequestInterface requestInterface = mVolley.getRequestBuilder().createJsonObjectRequest(url, listener);
        requestInterface.setMethod(Request.Method.POST);
        requestInterface.setHeaders(header);
        requestInterface.setPostParam(temp);
        mVolley.sendRequest(requestInterface);
    }


    private boolean checkNetworkAvailable() {
        boolean result = NetworkUtil.isNetworkAvailable(mContext);
        if (!result) {
            if (MToast.canShowNetError()) {
                MToast.show(mContext, "当前网络不可用，请检查您的网络设置");
            }
        }
        return result;
    }
}
