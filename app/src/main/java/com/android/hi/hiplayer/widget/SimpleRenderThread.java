package com.android.hi.hiplayer.widget;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.view.SurfaceHolder;

public class SimpleRenderThread extends Thread {

    private SurfaceHolder mHolder;
    private boolean isRun = false;

    public SimpleRenderThread(SurfaceHolder holder) {
        this.mHolder = holder;
        isRun = true;
    }

    @Override
    public void run() {

        long counter = 0;
        Canvas canvas = null;
        int maxTextWidth = 50;

        while (isRun) {
            try {
                canvas = mHolder.lockCanvas();

                canvas.drawColor(Color.WHITE);

                Paint p = new Paint();
                p.setColor(Color.BLACK);
                p.setTextSize(30);

                Rect rect = new Rect(100, 50, 380, 330);
                canvas.drawRect(rect, p);

                p.setColor(Color.GREEN);
                int len = 20;
                int left = (int) (100 + ((counter) % (280 - len)));
                int top = (int) (50 + ((counter) % (280 - len)));
                int right = left + len;
                int bottom = top + len;
                canvas.drawRect(left, top, right, bottom, p);

                p.setColor(Color.BLACK);

                String preText = "Interval = " + (counter++ / 100.0);
                float preTextWidth = p.measureText(preText);
                canvas.drawText(preText, 100, 410, p);
                maxTextWidth = Math.max((int) preTextWidth, maxTextWidth);
                canvas.drawText(" seconds.", maxTextWidth + 20 + 100, 410, p);
                Thread.sleep(1);
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                if (canvas != null) {
                    mHolder.unlockCanvasAndPost(canvas);
                }
            }
        }

        super.run();
    }

    public void setRun(boolean run) {
        this.isRun = run;
    }

    public boolean isRun() {
        return this.isRun;
    }
}
