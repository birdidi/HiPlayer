package com.android.hi.hiplayer.player;

import android.content.Context;
import android.net.Uri;
import android.view.Surface;
import android.view.SurfaceView;
import android.view.TextureView;

import com.google.android.exoplayer2.DefaultLoadControl;
import com.google.android.exoplayer2.ExoPlayerFactory;
import com.google.android.exoplayer2.LoadControl;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.extractor.DefaultExtractorsFactory;
import com.google.android.exoplayer2.extractor.ExtractorsFactory;
import com.google.android.exoplayer2.source.ExtractorMediaSource;
import com.google.android.exoplayer2.source.MediaSource;
import com.google.android.exoplayer2.trackselection.AdaptiveTrackSelection;
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector;
import com.google.android.exoplayer2.trackselection.TrackSelection;
import com.google.android.exoplayer2.trackselection.TrackSelector;
import com.google.android.exoplayer2.upstream.BandwidthMeter;
import com.google.android.exoplayer2.upstream.DataSource;
import com.google.android.exoplayer2.upstream.DefaultBandwidthMeter;
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory;
import com.google.android.exoplayer2.util.Util;

public class Player {

    private static final Player sInstance = new Player();

    private Player() {
    }

    public static final Player get() {
        return sInstance;
    }

    private volatile boolean isPrepared = false;
    private SimpleExoPlayer mPlayer;
    private Context mContext;

    public void prepare(Context context) {
        this.mContext = context;
        initPlayer(context);
        isPrepared = true;
    }

    private Context getContext() {
        return mContext;
    }

    private void initPlayer(Context context) {
        BandwidthMeter bandwidthMeter = new DefaultBandwidthMeter();
        TrackSelection.Factory videoTrackSelectionFactory = new AdaptiveTrackSelection.Factory(bandwidthMeter);

        TrackSelector trackSelector = new DefaultTrackSelector(videoTrackSelectionFactory);
        LoadControl loadControl = new DefaultLoadControl();

        mPlayer = ExoPlayerFactory.newSimpleInstance(context, trackSelector, loadControl);
    }

    public void setSurface(SurfaceView surface) {
        mPlayer.setVideoSurfaceView(surface);
    }

    public void setSurface(TextureView textureView) {
        mPlayer.setVideoTextureView(textureView);
    }

    public void setSurface(Surface surface) {
        mPlayer.setVideoSurface(surface);
    }

    public void play(String uri) throws Exception {
        checkPrepared();
        BandwidthMeter bandwidthMeter = new DefaultBandwidthMeter();
        DataSource.Factory dataSourceFactory = new DefaultDataSourceFactory(getContext(), Util.getUserAgent(getContext(), "HiPlayer"));

        ExtractorsFactory extractorsFactory = new DefaultExtractorsFactory();

        MediaSource mediaSource = new ExtractorMediaSource(Uri.parse(uri), dataSourceFactory, extractorsFactory, null, null);

        mPlayer.prepare(mediaSource);

        mPlayer.addListener(new SimpleEventListener());

        mPlayer.setPlayWhenReady(true);
    }

    public void stop() throws Exception {
        checkPrepared();
        mPlayer.stop();
    }

    public void release() throws Exception {
        checkPrepared();
        mPlayer.release();
    }

    private void checkPrepared() throws Exception {
        if (!isPrepared) {
            throw new Exception("player must to be initialized!");
        }
    }
}
