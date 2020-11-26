package com.ma7moud3ly.fd.activities;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.view.View;

import com.google.android.exoplayer2.SimpleExoPlayer;
import com.ma7moud3ly.fd.downloader.FBVideo;
import com.ma7moud3ly.fd.services.FloatingWidgetService;
import com.mikhaellopez.circularprogressbar.CircularProgressBar;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import static android.content.ClipDescription.MIMETYPE_TEXT_PLAIN;

public class BaseActivity extends AppCompatActivity {
    public SimpleExoPlayer videoPlayer;
    public static String videoUrl = "";
    public Intent serviceIntent;

    public String readClipboard() {
        String text = "";
        ClipboardManager clipboard = (ClipboardManager) getSystemService(CLIPBOARD_SERVICE);
        if (clipboard.hasPrimaryClip()) {
            ClipData clip = clipboard.getPrimaryClip();
            if (clip.getDescription().hasMimeType(MIMETYPE_TEXT_PLAIN))
                text = clip.getItemAt(0).getText().toString();
            else text = clip.getItemAt(0).coerceToText(this).toString();
        }
        return text;
    }

    public void clearClipboard() {
        ClipboardManager clipBoard = (ClipboardManager) getSystemService(CLIPBOARD_SERVICE);
        ClipData data = ClipData.newPlainText("", "");
        clipBoard.setPrimaryClip(data);
    }

    public void startVideoActivity(String url) {
        if (videoPlayer != null) videoPlayer.stop(true);
        final Intent intent = new Intent(this, VideoActivity.class);
        intent.putExtra("video", url);
        startActivity(intent);
    }

    public void startAboutActivity() {
        final Intent intent = new Intent(this, AboutActivity.class);
        startActivity(intent);
    }

    public void fetchVideo(String url) {
        if (FBVideo.isFbVideo(url)) {
            videoUrl = url;
            startVideoActivity(url);
        }
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        serviceIntent = new Intent(this, FloatingWidgetService.class);
    }

    @Override
    protected void onResume() {
        String url = readClipboard();
        if (videoUrl.equals(url) == false)
            fetchVideo(url);
        stopService(serviceIntent);
        super.onResume();
    }
    public void playInBackground(View v) {
        if (videoPlayer.isPlaying()) {
            serviceIntent.putExtra("position", videoPlayer.getCurrentPosition());
            serviceIntent.putExtra("video", videoPlayer.getCurrentMediaItem().mediaId);
            if (canDrawOverlays()) {
                startService(serviceIntent);
                finish();
            }
        }
    }


    //draw over apps
    public boolean canDrawOverlays() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (!Settings.canDrawOverlays(this)) {
                Intent intent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
                        Uri.parse("package:" + getPackageName()));
                startActivityForResult(intent, 2);
                return false;
            } else {
                return true;
            }
        } else {
            return true;
        }
    }

}