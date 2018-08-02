package com.android.hi.hiplayer.activity;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.widget.ImageView;
import android.widget.Toast;

import com.android.birdidi.core.kitset.ScreenUtil;
import com.android.hi.hiplayer.R;

public class GuideActivity extends AppCompatActivity implements View.OnClickListener {

    private static final String TAG = "GuideActivity";

    private ImageView ivGallery;
    private View btnGuideIgnore;
    private View btnGuideGo;
    private View btnGuidePlay;
    private int screenW, screenH;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_guide);
        screenW = ScreenUtil.getCurrentScreenWidth(getApplicationContext());
        screenH = ScreenUtil.getCurrentScreenHeight(getApplicationContext());
        Log.e(TAG, "screen: " + screenW + ", " + screenH);
        initView();
    }

    private void initView() {
        ivGallery = findViewById(R.id.iv_guide_post);
        btnGuideGo = findViewById(R.id.btn_guide_go);
        btnGuideIgnore = findViewById(R.id.btn_guide_ignore);
        btnGuidePlay = findViewById(R.id.btn_guide_play);
        btnGuideIgnore.setOnClickListener(this);
        btnGuideGo.setOnClickListener(this);
        btnGuidePlay.setOnClickListener(this);
    }

    @Override
    protected void onStart() {
        super.onStart();
        performPost1();
    }

    private void performPost1() {
        int[] post1WH = getBitmapWH(R.drawable.ic_guide_1);
        ivGallery.setImageResource(R.drawable.ic_guide_1);
        final Matrix matrix = new Matrix();

        float scaleX = (screenW * 0.7f) / post1WH[0];
        float scaleY = (screenH * 0.7f) / post1WH[1];
        scaleX = scaleY = Math.max(scaleX, scaleY);
        Log.d(TAG, "scale: " + scaleX + ", " + scaleY);
        Log.d(TAG, "screen : " + screenW + ", " + screenH);
        Log.d(TAG, "image: " + post1WH[0] + ", " + post1WH[1]);
        final float dx = -(post1WH[0] * scaleX) * (19 / 32.0f);
        final float dy = -(post1WH[1] * scaleY) * (14 / 32.0f);
        matrix.setScale(scaleX, scaleY);
        //matrix.postTranslate(dx * 1.5f, dy * 0f);
        ivGallery.setImageMatrix(matrix);

        ValueAnimator animator = ValueAnimator.ofFloat(0.5f, 1f);
        animator.setDuration(2000);
        animator.setInterpolator(new AccelerateDecelerateInterpolator());
        animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                float val = (float) animation.getAnimatedValue();
                Matrix mm = new Matrix(matrix);
                mm.postTranslate(dx * (2.0f - val)/* - val * screenW*/, dy * (val - 0.5f) * 2/* - val * screenH*/);
                ivGallery.setImageMatrix(mm);
            }
        });
        animator.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                performPost2();
            }
        });
        animator.start();
    }

    private void performPost2() {
        final int[] drawableWH = getBitmapWH(R.drawable.ic_guide_3);
        ivGallery.setImageResource(R.drawable.ic_guide_3);
        float scaleX = ((screenW * 0.7f) / drawableWH[0]);
        float scaleY = ((screenH * 0.7f) / drawableWH[1]);
        scaleX = scaleY = Math.max(scaleX, scaleY);
        final Matrix matrix = new Matrix();
        matrix.setScale(scaleX, scaleY);
        matrix.postTranslate((drawableWH[0] * scaleX - screenW), (drawableWH[1] * scaleY - screenH));
        ivGallery.setImageMatrix(matrix);


        ValueAnimator animator = ValueAnimator.ofFloat(1.0f, 3.0f);
        animator.setDuration(2000);
        animator.setInterpolator(new AccelerateDecelerateInterpolator());
        animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                float val = (float) animation.getAnimatedValue();
                Matrix mm = new Matrix(matrix);
                mm.postScale(val, val, drawableWH[0] * 3.0f / 2, drawableWH[1] * 3.0f / 2);
                mm.postTranslate((drawableWH[0] * val - screenW), (drawableWH[1] * val - screenH));
                ivGallery.setImageMatrix(mm);
            }
        });
        animator.addListener(new AnimatorListenerAdapter() {

            @Override
            public void onAnimationEnd(Animator animation) {
                performPost3();
            }
        });
        animator.start();
    }

    private void performPost3() {
        final int[] drawableWH = getBitmapWH(R.drawable.ic_guide_3);
        Log.e(TAG, "drawable: " + drawableWH[0] + ", " + drawableWH[1]);
        ivGallery.setImageResource(R.drawable.ic_guide_3);
        float scaleX = ((screenW * 0.7f) / drawableWH[0]);
        float scaleY = ((screenH * 0.7f) / drawableWH[1]);
        scaleX = scaleY = Math.max(scaleX, scaleY);
        Log.e(TAG, "scale: " + scaleX);
        final float dx = -(drawableWH[0] * scaleX * (2 / 32.0f));
        final float dy = -(drawableWH[1] * scaleY * (7 / 32.0f));

        final Matrix matrix = new Matrix();
        matrix.setScale(scaleX, scaleY);
        ivGallery.setImageMatrix(matrix);

        ValueAnimator animator = ValueAnimator.ofFloat(4.0f, 1f);
        animator.setDuration(2000);
        animator.setInterpolator(new AccelerateDecelerateInterpolator());
        animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                float val = (float) animation.getAnimatedValue();
                Matrix mm = new Matrix(matrix);
                mm.postTranslate(dx * ((val / 2) + 0.5f), dy * val);
                ivGallery.setImageMatrix(mm);
            }
        });
        animator.start();
    }

    private int[] getBitmapWH(int resId) {
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeResource(getResources(), resId, options);
        return new int[]{options.outWidth, options.outHeight};
    }

    @Override
    public void onClick(View v) {
        if (v == btnGuideGo || v == btnGuidePlay) {
            Toast.makeText(this, "你还可以在桌面设置-桌面布局中开启/关闭视频桌面噢", Toast.LENGTH_SHORT).show();
        } else if (v == btnGuideIgnore) {
            Toast.makeText(this, "关闭视频桌面后将不再进行提示了噢", Toast.LENGTH_SHORT).show();
        }
    }
}
