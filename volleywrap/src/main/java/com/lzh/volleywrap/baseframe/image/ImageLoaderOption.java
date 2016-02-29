package com.lzh.volleywrap.baseframe.image;

/**
 * liuzhenhui 16/2/24.下午8:49
 */
public class ImageLoaderOption {
    private static final String TAG = ImageLoaderOption.class.getSimpleName();

    public BitmapDisplayer displayer;

    private ImageLoaderOption(Builder builder) {
        displayer = builder.displayer;
    }

    public static class Builder {
        private BitmapDisplayer displayer;

        public Builder setBitmapDisplayer(BitmapDisplayer bitmapDisplayer) {
            displayer = bitmapDisplayer;
            return this;
        }

        public ImageLoaderOption build() {
            return new ImageLoaderOption(this);
        }
    }
}
