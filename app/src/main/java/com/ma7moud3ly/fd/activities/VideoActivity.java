package com.ma7moud3ly.fd.activities;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.view.View;

import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.material.snackbar.Snackbar;
import com.liulishuo.okdownload.DownloadListener;
import com.liulishuo.okdownload.DownloadTask;
import com.liulishuo.okdownload.core.cause.EndCause;
import com.liulishuo.okdownload.core.cause.ResumeFailedCause;
import com.liulishuo.okdownload.core.listener.DownloadListener1;
import com.liulishuo.okdownload.core.listener.assist.Listener1Assist;
import com.ma7moud3ly.fd.App;
import com.ma7moud3ly.fd.R;
import com.ma7moud3ly.fd.databinding.ActivityVideoBinding;
import com.ma7moud3ly.fd.downloader.FBVideo;
import com.ma7moud3ly.fd.downloader.Video;
import com.ma7moud3ly.fd.models.VideoViewModel;
import com.ma7moud3ly.fd.observers.VideoObserver;
import com.mikhaellopez.circularprogressbar.CircularProgressBar;

import java.io.File;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.ViewModelProvider;


public class VideoActivity extends BaseActivity {

    private ActivityVideoBinding binding;
    private VideoViewModel model;
    private VideoObserver observer;
    private ProgressDialog progress;
    private Video video = null;
    private SimpleExoPlayer exoPlayer;
    private DownloadTask downloadTask;

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
        } else {
            model.load("https://www.facebook.com/400318976815128/videos/1417366425110373/BM6ftIePszoTmI7_nScl-9cdqsw");
        }
        initVideoPlayer();
        initDownloadProgress(binding.circularProgressBar);
    }

    private void initDownloadProgress(CircularProgressBar circularProgressBar) {
        circularProgressBar.setProgress(0f);
        circularProgressBar.setProgressMax(100f);
        circularProgressBar.setProgressDirection(CircularProgressBar.ProgressDirection.TO_RIGHT);
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
        if (video == null) return;
        File video_file = new File(App.VIDEO_DIR, video.name + ".mp4");
        if (video_file.exists()) {
            Snackbar snackbar = Snackbar
                    .make(binding.getRoot(), getString(R.string.download_again), Snackbar.LENGTH_LONG)
                    .setAction("YES", view -> {
                        video_file.delete();
                        videoDownloader();
                    });
            snackbar.show();
            return;
        } else {
            videoDownloader();
        }
    }

    public void cancelDownload(View v) {
        if (downloadTask != null) downloadTask.cancel();
    }

    private void videoDownloader() {
        DownloadListener listener = new DownloadListener1() {
            @Override
            public void taskStart(@NonNull DownloadTask task, @NonNull Listener1Assist.Listener1Model model) {
                observer.downloading.set(true);
            }

            @Override
            public void retry(@NonNull DownloadTask task, @NonNull ResumeFailedCause cause) {

            }

            @Override
            public void connected(@NonNull DownloadTask task, int blockCount, long currentOffset, long totalLength) {

            }

            @Override
            public void progress(@NonNull DownloadTask task, long currentOffset, long totalLength) {
                Float percent = (float) ((currentOffset * 1.0) / totalLength) * 100;
                binding.circularProgressBar.setProgress(percent);
                binding.downloadPercent.setText("" + percent.intValue()+"%");
            }

            @Override
            public void taskEnd(@NonNull DownloadTask task, @NonNull EndCause cause, @Nullable Exception realCause, @NonNull Listener1Assist.Listener1Model model) {
                observer.downloading.set(false);
            }
        };
        downloadTask = new DownloadTask.Builder(video.url, App.VIDEO_DIR)
                .setFilename(video.name + ".mp4")
                // the minimal interval millisecond for callback progress
                .setMinIntervalMillisCallbackProcess(1000)
                // do re-download even if the task has already been completed in the past.
                .setPassIfAlreadyCompleted(false)
                .build();

        downloadTask.enqueue(listener);

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