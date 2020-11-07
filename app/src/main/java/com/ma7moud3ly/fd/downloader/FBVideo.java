package com.ma7moud3ly.fd.downloader;

public class FBVideo {

    //check if provided link is real facebook video link
    public static boolean isFbVideo(String link) {
        return link.contains("facebook.com") &&
                (
                        link.contains("/videos/") ||
                                link.contains("/posts/") ||
                                link.contains("/groups/") ||
                                link.contains("?story_fbid=")
                );
    }

    //edit video link to open mobile website
    public static String editFbLink(String link) {
        link = link.replace("www.facebook", "m.facebook");
        return link;
    }

    public static String editVideoName(String name) {
        if (name == null) name = "";
        return name.replace("\n", "").
                replace("/", "").
                replace("\\", "").
                replace("|", "").
                replace(":", "").
                replace("*", "").
                replace("?", "").
                replace("<", "").
                replace(">", "").
                replace("#", "");

    }

    public static String getVideoJson(String src) {
        String t1 = "type=\"application/ld+json\"";
        String t2 = "</script>";
        int i = src.indexOf(t1);
        if (i == -1) return "";
        src = src.substring(i + t1.length());
        i = src.indexOf("{");
        src = src.substring(i);
        String json = src.substring(0, src.indexOf(t2));
        //json=json.replace("kB","");
        return json;
    }

/*
    public static Video getVideoFormSrc(String src) {
        String name = "", link = "";
        int i = src.indexOf("<title>");
        if (i != -1) {
            name = src.substring(i + 7, src.indexOf("</title>"));
        }
        String t = "href=\"/video_redirect/?src=";
        i = src.indexOf(t);
        if (i == -1) new Video("", "");
        src = src.substring(i + 6);
        link = src.substring(0, src.indexOf("target=") - 2);
        t = ".mp4";
        if (link.contains(t)) {
            i = link.indexOf(t) + t.length();
            name = link.substring(0, i);
            i = name.lastIndexOf("%") + 1;
            name = name.substring(i);
        }
        return new Video(name, link);
    }
*/

}

