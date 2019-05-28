package com.android.hi.hiplayer;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import com.android.hi.hiplayer.activity.AudioActivity;
import com.android.hi.hiplayer.activity.VideoActivity;


public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private Button btnAudio, btnVideo;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        btnAudio = findViewById(R.id.btn_audio);
        btnVideo = findViewById(R.id.btn_video);

        btnAudio.setOnClickListener(this);
        btnVideo.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        if (btnAudio == v) {
            Intent intent = new Intent();
            intent.setClass(this, AudioActivity.class);
            startActivity(intent);
        } else if (btnVideo == v) {
            Intent intent = new Intent();
            intent.setClass(this, VideoActivity.class);
            startActivity(intent);
        }
    }
}
