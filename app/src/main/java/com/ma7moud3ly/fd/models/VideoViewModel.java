package com.ma7moud3ly.fd.models;

import android.app.DownloadManager;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageRequest;
import com.android.volley.toolbox.StringRequest;
import com.ma7moud3ly.fd.App;
import com.ma7moud3ly.fd.R;
import com.ma7moud3ly.fd.downloader.FBVideo;
import com.ma7moud3ly.fd.downloader.Video;
import com.ma7moud3ly.fd.network.VolleySingelton;
import com.ma7moud3ly.fd.util.MyFiles;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.io.FileOutputStream;

import androidx.databinding.BindingAdapter;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class VideoViewModel extends ViewModel {
    public MutableLiveData<Video> video = new MutableLiveData<>();
    public Boolean isDownloading = false;

    public void load(String url) {
        StringRequest request = new StringRequest(Request.Method.GET, url,
                src -> {
                    String json = FBVideo.getVideoJson(src);
                    Video v = Video.deserialize(json);
                    this.video.postValue(v);
                },
                error -> {
                    Log.i("HINT", error.getMessage());
                    this.video.postValue(null);
                });
        VolleySingelton.getInstance(App.getContext()).addToRequestQueue(request);
    }

    public void downloadVideo(Video video) {
        if (video == null) return;
        try {
            String url = video.url;
            String name = video.name;
            DownloadManager.Request r = new DownloadManager.Request(Uri.parse(url));
            r.setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, App.APP_NAME + "/" + name + ".mp4");
            r.allowScanningByMediaScanner();
            r.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
            DownloadManager dm = (DownloadManager) App.getContext().getSystemService(App.getContext().DOWNLOAD_SERVICE);
            dm.enqueue(r);
            App.toast("Downloading...");
        } catch (Exception e) {
            App.toast("Failed to download the video");
            e.printStackTrace();
        }
    }


    public void downloadThumbnail(Video video) {
        String url = video.thumbnail, name = video.name;
        ImageRequest request = new ImageRequest(url,
                bitmap -> {
                    Bitmap resized = Bitmap.createScaledBitmap(bitmap, (int) (bitmap.getWidth() * 0.4), (int) (bitmap.getHeight() * 0.4), true);
                    MyFiles.saveImage(resized, App.SAVE_PATH + "/.thumbnails/" + name);
                }, 0, 0, null,
                error -> {
                    App.L(error.getMessage());
                });
        VolleySingelton.getInstance(App.getContext()).addToRequestQueue(request);
    }
}
