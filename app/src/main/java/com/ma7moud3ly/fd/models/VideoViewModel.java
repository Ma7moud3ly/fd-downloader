package com.ma7moud3ly.fd.models;

import android.util.Log;

import com.android.volley.Request;
import com.android.volley.toolbox.StringRequest;
import com.ma7moud3ly.fd.App;
import com.ma7moud3ly.fd.downloader.FBVideo;
import com.ma7moud3ly.fd.downloader.Video;
import com.ma7moud3ly.fd.network.VolleySingelton;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class VideoViewModel extends ViewModel {
    public MutableLiveData<Video> video = new MutableLiveData<>();
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

}
