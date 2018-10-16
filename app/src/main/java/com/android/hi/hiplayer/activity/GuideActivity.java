package com.android.hi.hiplayer.activity;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ValueAnimator;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
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

    private static final int WHAT_ANIMATOR_LOOP = 1;

    private ImageView ivGallery;
    private View btnGuideIgnore;
    private View btnGuideGo;
    private View btnGuidePlay;
    private int screenW, screenH;

    private Matrix matrixA, matrixB, matrixC;
    private float[] deltaA = new float[2], deltaB = new float[2], deltaC = new float[2];
    private float[] scaleA = new float[2], scaleB = new float[2], scaleC = new float[2];
    private AnimatorSet animatorSet;

    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (msg.what == WHAT_ANIMATOR_LOOP) {
				//modified flag A
               if (animatorSet != null) {
                    animatorSet.start();
                }
            }
        }
    };

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_guide);
        //pending modify from v2
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

        initMatrix();
    }

    /**
     * 初始化矩阵动画
     */
    private void initMatrix() {
        ivGallery.setLayerType(View.LAYER_TYPE_HARDWARE, null);
        int[] drawableWH;
        float dx, dy;

        //图一
        drawableWH = getBitmapWH(R.drawable.ic_guide_1);
        ivGallery.setImageResource(R.drawable.ic_guide_1);
        matrixA = new Matrix();

        float scaleX = (screenW * 1.4f) / drawableWH[0];
        float scaleY = (screenH * 1.4f) / drawableWH[1];
        scaleX = scaleY = Math.max(scaleX, scaleY);
        Log.e(TAG, "scaleA : " + scaleX + ", imageA: " + drawableWH[0] + ", " + drawableWH[1]);
        dx = -(drawableWH[0] * scaleX) * (10 / 32.0f);
        dy = -(drawableWH[1] * scaleY) * (7 / 32.0f);
        scaleA[0] = scaleX;
        scaleA[1] = scaleY;
        deltaA[0] = dx;
        deltaA[1] = dy;
        matrixA.setScale(scaleX, scaleY);

        Matrix matrixDef= new Matrix(matrixA);
        matrixDef.postTranslate(dx * (2.0f - 0.6f), dy * (0.6f - 0.5f) * 2);
        ivGallery.setImageMatrix(matrixDef);

        //图二
        drawableWH = getBitmapWH(R.drawable.ic_guide_2);
        scaleX = ((screenW * 1.0f) / drawableWH[0]);
        scaleY = ((screenH * 1.0f) / drawableWH[1]);
        scaleX = scaleY = Math.max(scaleX, scaleY);
        Log.e(TAG, "scaleB : " + scaleX + ", imageB: " + drawableWH[0] + ", " + drawableWH[1]);
        matrixB = new Matrix();
        matrixB.setScale(scaleX, scaleY);
        matrixB.postTranslate((screenW - drawableWH[0] * scaleX) / 2, (screenH - drawableWH[1] * scaleY) / 2);

        scaleB[0] = scaleX;
        scaleB[1] = scaleY;
        deltaB[0] = drawableWH[0];
        deltaB[1] = drawableWH[1];

//        ivGallery.setImageMatrix(matrixB);

        //图三
        drawableWH = getBitmapWH(R.drawable.ic_guide_3);
        scaleX = ((screenW * 1.2f) / drawableWH[0]);
        scaleY = ((screenH * 1.2f) / drawableWH[1]);
        scaleX = scaleY = Math.max(scaleX, scaleY);
        Log.e(TAG, "scaleC: " + scaleX + ", imageC: " + drawableWH[0] + ", " + drawableWH[1]);
        dx = -(drawableWH[0] * scaleX * (1 / 32.0f));
        dy = -(drawableWH[1] * scaleY * (3 / 32.0f));

        matrixC = new Matrix();
        matrixC.setScale(scaleX, scaleY);
//        matrixC.postTranslate(dx, dy);

        scaleC[0] = scaleX;
        scaleC[1] = scaleY;
        deltaC[0] = dx;
        deltaC[1] = dy;

        animatorSet = new AnimatorSet();

        ValueAnimator animatorA = ValueAnimator.ofFloat(0.6f, 1f);
        animatorA.setDuration(2000);
        animatorA.setInterpolator(new AccelerateDecelerateInterpolator());
        animatorA.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                float val = (float) animation.getAnimatedValue();
                Matrix mm = new Matrix(matrixA);
                mm.postTranslate(deltaA[0] * (2.0f - val), deltaA[1] * (val - 0.5f) * 2);
                ivGallery.setImageMatrix(mm);
            }
        });
        animatorA.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationStart(Animator animation) {
                super.onAnimationStart(animation);
                ivGallery.setImageResource(R.drawable.ic_guide_1);
            }
        });

        ValueAnimator animatorB = ValueAnimator.ofFloat(scaleB[0], scaleB[0] + 0.5f);
        animatorB.setDuration(2000);
        animatorB.setInterpolator(new AccelerateDecelerateInterpolator());
        animatorB.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                float val = (float) animation.getAnimatedValue();
                Matrix mm = new Matrix();
                mm.setScale(val, val);
                mm.postTranslate((screenW - deltaB[0] * val) / 2 * (36 / 32f), (screenH - deltaB[1] * val) / 2 * (34 / 32f));
                ivGallery.setImageMatrix(mm);
            }
        });
        animatorB.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationStart(Animator animation) {
                super.onAnimationStart(animation);
                ivGallery.setImageResource(R.drawable.ic_guide_2);
            }
        });

        ValueAnimator animatorC = ValueAnimator.ofFloat(4.0f, 1f);
        animatorC.setDuration(2000);
        animatorC.setInterpolator(new AccelerateDecelerateInterpolator());
        animatorC.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                float val = (float) animation.getAnimatedValue();
                Matrix mm = new Matrix(matrixC);
                mm.postTranslate(deltaC[0] * ((val / 2) + 0.5f), deltaC[1] * val);
                ivGallery.setImageMatrix(mm);
            }
        });
        animatorC.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationStart(Animator animation) {
                super.onAnimationStart(animation);
                ivGallery.setImageResource(R.drawable.ic_guide_3);
            }
        });

        animatorSet.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
                handler.obtainMessage(WHAT_ANIMATOR_LOOP).sendToTarget();
            }
        });
        animatorSet.playSequentially(animatorA, animatorB, animatorC);
    }

    private int[] getBitmapWH(int resId) {
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeResource(getResources(), resId, options);
        float scale = (ScreenUtil.getDensity(getApplicationContext())) / 1.5f;//HDPI
        Log.e(TAG, "image scale : " + scale);
        return new int[]{(int) (options.outWidth * scale), (int) (options.outHeight * scale)};
    }

    @Override
    protected void onStart() {
        super.onStart();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                if (animatorSet != null) {
                    animatorSet.start();
                }
            }
        }, 800);
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (animatorSet != null) {
            animatorSet.cancel();
        }
    }

    @Override
    protected void onDestroy() {
        if (animatorSet != null) {
            animatorSet.cancel();
        }
        animatorSet = null;
        super.onDestroy();
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
