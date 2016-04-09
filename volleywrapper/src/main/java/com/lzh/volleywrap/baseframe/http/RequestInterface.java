package com.lzh.volleywrap.baseframe.http;

import java.util.Map;

import com.android.volley.Request;

public interface RequestInterface {

    void setTag(String tag);

    void setMethod(int method);

    void setCharset(String charset);

    void setRequestPriority(Request.Priority priority);

    void shouldCache(boolean cache);

    void setTimeout(int timeout);

    void setReties(int reties);

    void setPostParam(Map param);

    void setHeaders(Map<String, String> headers);

    Request create();
}

