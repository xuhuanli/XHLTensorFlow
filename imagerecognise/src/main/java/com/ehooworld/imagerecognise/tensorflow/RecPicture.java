package com.ehooworld.imagerecognise.tensorflow;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.SystemClock;

import com.ehooworld.imagerecognise.view.IMainActivity;

import java.util.List;

/**
 * Created by xuhuanli on 2017/12/28.
 */

public class RecPicture implements IRecognise {

    private IMainActivity view;
    private static final int INPUT_SIZE = 224;
    private static final int IMAGE_MEAN = 117;
    private static final float IMAGE_STD = 1;
    private static final String INPUT_NAME = "input";
    private static final String OUTPUT_NAME = "final_result";

    private static final String MODEL_FILE = "file:///android_asset/1221v3.pb";
    private static final String LABEL_FILE =
            "file:///android_asset/1221_labels.txt";
    private final Context mContext;
    private Classifier mClassifier;

    public RecPicture(IMainActivity view) {
        this.view = view;
        mContext = view.getContext();
    }


    @Override
    public void recognisePic(final Bitmap bitmap) {
        mClassifier = TensorFlowImageClassifier.create(mContext.getAssets(), MODEL_FILE, LABEL_FILE, INPUT_SIZE, IMAGE_MEAN, IMAGE_STD, INPUT_NAME, OUTPUT_NAME);
        view.runInBackground(new Runnable() {
            @Override
            public void run() {
                long startTime = SystemClock.uptimeMillis();
                List<Classifier.Recognition> results = mClassifier.recognizeImage(bitmap);
                long  lastProcessingTimeMs = SystemClock.uptimeMillis() - startTime;
                view.setResults(results,lastProcessingTimeMs);
            }
        });
    }
}
