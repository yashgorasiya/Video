package com.yjisolutions.video.Modal;

public class Folder{
    public String getName() {
        return Name;
    }

    public int getCount() {
        return count;
    }

    private final String Name;
    private final int count;

    public Folder(String name, int count) {
        Name = name;
        this.count = count;
    }
}