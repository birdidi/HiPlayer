package com.android.birdidi.core.config;

import android.os.Environment;

public class BaseConfig {

    private static final String BASE_DIR = Environment.getExternalStorageDirectory() + "/HiPlayer/";

    public static String getBaseDir() {
        return BASE_DIR;
    }
}
