package com.ehooworld.imagerecognise.view;

import android.content.Context;
import android.content.Intent;

import com.ehooworld.imagerecognise.tensorflow.Classifier;

import java.util.List;

/**
 * Created by xuhuanli on 2017/12/28.
 */
public interface IMainActivity {
    /**
     * Gets context.
     *
     * @return the context
     */
    Context getContext();

    /**
     * Start other activity.
     *
     * @param intent the intent
     * @param code   the code
     */
    void startOtherActivity(Intent intent,int code);

    /**
     * Run in background.
     *
     * @param r the r
     */
    void runInBackground(Runnable r);

    /**
     * Sets results.
     *
     * @param results              the results
     * @param lastProcessingTimeMs the last processing time ms
     */
    void setResults(List<Classifier.Recognition> results, long lastProcessingTimeMs);
}
