package com.android.hi.hiplayer.activity;

import android.animation.AnimatorSet;
import android.animation.ValueAnimator;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.animation.AccelerateInterpolator;
import android.widget.ImageView;

import com.android.birdidi.core.kitset.ScreenUtil;
import com.android.hi.hiplayer.R;

public class GuideActivity extends AppCompatActivity {

    private static final String TAG = "GuideActivity";

    private ImageView ivGallery;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_guide);
        initView();
    }

    private void initView() {
        ivGallery = findViewById(R.id.iv_guide_post);

        final int screenW = ScreenUtil.getCurrentScreenWidth(getApplicationContext());
        final int screenH = ScreenUtil.getCurrentScreenHeight(getApplicationContext());

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
        matrix.preTranslate(dx * 1.5f, dy * 0.5f);
        matrix.preScale(scaleX, scaleY);
        ivGallery.setImageMatrix(matrix);

        AnimatorSet set = new AnimatorSet();
        set.setDuration(3000);
        ValueAnimator animatorX = ValueAnimator.ofFloat(1.5f, 1f);
        animatorX.setInterpolator(new AccelerateInterpolator());
        animatorX.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                float val = (float) animation.getAnimatedValue();
                Matrix mm = new Matrix();
                mm.postTranslate(dx * val/* - val * screenW*/, 0/* - val * screenH*/);
                ivGallery.setImageMatrix(mm);
            }
        });

        ValueAnimator animatorY = ValueAnimator.ofFloat(0.5f, 1f);
        animatorY.setInterpolator(new AccelerateInterpolator());
        animatorY.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                float val = (float) animation.getAnimatedValue();
                Matrix mm = new Matrix();
                mm.postTranslate(0/* - val * screenW*/, dy * val/* - val * screenH*/);
                ivGallery.setImageMatrix(mm);
            }
        });
        set.playTogether(animatorY, animatorX);
        set.start();
    }

    private int[] getBitmapWH(int resId) {
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeResource(getResources(), resId, options);
        return new int[]{options.outWidth, options.outHeight};
    }
}
