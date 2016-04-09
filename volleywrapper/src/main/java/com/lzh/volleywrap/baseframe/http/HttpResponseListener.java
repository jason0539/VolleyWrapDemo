package com.lzh.volleywrap.baseframe.http;

public interface HttpResponseListener<T> {

    void onResponse(T response);

    void onError(String msg);
}
