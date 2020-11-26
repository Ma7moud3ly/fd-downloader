package com.ma7moud3ly.fd.activities;


import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;

import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.material.snackbar.Snackbar;
import com.ma7moud3ly.fd.App;
import com.ma7moud3ly.fd.R;
import com.ma7moud3ly.fd.adapters.VideosAdapter;
import com.ma7moud3ly.fd.databinding.ActivityHomeBinding;
import com.ma7moud3ly.fd.downloader.FBVideo;
import com.ma7moud3ly.fd.downloader.SavedVideo;
import com.ma7moud3ly.fd.observers.VideoObserver;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class HomeActivity extends BaseActivity {
    private ActivityHomeBinding binding;
    private VideoObserver observer;
    private List videos = new ArrayList<SavedVideo>();
    private VideosAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityHomeBinding.inflate(getLayoutInflater());
        videoPlayer = new SimpleExoPlayer.Builder(this).build();
        observer = new VideoObserver(videoPlayer);
        binding.setObserver(observer);
        setContentView(binding.getRoot());
        initSearch();
        initVideoPlayer();
        initVideosRecycler();
        if (getIntent().hasExtra("video") && getIntent().hasExtra("position")) {
            String video = getIntent().getStringExtra("video");
            Long position = getIntent().getLongExtra("position", 0);
            observer.playVideo(video, position, this);
        }
    }

    @Override
    protected void onResume() {
        String url = readClipboard();
        App.L("#" + url);
        if (FBVideo.isFbVideo(url))
            binding.searchLayout.searchBox.setText(url);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
        getSavedVideos();
        super.onResume();
    }

    private void initSearch() {
        binding.searchLayout.searchBox.setOnEditorActionListener((textView, i, keyEvent) -> {
            if (i == EditorInfo.IME_ACTION_SEARCH) {
                fetchVideo(textView.getText().toString());
                observer.play.set(false);
            }
            return false;
        });
        binding.searchLayout.searchBox.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                binding.searchLayout.searchClear.setVisibility(editable.length() > 0 ? View.VISIBLE : View.GONE);
            }
        });
        binding.searchLayout.searchButton.setOnClickListener(v -> {
            String url = binding.searchLayout.searchBox.getText().toString();
            if (url.equals("")) {
                String paste = readClipboard();
                binding.searchLayout.searchBox.setText(paste);
                fetchVideo(paste);
            } else fetchVideo(url);
            observer.play.set(false);
        });

        binding.searchLayout.searchClear.setOnClickListener(v -> {
            binding.searchLayout.searchBox.setText("");
            //clearClipboard();
            videoUrl = "";
        });

        binding.searchLayout.about.setOnClickListener(v -> {
            startAboutActivity();
        });

    }

    private void getSavedVideos() {
        observer.noDownloads.set(true);
        File[] files = App.VIDEO_DIR.listFiles();
        if (files == null) return;
        videos.clear();
        for (int i = 0; i < files.length; i++) {
            File video = files[i];
            String name = video.getName();
            if (!name.endsWith(".mp4")) continue;
            SavedVideo sv = new SavedVideo();
            sv.name = name.replace(".mp4", "");
            sv.dir = video.getParent();
            videos.add(sv);
        }
        if (videos.size() > 0 && adapter != null) {
            observer.noDownloads.set(false);
            Collections.reverse(videos);
            adapter.notifyDataSetChanged();
        }
    }

    private void initVideosRecycler() {
        binding.recycler.setHasFixedSize(true);
        GridLayoutManager gridLayoutManager = new GridLayoutManager(this, 1, GridLayoutManager.VERTICAL, false);
        binding.recycler.setLayoutManager(gridLayoutManager);
        adapter = new VideosAdapter(videos, observer);
        binding.recycler.setAdapter(adapter);
        binding.recycler.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                observer.stopVideo(binding.exoplayer);
            }
        });
    }

    private void initVideoPlayer() {
        binding.exoplayer.setPlayer(videoPlayer);
        binding.exoplayer.setControllerVisibilityListener(visibility -> {
            if (visibility == 0) {//visible
                observer.showControllers.set(true);
            } else {
                observer.showControllers.set(false);
            }
        });
    }

    public void back(View v) {
        videoPlayer.stop(true);
        finish();
    }

    public void deleteVideo(View v) {
        if (observer.play.get()) {
            File video_file = new File(videoPlayer.getCurrentMediaItem().mediaId);
            if (video_file.exists()) {
                Snackbar snackbar = Snackbar
                        .make(binding.getRoot(), getString(R.string.confirm_delete), Snackbar.LENGTH_LONG)
                        .setAction("YES", view -> {
                            observer.stopVideo(binding.exoplayer);
                            video_file.delete();
                            getSavedVideos();
                        });
                snackbar.show();
            }
        }

    }

    @Override
    protected void onDestroy() {
        videoPlayer.release();
        super.onDestroy();
    }
}
