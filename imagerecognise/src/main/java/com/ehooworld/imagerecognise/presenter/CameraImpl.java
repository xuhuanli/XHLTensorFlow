package com.ehooworld.imagerecognise.presenter;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.content.FileProvider;

import com.ehooworld.imagerecognise.view.IMainActivity;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by xuhuanli on 2017/12/28.
 */
public class CameraImpl implements ICamera {

    /**
     * The constant CAMEAR_REQUEST_CODE.
     */
    public static final int CAMEAR_REQUEST_CODE = 1;

    /**
     * The M current photo path.
     */
    String mCurrentPhotoPath;

    private IMainActivity view;
    private Uri uri;

    /**
     * Instantiates a new Camera.
     *
     * @param view the view
     */
    public CameraImpl(IMainActivity view) {
        this.view = view;
    }


    @Override
    public void openCamera() {
        Context context = view.getContext();
        //这是只保存一张的方式
        /*File file = new File(context.getExternalFilesDir(Environment.DIRECTORY_PICTURES), "image.jpg");
        try {
            if (file.exists()) {
                file.delete();
            }

            file.createNewFile();
        } catch (IOException e) {
            e.printStackTrace();
        }*/
        try {
            File file = createImageFile(context);
            if (Build.VERSION.SDK_INT >= 24 && file != null) {
                uri = FileProvider.getUriForFile(context, "com.ehooworld.imagerecognise.fileprovider", file);
            } else {
                // TODO: 2017/12/28 检查一下是否可以直接通过Uri.fromFile获取Uri
                uri = Uri.fromFile(file);
            }

            Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            //you need the path to write bitmap into device
            intent.putExtra(MediaStore.EXTRA_OUTPUT, uri);
            view.startOtherActivity(intent, CAMEAR_REQUEST_CODE);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public Uri getUri() {
        return uri;
    }


    private File createImageFile(Context context) throws IOException {
        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = context.getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(
                imageFileName,  /* prefix */
                ".jpg",         /* suffix */
                storageDir      /* directory */
        );

        // Save a file: path for use with ACTION_VIEW intents
        mCurrentPhotoPath = image.getAbsolutePath();
        return image;
    }
}
