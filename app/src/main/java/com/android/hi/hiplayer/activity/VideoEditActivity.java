package com.android.hi.hiplayer.activity;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.android.birdidi.core.config.BaseConfig;
import com.android.birdidi.core.kitset.FileUtil;
import com.android.hi.hiplayer.R;
import com.android.hi.hiplayer.kitset.VideoUtil;

import java.net.URISyntaxException;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import rx.Observable;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.schedulers.Schedulers;

public class VideoEditActivity extends AppCompatActivity {

    private static final String SOURCE_PATH = Environment.getExternalStorageDirectory() + "/abc.mp4";

    private static final int REQUEST_CODE_PURE_VIDEO = 1;
    private static final int REQUEST_CODE_PURE_AUDIO = 2;
    private static final int REQUEST_CODE_COMBINE_VIDEO = 3;
    private static final int REQUEST_CODE_COMBINE_AUDIO = 4;

    VideoUtil videoUtil;
    @Bind(R.id.btn_extract_video)
    Button btnExtractVideo;
    @Bind(R.id.tv_path_extract_video)
    TextView tvPathExtractVideo;
    @Bind(R.id.btn_extract_audio)
    Button btnExtractAudio;
    @Bind(R.id.tv_path_extract_audio)
    TextView tvPathExtractAudio;
    @Bind(R.id.btn_combine_video)
    Button btnCombineVideo;
    @Bind(R.id.tv_path_combine_video)
    TextView tvPathCombineVideo;
    @Bind(R.id.tv_path_combine_audio)
    TextView tvPathCombineAudio;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video_edit);
        ButterKnife.bind(this);
        videoUtil = new VideoUtil();
        init();
    }

    private void init() {

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == Activity.RESULT_OK) {
            Uri uri = data.getData();
            String file = null;
            if (uri != null) {
                try {
                    file = FileUtil.getPath(VideoEditActivity.this, uri);
                } catch (URISyntaxException e) {
                    e.printStackTrace();
                }
            }
            switch (requestCode) {
                case REQUEST_CODE_PURE_VIDEO:
                    tvPathExtractVideo.setText(file);
                    break;
                case REQUEST_CODE_PURE_AUDIO:
                    tvPathExtractAudio.setText(file);
                    break;
                case REQUEST_CODE_COMBINE_VIDEO:
                    tvPathCombineVideo.setText(file);
                    break;
                case REQUEST_CODE_COMBINE_AUDIO:
                    tvPathCombineAudio.setText(file);
                    break;
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    @OnClick({R.id.btn_extract_video, R.id.tv_path_extract_video, R.id.btn_extract_audio, R.id.tv_path_extract_audio, R.id.btn_combine_video, R.id.tv_path_combine_video, R.id.tv_path_combine_audio})
    public void onViewClicked(View view) {
        String path = null;
        String outputPath = null;
        switch (view.getId()) {
            case R.id.btn_extract_video:
                if (TextUtils.isEmpty(path = checkFileValid(tvPathExtractVideo))) return;
                outputPath = BaseConfig.getBaseDir() + "/" + System.currentTimeMillis() + ".mp4";
                videoUtil.extractPureVideoFile(path, outputPath);
                Toast.makeText(this, "导出：" + outputPath, Toast.LENGTH_SHORT).show();
                break;
            case R.id.tv_path_extract_video:
                openFileChooser("video/*", REQUEST_CODE_PURE_VIDEO);
                break;
            case R.id.btn_extract_audio:
                if (TextUtils.isEmpty(path = checkFileValid(tvPathExtractAudio))) return;
                outputPath = BaseConfig.getBaseDir() + "/" + System.currentTimeMillis() + ".mp3";
                final String extAudioSource = path;
                final String extAudioOutput = outputPath;
                Observable.create(new Observable.OnSubscribe<String>() {
                    @Override
                    public void call(Subscriber<? super String> subscriber) {
                        videoUtil.extractPureAudioFile(extAudioSource, extAudioOutput);
                        subscriber.onNext(extAudioOutput);
                        subscriber.onCompleted();
                    }
                })
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(new Action1<String>() {
                    @Override
                    public void call(String s) {
                        Toast.makeText(VideoEditActivity.this, "导出：" + s, Toast.LENGTH_SHORT).show();
                    }
                });
                break;
            case R.id.tv_path_extract_audio:
                openFileChooser("video/*", REQUEST_CODE_PURE_AUDIO);
                break;
            case R.id.btn_combine_video:
                String audioPath, videoPath;
                if (TextUtils.isEmpty(audioPath = checkFileValid(tvPathCombineAudio))) return;
                if (TextUtils.isEmpty(videoPath = checkFileValid(tvPathCombineVideo))) return;
                outputPath = BaseConfig.getBaseDir() + "/" + System.currentTimeMillis() + ".mp4";
                videoUtil.combineVideo(videoPath,
                        audioPath,
                        outputPath);
                Toast.makeText(this, "导出：" + outputPath, Toast.LENGTH_SHORT).show();
                break;
            case R.id.tv_path_combine_video:
                openFileChooser("video/*", REQUEST_CODE_COMBINE_VIDEO);
                break;
            case R.id.tv_path_combine_audio:
                openFileChooser("video/*;audio/*", REQUEST_CODE_COMBINE_AUDIO);
                break;
        }
    }

    private String checkFileValid(TextView tv) {
        String path = tv.getText().toString();
        if (TextUtils.isEmpty(path)) {
            Toast.makeText(this, "请先选择文件", Toast.LENGTH_SHORT).show();
            return null;
        }
        return path;
    }

    private void openFileChooser(String mime, int requestCode) {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType(mime);
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        startActivityForResult(intent, requestCode);
    }
}
