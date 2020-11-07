package com.ma7moud3ly.fd.activities;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.lifecycle.ViewModelProvider;

import com.google.android.exoplayer2.MediaItem;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.ma7moud3ly.fd.App;
import com.ma7moud3ly.fd.R;

import android.view.View;
import android.widget.Toast;

import com.ma7moud3ly.fd.databinding.ActivityVideoBinding;
import com.ma7moud3ly.fd.downloader.FBVideo;
import com.ma7moud3ly.fd.downloader.Video;
import com.ma7moud3ly.fd.models.VideoViewModel;
import com.ma7moud3ly.fd.observers.VideoObserver;

import java.io.File;


public class VideoActivity extends BaseActivity {

    private ActivityVideoBinding binding;
    private VideoViewModel model;
    private VideoObserver observer;
    private ProgressDialog progress;
    private Video video = null;
    private SimpleExoPlayer exoPlayer;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityVideoBinding.inflate(getLayoutInflater());
        exoPlayer = new SimpleExoPlayer.Builder(this).build();
        observer = new VideoObserver(exoPlayer);
        binding.setObserver(observer);

        setContentView(binding.getRoot());
        model = new ViewModelProvider(this).get(VideoViewModel.class);

        model.video.observe(this, video -> {
            progress.dismiss();
            observer.loading.set(false);
            if (video == null) {
                App.toast(getString(R.string.invalid_link));
                finish();
                return;
            }
            ;
            this.video = video;
            binding.headerLayout.title.setText(video.name);
            video.name = FBVideo.editVideoName(video.name);
            binding.setVideo(video);
        });

        progress = new ProgressDialog(this);
        progress.setMessage(getString(R.string.loading));
        progress.show();


        if (getIntent().hasExtra("video")) {
            String url = getIntent().getStringExtra("video");
            model.load(url);
        }
        initVideoPlayer();
    }

    private void initVideoPlayer() {
        binding.exoplayer.setPlayer(exoPlayer);
        binding.exoplayer.setControllerVisibilityListener(visibility -> {
            if (visibility == 0) {//visible
                observer.showControllers.set(true);
            } else {
                observer.showControllers.set(false);
            }
        });
    }

    public void download(View v) {
        if (!isStoragePermissionGranted()) return;
        String path = App.SAVE_PATH + "/" + video.name + ".mp4";
        if (new File(path).exists()) {
            App.toast(getString(R.string.video_exists));
            return;
        }
        model.downloadVideo(video);
        model.downloadThumbnail(video);
    }

    @Override
    public void onDestroy() {
        exoPlayer.release();
        super.onDestroy();
    }

    public void back(View v) {
        exoPlayer.stop(true);
        finish();
    }

}