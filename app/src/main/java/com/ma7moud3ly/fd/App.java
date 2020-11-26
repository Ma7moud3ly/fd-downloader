package com.ma7moud3ly.fd;

import android.app.Application;
import android.content.Context;
import android.os.Environment;
import android.util.Log;
import android.widget.Toast;

import java.io.File;

public class App extends Application {
    private static Context context;
    public static File VIDEO_DIR;
    private final static String DEBUG_TAG = "HINT";

    @Override
    public void onCreate() {
        super.onCreate();
        this.context = getApplicationContext();
        VIDEO_DIR = getExternalFilesDir(Environment.DIRECTORY_MOVIES);
        if (!VIDEO_DIR.exists()) VIDEO_DIR.mkdirs();
        L(VIDEO_DIR.getPath());
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
