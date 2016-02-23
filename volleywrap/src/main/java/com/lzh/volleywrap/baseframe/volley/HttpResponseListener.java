package com.lzh.volleywrap.baseframe.volley;

public interface HttpResponseListener<T> {

    void onResponse(T response);

    void onError(String msg);
}
