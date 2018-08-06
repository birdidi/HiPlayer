package com.android.hi.hiplayer.filter;

import android.graphics.SurfaceTexture;
import android.opengl.GLSurfaceView;
import android.view.Surface;

import com.android.birdidi.core.kitset.GLUtil;

public class OutputSurface {

    private GLSurfaceView.Renderer renderer;
    private SurfaceTexture surfaceTexture;
    private Surface surface;
    private GLUtil glUtil;

    public OutputSurface() {
        renderer = new TextureRender();
        glUtil = new GLUtil();
        surfaceTexture = new SurfaceTexture(glUtil.createTextureID());
        surface = new Surface(surfaceTexture);
    }

    public Surface getSurface() {
        return surface;
    }
}
