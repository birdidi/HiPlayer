package com.android.hi.hiplayer.widget;

import android.content.Context;
import android.graphics.SurfaceTexture;
import android.opengl.GLES11Ext;
import android.opengl.GLES20;
import android.opengl.GLSurfaceView;
import android.util.AttributeSet;
import android.view.Surface;

import com.android.hi.hiplayer.player.Player;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

public class CameraGLSurfaceView extends GLSurfaceView implements GLSurfaceView.Renderer, SurfaceTexture.OnFrameAvailableListener {

    private int mTextureID;
    private SurfaceTexture mSurface;
    private Player mPlayer;

    private float[] mSTMatrix = new float[16];
    private boolean updateSurface = true;

    public CameraGLSurfaceView(Context context) {
        this(context, null);
    }

    public CameraGLSurfaceView(Context context, AttributeSet attrs) {
        super(context, attrs);

        mPlayer = Player.get();

        //设置OpenGL ES版本为2.0
        setEGLContextClientVersion(2);
        //设置与当前GLSurfaceView绑定的Render
        setRenderer(this);
        //设置渲染模式
        setRenderMode(RENDERMODE_WHEN_DIRTY);
    }

    @Override
    public void onSurfaceCreated(GL10 gl, EGLConfig config) {
        int[] textures = new int[1];
        GLES20.glGenTextures(1, textures, 0);

        mTextureID = textures[0];
        GLES20.glBindTexture(GLES11Ext.GL_TEXTURE_EXTERNAL_OES, mTextureID);

        mSurface = new SurfaceTexture(mTextureID);
        mSurface.setOnFrameAvailableListener(this);

        Surface surface = new Surface(mSurface);
        mPlayer.setSurface(surface);
    }

    @Override
    public void onSurfaceChanged(GL10 gl, int width, int height) {
        updateSurface = true;
    }

    @Override
    public void onDrawFrame(GL10 gl) {

        if (updateSurface) {
            mSurface.updateTexImage();
            mSurface.getTransformMatrix(mSTMatrix);
            updateSurface = false;

//            GLES20.glActiveTexture(GLES20.GL_TEXTURE0);
//            GLES20.glBindTexture(GLES11Ext.GL_TEXTURE_EXTERNAL_OES, mTextureID);
//            GLES20.glDrawArrays(GLES20.GL_TRIANGLE_STRIP, 0, 4);
        }
    }

    @Override
    public void onFrameAvailable(SurfaceTexture surfaceTexture) {
        requestRender();
    }
}
