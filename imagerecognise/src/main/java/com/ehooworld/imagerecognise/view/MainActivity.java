package com.ehooworld.imagerecognise.view;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.HandlerThread;
import android.provider.MediaStore;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.ehooworld.androidutils.ToastUtil;
import com.ehooworld.imagerecognise.R;
import com.ehooworld.imagerecognise.presenter.CameraImpl;
import com.ehooworld.imagerecognise.presenter.ICamera;
import com.ehooworld.imagerecognise.tensorflow.Classifier;
import com.ehooworld.imagerecognise.tensorflow.IRecognise;
import com.ehooworld.imagerecognise.tensorflow.RecPicture;

import java.io.File;
import java.io.IOException;
import java.util.List;

import top.zibin.luban.CompressionPredicate;
import top.zibin.luban.Luban;
import top.zibin.luban.OnCompressListener;

import static com.ehooworld.imagerecognise.presenter.CameraImpl.CAMEAR_REQUEST_CODE;

/**
 * The type Main activity.
 */
public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener, IMainActivity {
    private static final String TAG = "MainActivity";
    private ICamera cameraImpl;
    private IRecognise mRecognise;
    private Bitmap bitmap;
    private HandlerThread handlerThread;
    private Handler handler;
    private ImageView picView;
    private TextView tv_result;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initPresenter();
        initView();


    }

    private void initView() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        picView = findViewById(R.id.picView);
        tv_result = findViewById(R.id.tv_result);
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //recognize bitmap
                if (bitmap != null) {
//                    Bitmap newBM = scaleBitmap(MainActivity.this.bitmap);
                    mRecognise.recognisePic(bitmap);
                } else {
                    Snackbar.make(view, R.string.noImage, Snackbar.LENGTH_SHORT)
                            .setAction(R.string.takePhoto, new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    cameraImpl.openCamera();
                                }
                            }).show();
                }
            }
        });
    }

    @Override
    protected synchronized void onResume() {
        super.onResume();
        handlerThread = new HandlerThread("inference");
        handlerThread.start();
        handler = new Handler(handlerThread.getLooper());
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        if (bitmap != null) {
            outState.putParcelable("bitmap", bitmap);
        }
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        bitmap = savedInstanceState.getParcelable("bitmap");
        if (bitmap != null) {
            picView.setImageBitmap(bitmap);
        }
    }

    private void initPresenter() {
        cameraImpl = new CameraImpl(this);
        mRecognise = new RecPicture(this);
    }

    //矩阵变换
    private Bitmap scaleBitmap(Bitmap bm) {

        // 获得图片的宽高
        int width = bm.getWidth();
        int height = bm.getHeight();
        Log.d(TAG, "原始bitmap尺寸:width= " + width + ";height= " + height);
        // 设置想要的大小
        int newWidth = 224;
        int newHeight = 224;
        // 计算缩放比例
        float scaleWidth = ((float) newWidth) / width;
        float scaleHeight = ((float) newHeight) / height;
        // 取得想要缩放的matrix参数
        Matrix matrix = new Matrix();
        matrix.postScale(scaleWidth, scaleHeight);
        // 得到新的图片
        return Bitmap.createBitmap(bm, 0, 0, width, height, matrix,
                true);
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_camera) {
            // Handle the camera action
            cameraImpl.openCamera();
        } else if (id == R.id.nav_gallery) {
            ToastUtil.showShort(this, R.string.nav_gallery);
        } else if (id == R.id.nav_slideshow) {
            ToastUtil.showShort(this, R.string.nav_slideshow);
        } else if (id == R.id.nav_manage) {
            ToastUtil.showShort(this, R.string.nav_manage);
        } else if (id == R.id.nav_share) {
            ToastUtil.showShort(this, R.string.nav_share);
        } else if (id == R.id.nav_send) {
            ToastUtil.showShort(this, R.string.nav_send);
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }


    /*private void setPicture(Bitmap bitmap) {

        picView.getDrawingCache();
        //set完整居中显示
        picView.setScaleType(ImageView.ScaleType.CENTER_INSIDE);
        RequestOptions placeholder = new RequestOptions()
                .placeholder(R.drawable.ic_sentiment_very_dissatisfied_black_24dp);
        Glide
                .with(this)
                .load(bitmap)
                .apply(placeholder)
                .into(picView);
    }*/

    @Override
    public Context getContext() {
        return this;
    }

    @Override
    public void startOtherActivity(Intent intent, int code) {
        /*
        Notice that the startActivityForResult() method is protected by a condition that calls resolveActivity(), which returns the first activity component that can handle the intent. Performing this check is important because if you call startActivityForResult() using an intent that no app can handle, your app will crash. So as long as the result is not null, it's safe to use the intent
         */
        if (intent.resolveActivity(getPackageManager()) != null) {
            startActivityForResult(intent, code);
        }
    }

    @Override
    public synchronized void runInBackground(Runnable r) {
        if (handler != null) {
            handler.post(r);
        }
    }

    @Override
    public void setResults(final List<Classifier.Recognition> results, final long lastProcessingTimeMs) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {

                String stringBuilder = results.toString() +
                        " 识别所用时长 = " +
                        lastProcessingTimeMs +
                        "ms";
                tv_result.setText(stringBuilder);
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case CAMEAR_REQUEST_CODE:
                if (resultCode == RESULT_OK) {
                    /*通过bundle拿到的bitmap是一个缩略图 可以做头像这些
                    Bundle extras = data.getExtras();
                    Bitmap imageBitmap = (Bitmap) extras.get("data");*/
                    final Uri uri = cameraImpl.getUri();
                    try {
                        bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), uri);
                        Glide
                                .with(getApplicationContext())
                                .load(bitmap)
                                .into(picView);
                        bitmap = scaleBitmap(bitmap);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    // TODO: 2018/5/25 LuBan 压缩图片质量
                }
                break;
        }
    }

    /**
     * 测试Glide 缩放图像的尺寸
     */
    private void testSize() {
        int width = bitmap.getWidth();
        int height = bitmap.getHeight();
        Log.d(TAG, "2原始bitmap尺寸:width= " + width + ";height= " + height);
    }
}
