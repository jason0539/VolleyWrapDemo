package com.lzh.volleywrap.baseframe.image;

import com.lzh.volleywrap.baseframe.image.displayer.BitmapDisplayer;
import com.lzh.volleywrap.baseframe.image.displayer.SimpleBitmapDisplayer;

/**
 * liuzhenhui 16/2/24.下午8:49
 */
public class ImageLoaderOption {
    private static final String TAG = ImageLoaderOption.class.getSimpleName();

    private BitmapDisplayer displayer;
    private boolean resetImageViewBeforeLoad;

    private ImageLoaderOption(Builder builder) {
        displayer = builder.displayer == null ? new SimpleBitmapDisplayer() : builder.displayer;
        resetImageViewBeforeLoad = builder.resetImageViewBeforeLoad;
    }

    public BitmapDisplayer getDisplayer() {
        return displayer;
    }

    public boolean isResetImageViewBeforeLoad() {
        return resetImageViewBeforeLoad;
    }

    public static class Builder {
        private BitmapDisplayer displayer;
        private boolean resetImageViewBeforeLoad = false;

        public Builder setBitmapDisplayer(BitmapDisplayer bitmapDisplayer) {
            displayer = bitmapDisplayer;
            return this;
        }

        public Builder resetImageViewBeforLoad() {
            resetImageViewBeforeLoad = true;
            return this;
        }
        public ImageLoaderOption build() {
            return new ImageLoaderOption(this);
        }
    }
}
