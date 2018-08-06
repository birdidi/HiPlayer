package com.android.hi.hiplayer.activity;

import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import com.android.birdidi.core.config.BaseConfig;
import com.android.hi.hiplayer.R;
import com.android.hi.hiplayer.kitset.VideoUtil;

public class VideoEditActivity extends AppCompatActivity {

    private static final String SOURCE_PATH = Environment.getExternalStorageDirectory() + "/abc.mp4";

    VideoUtil videoUtil;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video_edit);
        videoUtil = new VideoUtil();
        init();
    }

    private void init(){
        findViewById(R.id.btn_extract_video).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                videoUtil.extractPureVideoFile(SOURCE_PATH, BaseConfig.getBaseDir() + "/abc_pure_video.mp4");
            }
        });

        findViewById(R.id.btn_extract_audio).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                videoUtil.extractPureAudioFile(SOURCE_PATH, BaseConfig.getBaseDir() + "/abc_pure_audio.mp3");
            }
        });

        findViewById(R.id.btn_combine_video).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                videoUtil.combineVideo(Environment.getExternalStorageDirectory() + "/abc.mp4",
                        Environment.getExternalStorageDirectory() + "/aa.mp4",
                        BaseConfig.getBaseDir() + "/abc_combine.mp4");
            }
        });
    }
}
