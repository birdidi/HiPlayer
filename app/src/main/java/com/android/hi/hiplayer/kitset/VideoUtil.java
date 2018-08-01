package com.android.hi.hiplayer.kitset;

import android.content.Context;
import android.media.MediaCodec;
import android.media.MediaCodecInfo;
import android.media.MediaExtractor;
import android.media.MediaFormat;
import android.media.MediaMetadataRetriever;
import android.util.Log;

import com.android.hi.hiplayer.domain.VideoInfo;
import com.android.hi.hiplayer.widget.CameraGLSurfaceView;

import java.io.IOException;

/**
 * 1、利用MediaExtractor获取Mp4的音轨和视轨，获取音频视频的MediaFormat.
 * <p>
 * 2、根据音视频信息，创建视频解码器，视频编码器，音频暂时不处理就不创建编解码器了。
 * 其中视频解码器的Surface是通过先创建一个SurfaceTexture，然后将这个SurfaceTexture作为参数创建的，这样的话，视频流就可以通过这个SurfaceTexture提供给OpenGL环境作为输出。
 * 视频编码器的Surface可直接调用createInputSurface()方法创建，这个Surface后续传递给OpenGL环境作为输出
 * <p>
 * 3、创建MediaMuxer，用于后面合成处理后的视频和音频。
 * <p>
 * 4、创建OpenGL环境，用于处理视频图像，这个OpenGL环境由EGL创建，EGLSurface为WindowSurface，并以编码器创建的Surface作为参数。
 * <p>
 * 5、MediaExtractor读取原始Mp4中的视频流，交由解码器解码到Surface上。
 * <p>
 * 6、SurfaceTexture监听有视频帧时，通知OpenGL线程工作，处理视频图像，并渲染。
 * <p>
 * 7、OpenGL线程每次渲染完毕，通知编码线程进行编码，编码后的数据通过MediaMuxer混合。
 * <p>
 * 8、视频流处理完毕后，利用MediaExtractor读取音频流，并利用MediaMuxer混合到新的视频文件中。
 * <p>
 * 9、处理完毕后调用MediaMuxer的stop方法，处理后的视频就生成成功了
 */

public class VideoUtil {

    private static final String TAG = "VideoUtil";

    //视频解码器
    private MediaCodec mVideoDecoder;
    //音频解码器
    private MediaCodec mAudioDecoder;

    private MediaCodec mVideoEncoder;

    private MediaCodec mAudioEncoder;

    //源文件中视频轨道位置
    private int mVideoTrackIndex = -1;
    //源文件中视频MediaFormat
    private MediaFormat mVideoMediaFormat;

    //源文件中音频轨道位置
    private int mAudioTrackIndex;
    //源文件中音频MediaFormat
    private MediaFormat mAudioMediaFormat;

    private VideoInfo mVideoInfo;

    public VideoUtil() {
        try {
            mVideoEncoder = MediaCodec.createEncoderByType("video/avc");
            mVideoDecoder = MediaCodec.createDecoderByType("video/avc");

            mAudioEncoder = MediaCodec.createEncoderByType("audio/mp4a-latm");
            mAudioDecoder = MediaCodec.createDecoderByType("audio/mp4a-latm");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 提取视频源信息
     *
     * @param dataSource
     */
    private void extractVideoInfo(String dataSource) {
        MediaMetadataRetriever retriever = new MediaMetadataRetriever();
        retriever.setDataSource(dataSource);
        String width = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_VIDEO_WIDTH);
        String height = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_VIDEO_HEIGHT);
        String rotation = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_VIDEO_ROTATION);

        mVideoInfo = new VideoInfo();
        mVideoInfo.width = Integer.valueOf(width);
        mVideoInfo.height = Integer.valueOf(height);
        mVideoInfo.rotation = Integer.valueOf(rotation);
    }

    public void aa(Context context, String dataSource) throws Exception {
        extractVideoInfo(dataSource);
        Log.e(TAG, "aa: source = " + mVideoInfo);
        MediaExtractor extractor = new MediaExtractor();
        extractor.setDataSource(dataSource);

        //轨道数目
        int trackCount = extractor.getTrackCount();
        Log.e(TAG, "aa: track count = " + trackCount);
        for (int i = 0; i < trackCount; i++) {
            MediaFormat mediaFormat = extractor.getTrackFormat(i);
            String mime = mediaFormat.getString(MediaFormat.KEY_MIME);
            Log.e(TAG, "aa: mime = " + mime);
            if (mime.startsWith("audio")) {//音频轨
                mAudioTrackIndex = i;
                mAudioMediaFormat = mediaFormat;
            } else if (mime.startsWith("video")) {//视频轨
                mVideoTrackIndex = i;
                mVideoMediaFormat = mediaFormat;
            }
        }

        //选中当前操作的轨道-视频轨
        extractor.selectTrack(mVideoTrackIndex);
        long firstVideoTime = extractor.getSampleTime();
        Log.e(TAG, "aa: sample time = " + firstVideoTime);
        extractor.seekTo(firstVideoTime, MediaExtractor.SEEK_TO_PREVIOUS_SYNC);

        //编码media format
        MediaFormat encodeMediaFormat;
        if (mVideoInfo.rotation == 0 || mVideoInfo.rotation == 180) {
            encodeMediaFormat = MediaFormat.createVideoFormat("video/avc", mVideoInfo.width, mVideoInfo.height);
        } else {
            encodeMediaFormat = MediaFormat.createVideoFormat("video/avc", mVideoInfo.height, mVideoInfo.width);
        }
        //设置视频的编码参数
        //比特率
        encodeMediaFormat.setInteger(MediaFormat.KEY_BIT_RATE, 3000000);
        //帧率
        encodeMediaFormat.setInteger(MediaFormat.KEY_FRAME_RATE, 30);
        encodeMediaFormat.setInteger(MediaFormat.KEY_COLOR_FORMAT, MediaCodecInfo.CodecCapabilities.COLOR_FormatSurface);
        encodeMediaFormat.setInteger(MediaFormat.KEY_I_FRAME_INTERVAL, 1);
        mVideoEncoder.configure(encodeMediaFormat, null, null, MediaCodec.CONFIGURE_FLAG_ENCODE);
        mVideoEncoder.start();
        mVideoDecoder.configure(mVideoMediaFormat, new CameraGLSurfaceView(context).getHolder().getSurface(), null, 0);
        mVideoDecoder.start();

        extractor.release();
    }
}
