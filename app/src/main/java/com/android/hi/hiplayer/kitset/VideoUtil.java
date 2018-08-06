package com.android.hi.hiplayer.kitset;

import android.content.Context;
import android.media.MediaCodec;
import android.media.MediaCodecInfo;
import android.media.MediaExtractor;
import android.media.MediaFormat;
import android.media.MediaMetadataRetriever;
import android.media.MediaMuxer;
import android.os.Environment;
import android.text.TextUtils;
import android.util.Log;

import com.android.birdidi.core.kitset.IOUtil;
import com.android.hi.hiplayer.domain.VideoInfo;

import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;

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

    private MediaExtractor mMediaExtractor;

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

    private MediaMuxer mMediaMuxer;

    private VideoInfo mVideoInfo;

    public VideoUtil() {
        try {
            mVideoEncoder = MediaCodec.createEncoderByType("video/avc");
            mVideoDecoder = MediaCodec.createDecoderByType("video/avc");

            mAudioEncoder = MediaCodec.createEncoderByType("audio/mp4a-latm");
            mAudioDecoder = MediaCodec.createDecoderByType("audio/mp4a-latm");

            mMediaExtractor = new MediaExtractor();
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
        Log.d(TAG, "extractVideoInfo() called with: dataSource = [" + dataSource + "]");
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

    public void aa(Context context, String dataSource, String directPath) throws Exception {
        Log.d(TAG, "aa() called with: context = [" + context + "], dataSource = [" + dataSource + "], directPath = [" + directPath + "]");
        extractVideoInfo(dataSource);
        Log.e(TAG, "aa: source = " + mVideoInfo);
        mMediaExtractor.setDataSource(dataSource);

        //轨道数目
        int trackCount = mMediaExtractor.getTrackCount();
        Log.e(TAG, "aa: track count = " + trackCount);
        for (int i = 0; i < trackCount; i++) {
            MediaFormat mediaFormat = mMediaExtractor.getTrackFormat(i);
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
        mMediaExtractor.selectTrack(mVideoTrackIndex);
        long firstVideoTime = mMediaExtractor.getSampleTime();
        Log.e(TAG, "aa: sample time = " + firstVideoTime);
        mMediaExtractor.seekTo(firstVideoTime, MediaExtractor.SEEK_TO_PREVIOUS_SYNC);

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
        //编码器
        mVideoEncoder.configure(encodeMediaFormat, null, null, MediaCodec.CONFIGURE_FLAG_ENCODE);
        mVideoEncoder.start();
        //解码器
        mVideoDecoder.configure(mVideoMediaFormat, null, null, 0);
        mVideoDecoder.start();


        mMediaMuxer = new MediaMuxer(directPath, MediaMuxer.OutputFormat.MUXER_OUTPUT_MPEG_4);
        mMediaExtractor.release();
    }

    /**
     * 抽取纯视频文件（可播放）
     *
     * @param dataSource
     * @param outputPath
     */
    public void extractPureVideoFile(String dataSource, String outputPath) {
        Log.d(TAG, "extractPureVideoFile() called with: dataSource = [" + dataSource + "], outputPath = [" + outputPath + "]");
        MediaExtractor mediaExtractor = new MediaExtractor();
        int targetTrackIndex = -1;
        MediaMuxer mediaMuxer = null;

        try {
            mediaExtractor.setDataSource(dataSource);
            targetTrackIndex = obtainTrackIndex(0, mediaExtractor);

            if (targetTrackIndex < 0) {
                return;
            }

            mediaExtractor.selectTrack(targetTrackIndex);
            MediaFormat trackFormat = mediaExtractor.getTrackFormat(targetTrackIndex);
            mediaMuxer = new MediaMuxer(outputPath, MediaMuxer.OutputFormat.MUXER_OUTPUT_MPEG_4);
            //追踪信道
            int writeVideoIndex = mediaMuxer.addTrack(trackFormat);
            ByteBuffer byteBuffer = ByteBuffer.allocate(500 * 1024);
            MediaCodec.BufferInfo bufferInfo = new MediaCodec.BufferInfo();
            mediaMuxer.start();

            while (true) {
                //读取帧之间的数据
                int readSampleSize = mediaExtractor.readSampleData(byteBuffer, 0);
                if (readSampleSize < 0) {
                    break;
                }
                bufferInfo.size = readSampleSize;
                bufferInfo.offset = 0;
                bufferInfo.flags = mediaExtractor.getSampleFlags();
                bufferInfo.presentationTimeUs = mediaExtractor.getSampleTime();
                mediaExtractor.advance();
                //写入帧数据
                mediaMuxer.writeSampleData(writeVideoIndex, byteBuffer, bufferInfo);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (mediaMuxer != null) {
                mediaMuxer.stop();
            }
            mediaExtractor.release();
            if (mediaMuxer != null) {
                mediaMuxer.release();
            }
        }
    }

    /**
     * 抽取纯音频文件（可播放）
     *
     * @param dataSource
     * @param outputPath
     */
    public void extractPureAudioFile(String dataSource, String outputPath) {
        Log.d(TAG, "extractPureAudioFile() called with: dataSource = [" + dataSource + "], outputPath = [" + outputPath + "]");
        MediaExtractor mediaExtractor = new MediaExtractor();
        int targetTrackIndex = -1;
        MediaMuxer mediaMuxer = null;

        try {
            mediaExtractor.setDataSource(dataSource);

            targetTrackIndex = obtainTrackIndex(1, mediaExtractor);

            if (targetTrackIndex < 0) {
                return;
            }

            mediaExtractor.selectTrack(targetTrackIndex);
            mediaMuxer = new MediaMuxer(outputPath, MediaMuxer.OutputFormat.MUXER_OUTPUT_MPEG_4);
            int writeAudioIndex = mediaMuxer.addTrack(mediaExtractor.getTrackFormat(targetTrackIndex));
            mediaMuxer.start();

            ByteBuffer byteBuffer = ByteBuffer.allocate(500 * 1024);
            MediaCodec.BufferInfo bufferInfo = new MediaCodec.BufferInfo();


            while (true) {
                int readSampleSize = mediaExtractor.readSampleData(byteBuffer, 0);
                if (readSampleSize < 0) {
                    break;
                }
                bufferInfo.size = readSampleSize;
                bufferInfo.offset = 0;
                bufferInfo.flags = mediaExtractor.getSampleFlags();
                bufferInfo.presentationTimeUs = mediaExtractor.getSampleTime();

                mediaExtractor.advance();

                mediaMuxer.writeSampleData(writeAudioIndex, byteBuffer, bufferInfo);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (mediaMuxer != null) {
                mediaMuxer.stop();
            }
            mediaExtractor.release();
            if (mediaMuxer != null) {
                mediaMuxer.release();
            }
        }
    }

    /**
     * 抽离音、视频轨道文件(原始文件)
     *
     * @param dataSource
     * @param outputVideoPath
     * @param outputAudioPath
     */
    private void separateTrackToFile(String dataSource, String outputVideoPath, String outputAudioPath) {
        Log.d(TAG, "extractMediaTrackToFile() called with: dataSource = [" + dataSource + "], outputVideoPath = [" + outputVideoPath + "], outputAudioPath = [" + outputAudioPath + "]");
        FileOutputStream videoFOS = null;
        FileOutputStream audioFOS = null;

        MediaExtractor mediaExtractor = new MediaExtractor();
        try {
            mediaExtractor.setDataSource(dataSource);

            videoFOS = new FileOutputStream(outputVideoPath);
            audioFOS = new FileOutputStream(outputAudioPath);

            ByteBuffer byteBuffer = ByteBuffer.allocate(500 * 1024);

            //抽取视频信道
            mediaExtractor.selectTrack(mVideoTrackIndex);
            Log.e(TAG, "extractMediaTrackToFile: video start...");
            while (true) {
                int readSampleCount = mediaExtractor.readSampleData(byteBuffer, 0);
                if (readSampleCount < 0) {
                    break;
                }
                byte[] buffer = new byte[readSampleCount];
                byteBuffer.get(buffer);
                videoFOS.write(buffer);
                byteBuffer.clear();
                mediaExtractor.advance();
            }
            Log.e(TAG, "extractMediaTrackToFile: video end.");

            //抽取音频信道
            Log.e(TAG, "extractMediaTrackToFile: audio start...");
            mediaExtractor.selectTrack(mAudioTrackIndex);
            while (true) {
                int readSampleCount = mediaExtractor.readSampleData(byteBuffer, 0);
                if (readSampleCount < 0) {
                    break;
                }

                byte[] buffer = new byte[readSampleCount];
                byteBuffer.get(buffer);
                audioFOS.write(buffer);
                byteBuffer.clear();
                mediaExtractor.advance();
            }
            Log.e(TAG, "extractMediaTrackToFile: audio end.");
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            IOUtil.slientClose(videoFOS);
            IOUtil.slientClose(audioFOS);
            mediaExtractor.release();
        }
    }

    /**
     * 合成视频
     *
     * @param pureVideoPath
     * @param pureAudioPath
     * @param directPath
     */
    public void combineVideo(String pureVideoPath, String pureAudioPath, String directPath) {
        Log.d(TAG, "combineVideo() called with: pureVideoPath = [" + pureVideoPath + "], pureAudioPath = [" + pureAudioPath + "], directPath = [" + directPath + "]");
        MediaExtractor videoExtractor = new MediaExtractor();
        int videoTrackIndex = -1;
        try {
            videoExtractor.setDataSource(pureVideoPath);
            videoTrackIndex = obtainTrackIndex(0, videoExtractor);
        } catch (Exception e) {
            e.printStackTrace();
        }

        MediaExtractor audioExtractor = new MediaExtractor();
        int audioTrackIndex = -1;
        try {
            audioExtractor.setDataSource(pureAudioPath);
            audioTrackIndex = obtainTrackIndex(1, audioExtractor);
        } catch (Exception e) {
            e.printStackTrace();
        }

        if (audioTrackIndex < 0 || videoTrackIndex < 0) {
            return;
        }

        videoExtractor.selectTrack(videoTrackIndex);
        audioExtractor.selectTrack(audioTrackIndex);

        MediaCodec.BufferInfo videoBufferInfo = new MediaCodec.BufferInfo();
        MediaCodec.BufferInfo audioBufferInfo = new MediaCodec.BufferInfo();

        MediaMuxer mediaMuxer = null;

        try {
            mediaMuxer = new MediaMuxer(directPath, MediaMuxer.OutputFormat.MUXER_OUTPUT_MPEG_4);
            int writeVideoIndex = mediaMuxer.addTrack(videoExtractor.getTrackFormat(videoTrackIndex));
            int writeAudioIndex = mediaMuxer.addTrack(audioExtractor.getTrackFormat(audioTrackIndex));
            mediaMuxer.start();

            ByteBuffer byteBuffer = ByteBuffer.allocate(500 * 1024);
            while (true) {
                int readSampleSize = videoExtractor.readSampleData(byteBuffer, 0);
                if (readSampleSize < 0) {
                    break;
                }

                videoBufferInfo.size = readSampleSize;
                videoBufferInfo.flags = videoExtractor.getSampleFlags();
                videoBufferInfo.offset = 0;
                videoBufferInfo.presentationTimeUs = videoExtractor.getSampleTime();

                videoExtractor.advance();
                mediaMuxer.writeSampleData(writeVideoIndex, byteBuffer, videoBufferInfo);
            }

            while (true) {
                int readSampleSize = audioExtractor.readSampleData(byteBuffer, 0);
                if (readSampleSize < 0) {
                    break;
                }

                audioBufferInfo.size = readSampleSize;
                audioBufferInfo.flags = audioExtractor.getSampleFlags();
                audioBufferInfo.offset = 0;
                audioBufferInfo.presentationTimeUs = audioExtractor.getSampleTime();

                audioExtractor.advance();
                mediaMuxer.writeSampleData(writeAudioIndex, byteBuffer, audioBufferInfo);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (mediaMuxer != null) {
                mediaMuxer.stop();
            }
            videoExtractor.release();
            audioExtractor.release();
            if (mediaMuxer != null) {
                mediaMuxer.release();
            }
        }
    }

    private int obtainTrackIndex(int mimeType, MediaExtractor extractor) {
        String mimePrefix = mimeType == 0 ? "video/" : mimeType == 1 ? "audio/" : null;
        int trackIndex = -1;

        if (TextUtils.isEmpty(mimePrefix)) {
            return trackIndex;
        }
        int trackCount = extractor.getTrackCount();
        for (int i = 0; i < trackCount; i++) {
            MediaFormat format = extractor.getTrackFormat(i);
            String mime = format.getString(MediaFormat.KEY_MIME);
            if (mime.startsWith(mimePrefix)) {
                trackIndex = i;
                break;
            }
        }
        return trackIndex;
    }
}
