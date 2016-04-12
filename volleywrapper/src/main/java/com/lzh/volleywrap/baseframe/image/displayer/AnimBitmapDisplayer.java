package com.lzh.volleywrap.baseframe.image.displayer;

import com.lzh.volleywrap.baseframe.utils.ImageviewAnimTool;
import com.lzh.volleywrap.baseframe.utils.MLog;

import android.graphics.Bitmap;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.ScaleAnimation;
import android.widget.ImageView;

/**
 * liuzhenhui 16/4/10.下午8:28
 */
public class AnimBitmapDisplayer implements BitmapDisplayer {

    @Override
    public void display(Bitmap bitmap, ImageView imageView) {
        if (ImageviewAnimTool.changeBmp(imageView, bitmap)) {
            imageView.setImageBitmap(bitmap);
        }
        if (!ImageviewAnimTool.hasShowAnim(bitmap)) {
            startIvAnim(imageView);
        }
    }

    public static void startIvAnim(ImageView iv) {
        if (iv != null) {
            iv.clearAnimation();
            AnimationSet as = new AnimationSet(true);
            ScaleAnimation sc =
                    new ScaleAnimation(1.2f, 1f, 1.2f, 1f, Animation.RELATIVE_TO_SELF, .5f, Animation.RELATIVE_TO_SELF,
                            0f);
            as.addAnimation(sc);
            AlphaAnimation al = new AlphaAnimation(.5f, 1);
            as.addAnimation(al);
            as.setDuration(500);
            iv.startAnimation(as);
        }
    }
}
