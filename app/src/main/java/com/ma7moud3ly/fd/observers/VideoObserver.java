package com.ma7moud3ly.fd.observers;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.exoplayer2.MediaItem;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.ma7moud3ly.fd.App;
import com.ma7moud3ly.fd.R;
import com.ma7moud3ly.fd.downloader.SavedVideo;
import com.ma7moud3ly.fd.downloader.Video;

import java.io.File;

import androidx.databinding.BindingAdapter;
import androidx.databinding.ObservableField;

public class VideoObserver {
    public ObservableField<Boolean> loading = new ObservableField<>();
    public ObservableField<Boolean> play = new ObservableField<>();
    public ObservableField<Boolean> downloading = new ObservableField<>();
    public ObservableField<Boolean> showControllers = new ObservableField<>();
    public ObservableField<Boolean> noDownloads = new ObservableField<>();
    private SimpleExoPlayer exoPlayer;

    public VideoObserver(SimpleExoPlayer exoPlayer) {
        loading.set(true);
        play.set(false);
        showControllers.set(true);
        noDownloads.set(true);
        downloading.set(false);
        this.exoPlayer = exoPlayer;
    }

    public void playVideo(View v, Video video) {
        try {
            play.set(true);
            MediaItem mediaItem = MediaItem.fromUri(video.url);
            exoPlayer.setMediaItem(mediaItem);
            exoPlayer.prepare();
            exoPlayer.play();
        } catch (Exception e) {
            App.toast(v.getContext().getString(R.string.video_failed));
            e.printStackTrace();
        }
    }

    public void playVideo(String video, Long position, Context context) {
        try {
            play.set(true);
            MediaItem mediaItem = MediaItem.fromUri(video);
            exoPlayer.setMediaItem(mediaItem);
            exoPlayer.prepare();
            exoPlayer.seekTo(position);
            exoPlayer.play();
        } catch (Exception e) {
            App.toast(context.getString(R.string.video_failed));
            e.printStackTrace();
        }
    }

    public void playVideo(View v, SavedVideo video) {
        try {
            play.set(true);
            String path = video.dir + "/" + video.name + ".mp4";
            MediaItem mediaItem = MediaItem.fromUri(path);
            exoPlayer.setMediaItem(mediaItem);
            exoPlayer.prepare();
            exoPlayer.play();
        } catch (Exception e) {
            App.toast(v.getContext().getString(R.string.video_failed));
            e.printStackTrace();
        }
    }

    public void stopVideo(View v) {
        exoPlayer.stop(true);
        play.set(false);
    }

    public void copyVideo(View v, Video video) {
        if (video == null) return;
        ClipboardManager clipboard = (ClipboardManager) v.getContext().getSystemService(Context.CLIPBOARD_SERVICE);
        ClipData clip = ClipData.newPlainText("video link copied", video.url);
        clipboard.setPrimaryClip(clip);
        App.toast("copied..");
    }

    public void shareVideo(View v, Video video) {
        Context context = v.getContext();
        if (video == null) return;
        Intent intent = new Intent();
        intent.setAction(Intent.ACTION_SEND);
        intent.setType("text/plain");
        intent.putExtra(Intent.EXTRA_SUBJECT, context.getString(R.string.app_name));
        intent.putExtra(Intent.EXTRA_TEXT, video.url);
        try {
            context.startActivity(Intent.createChooser(intent, context.getString(R.string.app_name)));
        } catch (Exception e) {
            Toast.makeText(context, "No App Available", Toast.LENGTH_SHORT).show();
            e.printStackTrace();
        }
    }


    @BindingAdapter("loadImage")
    public static void loadImage(ImageView v, String src) {
        Drawable loader = v.getContext().getResources().getDrawable(R.drawable.loading_animator);
        Glide.with(v.getContext()).load(src).into(v).onLoadStarted(loader);

    }

    @BindingAdapter("loadLocalImage")
    public static void loadLocalImage(ImageView v, String name) {
        File img = new File(App.VIDEO_DIR, name);
        if (img.exists()) Glide.with(v.getContext()).load(Uri.fromFile(img)).into(v);
        else App.L("image not exits");
    }
}
