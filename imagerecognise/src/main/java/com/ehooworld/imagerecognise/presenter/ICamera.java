package com.ehooworld.imagerecognise.presenter;

import android.net.Uri;

/**
 * The interface Camera.
 */
public interface ICamera {
    /**
     * Open camera.
     */
    void openCamera();

    /**
     * Gets uri.
     *
     * @return the uri
     */
    Uri getUri();
}
