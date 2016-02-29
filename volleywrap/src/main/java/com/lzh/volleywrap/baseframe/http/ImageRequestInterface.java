package com.lzh.volleywrap.baseframe.http;

import android.graphics.Bitmap;
import android.widget.ImageView;

public interface ImageRequestInterface extends RequestInterface {
    void setBitmapDecodeConfig(Bitmap.Config config);

    void setBitmapScaleType(ImageView.ScaleType scaleType);

    void setBitmapMaxWidth(int width);

    void setBitmapMaxHeight(int height);
}
