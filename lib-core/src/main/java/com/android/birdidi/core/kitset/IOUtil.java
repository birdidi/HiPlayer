package com.android.birdidi.core.kitset;

import java.io.Closeable;

public class IOUtil {

    public static void slientClose(Closeable closeable){
        if(closeable != null){
            try {
                closeable.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
