package com.android.hi.hiplayer;

import android.graphics.SurfaceTexture;
import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.view.Surface;
import android.view.TextureView;

import com.android.hi.hiplayer.kitset.VideoUtil;
import com.android.hi.hiplayer.player.Player;


public class MainActivity extends AppCompatActivity {


    TextureView videoCanvas;
    VideoUtil videoUtil;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        videoCanvas = (TextureView) findViewById(R.id.video_canvas);
        videoUtil = new VideoUtil();

        try {
            videoUtil.aa(this, Environment.getExternalStorageDirectory() + "/DCIM/Camera/abc.mp4");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();

        Player.get().prepare(this);
        Player.get().setSurface(videoCanvas);
        try {
            Player.get().play("https://storage.googleapis.com/android-tv/Sample%20videos/Demo%20Slam/Google%20Demo%20Slam_%20Hangin'%20with%20the%20Google%20Search%20Bar.mp4");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        try {
            Player.get().stop();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onDestroy() {
        try {
            Player.get().release();
        } catch (Exception e) {
            e.printStackTrace();
        }
        super.onDestroy();
    }
}
