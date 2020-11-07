package com.ma7moud3ly.fd;

import android.app.Application;
import android.content.Context;
import android.os.Environment;
import android.util.Log;
import android.widget.Toast;

import com.google.android.exoplayer2.SimpleExoPlayer;

public class App extends Application {
    private static Context context;
    public static String APP_DIR;
    public static String APP_NAME = "F-DOWNLOADER";
    public static String SAVE_DIR = Environment.DIRECTORY_DOWNLOADS + "/" + APP_NAME;
    public static String SAVE_PATH = Environment.getExternalStorageDirectory() + "/" + SAVE_DIR;
    private final static String DEBUG_TAG = "HINT";

    @Override
    public void onCreate() {
        super.onCreate();
        this.context = getApplicationContext();
        APP_DIR = getApplicationInfo().dataDir;
    }

    public static void toast(Object o) {
        Toast.makeText(getContext(), o.toString(), Toast.LENGTH_SHORT).show();
    }


    public static void L(Object msg) {
        Log.i(DEBUG_TAG, msg.toString());
    }

    public static Context getContext() {
        return context;
    }


}
