package com.android.hi.hiplayer.player;

import android.util.Log;

import com.google.android.exoplayer2.ExoPlaybackException;
import com.google.android.exoplayer2.ExoPlayer;
import com.google.android.exoplayer2.PlaybackParameters;
import com.google.android.exoplayer2.Timeline;
import com.google.android.exoplayer2.source.TrackGroupArray;
import com.google.android.exoplayer2.trackselection.TrackSelectionArray;

public class SimpleEventListener implements ExoPlayer.EventListener {
    @Override
    public void onTimelineChanged(Timeline timeline, Object manifest) {
        Log.d("cxydebug", "onTimelineChanged() called with: timeline = [" + timeline + "], manifest = [" + manifest + "]");
    }

    @Override
    public void onTracksChanged(TrackGroupArray trackGroups, TrackSelectionArray trackSelections) {
        Log.d("cxydebug", "onTracksChanged() called with: trackGroups = [" + trackGroups + "], trackSelections = [" + trackSelections + "]");
    }

    @Override
    public void onLoadingChanged(boolean isLoading) {
        Log.d("cxydebug", "onLoadingChanged() called with: isLoading = [" + isLoading + "]");
    }

    @Override
    public void onPlayerStateChanged(boolean playWhenReady, int playbackState) {
        Log.d("cxydebug", "onPlayerStateChanged() called with: playWhenReady = [" + playWhenReady + "], playbackState = [" + playbackState + "]");
    }

    @Override
    public void onPlayerError(ExoPlaybackException error) {
        Log.d("cxydebug", "onPlayerError() called with: error = [" + error + "]");
    }

    @Override
    public void onPositionDiscontinuity() {
        Log.d("cxydebug", "onPositionDiscontinuity() called");
    }

    @Override
    public void onPlaybackParametersChanged(PlaybackParameters playbackParameters) {
        Log.d("cxydebug", "onPlaybackParametersChanged() called with: playbackParameters = [" + playbackParameters + "]");
    }
}
