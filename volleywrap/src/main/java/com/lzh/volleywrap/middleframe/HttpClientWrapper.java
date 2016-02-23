package com.lzh.volleywrap.middleframe;

import java.util.HashMap;
import java.util.Map;

import org.json.JSONObject;

import com.android.volley.Request;
import com.lzh.volleywrap.baseframe.utils.HttpUtils;
import com.lzh.volleywrap.baseframe.utils.MToast;
import com.lzh.volleywrap.baseframe.utils.NetworkUtil;
import com.lzh.volleywrap.baseframe.utils.SysOSAPI;
import com.lzh.volleywrap.baseframe.volley.HttpResponseListener;
import com.lzh.volleywrap.baseframe.volley.RequestInterface;
import com.lzh.volleywrap.baseframe.volley.VolleyHttpClient;

import android.content.Context;

/**
 * 业务逻辑的网络请求
 */
public class HttpClientWrapper {
    private static final String TAG = HttpClientWrapper.class.getSimpleName();

    private Context mContext;
    private VolleyHttpClient mVolley;
    private Map<String, String> mBaseParam;

    private HttpClientWrapper() {
        mBaseParam = new HashMap<>();
        mBaseParam.put("ver", SysOSAPI.getAppVersionName());
        mBaseParam.put("os", SysOSAPI.getOSVersion());
        mBaseParam.put("pn", SysOSAPI.getPackageName());
    }

    public void init(Context context) {
        mContext = context;
        mVolley = new VolleyHttpClient(context);
    }

    private Map<String, String> getBaseParam() {
        HashMap<String, String> temp = new HashMap<>();
        temp.putAll(mBaseParam);
        temp.put("nt", NetworkUtil.getNetworkStatus(mContext));
        return temp;
    }

    public void getWeather(String apiKey, String cityCode, HttpResponseListener listener) {
        if (!checkNetworkAvailable()) {
            return;
        }
        Map<String, String> tempParams = new HashMap<>();
        tempParams.put("city", cityCode);
        String param = HttpUtils.Map2Query(tempParams, false);

        String url = ServerAddressManager.getInstance().getWeatherUrl() + param;
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

    private static volatile HttpClientWrapper mInstance;

    public static HttpClientWrapper getInstance() {
        if (mInstance == null) {
            synchronized(HttpClientWrapper.class) {
                if (mInstance == null) {
                    mInstance = new HttpClientWrapper();
                }
            }
        }
        return mInstance;
    }
}
