package com.ma7moud3ly.fd.downloader;

import com.google.gson.Gson;
import com.google.gson.annotations.SerializedName;

public class Video {
    public String name;
    public String description;
    @SerializedName("contentSize")
    public String size;
    public String url;
    @SerializedName("thumbnailUrl")
    public String thumbnail;
    @SerializedName("videoQuality")
    public String quality;
    public String width;
    public String height;
    public String duration;

    public String toString() {
        return new Gson().toJson(this);
    }

    public static String serialize(Object obj) {
        return new Gson().toJson(obj);
    }

    public static Video deserialize(String str) {
        return new Gson().fromJson(str, Video.class);
    }


}