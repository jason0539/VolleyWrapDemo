package com.lzh.volleywrap.baseframe.volley;

import java.io.File;
import java.util.Map;

public interface MultipartRequestInterface extends RequestInterface {

    void setFileBody(String key, File file, String mimeType);

    void setStringBody(Map<String, String> param);

}
