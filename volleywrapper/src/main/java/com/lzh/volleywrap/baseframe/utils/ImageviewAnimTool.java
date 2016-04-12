package com.lzh.volleywrap.baseframe.utils;

import java.util.HashMap;
import java.util.Iterator;

import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.widget.ImageView;

/**
 * liuzhenhui 16/4/10.下午8:34
 */
public class ImageviewAnimTool {

    public static boolean changeBmp(ImageView iv, Bitmap bmp) {
        boolean change = false;
        if (iv != null) {
            Drawable d = iv.getDrawable();
            if (d != null) {
                if (d instanceof BitmapDrawable) {
                    BitmapDrawable bd = (BitmapDrawable) d;
                    Bitmap tmp = bd.getBitmap();
                    if (tmp == null && bmp != null) {
                        change = true;
                    } else if (tmp != null && bmp != null) {
                        if (!tmp.equals(bmp)) {
                            change = true;
                        }
                    }
                } else {
                    change = true;
                }
            } else {
                change = true;
            }
        }
        return change;
    }

    private static final HashMap<Integer, Object> mapShow = new HashMap<>();
    private static final Object NOT_NULL = new Object();
    private static final int MAX_SIZE = 100;

    public static boolean hasShowAnim(Bitmap bmp) {
        boolean has = false;
        if (bmp != null && !bmp.isRecycled()) {
            int id = bmp.hashCode();
            synchronized(mapShow) {
                if (mapShow.get(id) != null) {
                    has = true;
                } else {
                    mapShow.put(id, NOT_NULL);
                    if (mapShow.size() > MAX_SIZE) {
                        Iterator iter = mapShow.entrySet().iterator();
                        if (iter.hasNext()) {
                            mapShow.remove(iter.next());
                        }
                    }
                }
            }
        }
        return has;
    }
}
