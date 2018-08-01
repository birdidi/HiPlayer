package com.android.hi.hiplayer.domain;

public class VideoInfo {

    public int width;
    public int height;
    public int rotation;

    @Override
    public String toString() {
        return "VideoInfo{" +
                "width=" + width +
                ", height=" + height +
                ", rotation=" + rotation +
                '}';
    }
}
