package com.yjisolutions.video.code;

import android.net.Uri;

public class Video {

    private final Uri uri;
    private final String name;

    private final int duration;
    private final long size;

    public Video(Uri uri, String name, int duration, long size) {
        this.uri = uri;
        this.name = name;
        this.duration = duration;
        this.size = size;
    }

    public Uri getUri() {
        return uri;
    }

    public String getName() {
        return name;
    }

    public int getDuration() {
        return duration;
    }

    public long getSize() {
        return size;
    }

}
