package com.peixing.myapplication.rxgalleryfinal.imageloader;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;

import com.peixing.myapplication.rxgalleryfinal.ui.widget.FixImageView;


/**
 * Desction:
 * Author:pengjianbo  Dujinyang
 * Date:16/6/17 下午1:05
 */
public interface AbsImageLoader {
    void displayImage(Context context,
                      String path,
                      FixImageView imageView,
                      Drawable defaultDrawable,
                      Bitmap.Config config,
                      boolean resize,
                      boolean isGif,
                      int width,
                      int height,
                      int rotate);
}
