package com.lzh.volleywrap.baseframe.utils;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.Map;

/**
 * liuzhenhui 16/2/22.下午9:19
 */
public class HttpUtils {
    private static final String TAG = HttpUtils.class.getSimpleName();

    public static String Map2Query(Map<String, String> map, boolean encode) {
        if (map.isEmpty()) {
            return "";
        }
        StringBuilder builder = new StringBuilder();
        for (Map.Entry<String, String> entry : map.entrySet()) {
            String val = entry.getValue();
            if (encode) {
                val = stringEncode(val);
            }
            buildRequestParameter(builder, entry.getKey(), val);
        }
        return builder.toString();
    }

    public static String stringEncode(String src) {
        String dst = src;
        try {
            dst = URLEncoder.encode(src, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return dst;
    }

    public static void buildRequestParameter(StringBuilder param, String key, String value) {
        if (param.length() == 0) {
            param.append("?");
        } else {
            param.append("&");
        }
        param.append(key).append("=").append(value);
    }

}
