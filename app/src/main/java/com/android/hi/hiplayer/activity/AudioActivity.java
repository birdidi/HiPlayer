package com.android.hi.hiplayer.activity;

import android.media.AudioFormat;
import android.media.AudioRecord;
import android.media.MediaRecorder;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import com.android.hi.hiplayer.R;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;

public class AudioActivity extends AppCompatActivity implements View.OnClickListener {

    private Button btnAudioRecord;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_audio);

        btnAudioRecord = findViewById(R.id.btn_audio_record);
        btnAudioRecord.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        if (v == btnAudioRecord) {
            audioRecord();
        }
    }

    private void audioRecord() {

        int recordBufferSize = AudioRecord.getMinBufferSize(441 * 100, 1, AudioFormat.ENCODING_PCM_16BIT);
        AudioRecord audioRecord = new AudioRecord(MediaRecorder.AudioSource.MIC, 441 * 100, 1, AudioFormat.ENCODING_PCM_16BIT, recordBufferSize);

        byte[] data = new byte[recordBufferSize];

        audioRecord.startRecording();
        boolean isRecording = true;

        OutputStream os = null;

        try {
            os = new FileOutputStream(Environment.getExternalStorageDirectory() + "/HiPlayer/" + (System.currentTimeMillis()) + ".wav");
            int flag = 0;
            while (isRecording) {
                flag = audioRecord.read(data, 0, recordBufferSize);
                if (flag != AudioRecord.ERROR_INVALID_OPERATION) {
                    os.write(data, 0, flag);
                } else {
                    isRecording = false;
                }
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (os != null) {
                try {
                    os.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
