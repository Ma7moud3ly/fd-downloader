package com.ma7moud3ly.fd.activities;

import android.Manifest;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.widget.Toast;

import com.google.android.exoplayer2.SimpleExoPlayer;
import com.ma7moud3ly.fd.App;
import com.ma7moud3ly.fd.R;
import com.ma7moud3ly.fd.downloader.FBVideo;

import java.io.File;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import static android.content.ClipDescription.MIMETYPE_TEXT_PLAIN;

public class BaseActivity extends AppCompatActivity {
    public SimpleExoPlayer videoPlayer;
    public static String videoUrl = "";

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
    protected void onResume() {
        String url = readClipboard();
        if (videoUrl.equals(url) == false)
            fetchVideo(url);
        super.onResume();
    }

    //check or request storage permission
    public boolean isStoragePermissionGranted() {
        if (Build.VERSION.SDK_INT >= 23) {
            if (checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE)
                    == PackageManager.PERMISSION_GRANTED) {
                return true;
            } else {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.
                        WRITE_EXTERNAL_STORAGE}, 1);
                return false;
            }
        } else {
            return true;
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            File file = new File(App.SAVE_DIR);
            file.mkdirs();
            Toast.makeText(getApplicationContext(), getResources().getString(R.string.download_enabled), Toast.LENGTH_LONG).show();
        } else {
            Toast.makeText(getApplicationContext(), getResources().getString(R.string.need_storage_permission), Toast.LENGTH_LONG).show();
            finish();
        }
    }


}