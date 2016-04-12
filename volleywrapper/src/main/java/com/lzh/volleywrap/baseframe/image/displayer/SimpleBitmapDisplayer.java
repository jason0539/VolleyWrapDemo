package com.lzh.volleywrap.baseframe.image.displayer;

import android.graphics.Bitmap;
import android.widget.ImageView;

/**
 * liuzhenhui 16/2/25.上午9:51
 */
public class SimpleBitmapDisplayer implements BitmapDisplayer {
    private static final String TAG = SimpleBitmapDisplayer.class.getSimpleName();

    @Override
    public void display(Bitmap bitmap, ImageView imageView) {
        if (bitmap != null && imageView != null) {
            imageView.setImageBitmap(bitmap);
        }
    }
}
