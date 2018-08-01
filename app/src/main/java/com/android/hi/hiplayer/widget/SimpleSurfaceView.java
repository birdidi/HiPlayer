package com.android.hi.hiplayer.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

public class SimpleSurfaceView extends SurfaceView implements SurfaceHolder.Callback{

    private SimpleRenderThread mThread;

    public SimpleSurfaceView(Context context) {
        this(context, null);
    }

    public SimpleSurfaceView(Context context, AttributeSet attrs) {
        super(context, attrs);

        SurfaceHolder holder = getHolder();
        holder.addCallback(this);

        mThread = new SimpleRenderThread(holder);
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        Log.d("cxydebug", "surfaceCreated() called with: holder = [" + holder + "]");
        mThread.setRun(true);
        mThread.start();
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
        Log.d("cxydebug", "surfaceChanged() called with: holder = [" + holder + "], format = [" + format + "], width = [" + width + "], height = [" + height + "]");
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        Log.d("cxydebug", "surfaceDestroyed() called with: holder = [" + holder + "]");
        mThread.setRun(false);
    }
}
