package com.android.hi.hiplayer;

import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.view.TextureView;
import android.view.View;

import com.android.hi.hiplayer.activity.GuideActivity;
import com.android.hi.hiplayer.kitset.VideoUtil;
import com.android.hi.hiplayer.player.Player;


public class MainActivity extends AppCompatActivity {

    private static final String SOURCE_PATH = Environment.getExternalStorageDirectory() + "/abc.mp4";

    TextureView videoCanvas;
    VideoUtil videoUtil;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        videoCanvas = (TextureView) findViewById(R.id.video_canvas);
        videoUtil = new VideoUtil();

        initView();

    }

    private void initView() {
        findViewById(R.id.btn_process_video).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                try {
//                    videoUtil.aa(MainActivity.this.getApplicationContext(), SOURCE_PATH,
//                            BaseConfig.getBaseDir() + "/abc_final.mp4");
//                } catch (Exception e) {
//                    Log.e("cxydebug", "exception : " + e.getMessage());
//                    e.printStackTrace();
//                }

                Intent jumpToMediaEdit = new Intent();
                jumpToMediaEdit.setClass(MainActivity.this, GuideActivity.class);
                startActivity(jumpToMediaEdit);
            }
        });

//        findViewById(R.id.btn_extract_video).setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                videoUtil.extractPureVideoFile(SOURCE_PATH, BaseConfig.getBaseDir() + "/abc_pure_video.mp4");
//            }
//        });
//
//        findViewById(R.id.btn_extract_audio).setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                videoUtil.extractPureAudioFile(SOURCE_PATH, BaseConfig.getBaseDir() + "/abc_pure_audio.mp3");
//            }
//        });
//
//        findViewById(R.id.btn_combine_video).setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                videoUtil.combineVideo(Environment.getExternalStorageDirectory() + "/abc.mp4",
//                        Environment.getExternalStorageDirectory() + "/aa.mp4",
//                        BaseConfig.getBaseDir() + "/abc_combine.mp4");
//            }
//        });

        findViewById(R.id.btn_guide).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent jumpGuide = new Intent();
                jumpGuide.setClass(MainActivity.this, GuideActivity.class);
                MainActivity.this.startActivity(jumpGuide);
            }
        });
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
