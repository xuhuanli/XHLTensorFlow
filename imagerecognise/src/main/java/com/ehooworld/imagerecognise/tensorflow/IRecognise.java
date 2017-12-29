package com.ehooworld.imagerecognise.tensorflow;

import android.graphics.Bitmap;

/**
 * Created by xuhuanli on 2017/12/28.
 */
public interface IRecognise {
    /**
     * Recognise pic.
     *
     * @param bitmap the bitmap
     */
    void recognisePic(Bitmap bitmap);
}
