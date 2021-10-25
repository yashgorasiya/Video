package com.yjisolutions.video.code;

import android.content.ContentUris;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.provider.MediaStore;

import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

public class VideoRead {
    public static String[] order = {" DESC", " ASC"};
    public static String[] sortBy = {
            MediaStore.Video.Media.DATE_ADDED,
            MediaStore.Video.Media.DATE_MODIFIED,
            MediaStore.Video.Media.DISPLAY_NAME,
            MediaStore.Video.Media.DURATION,
            MediaStore.Video.Media.SIZE,
    };
    public static String[] sortByFolders = {
            MediaStore.Video.Media.DATE_ADDED,
            MediaStore.Video.Media.DATE_MODIFIED,
            MediaStore.Video.Media.BUCKET_DISPLAY_NAME,
            MediaStore.Video.Media.DURATION,
            MediaStore.Video.Media.SIZE,
    };

    public static ArrayList<Video> getVideoFromFolder(Context context, String folderName) {

        ArrayList<Video> videoList = new ArrayList<>();

        Uri collection;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            collection = MediaStore.Video.Media.getContentUri(MediaStore.VOLUME_EXTERNAL);
        } else {
            collection = MediaStore.Video.Media.EXTERNAL_CONTENT_URI;
        }

        String[] projection = new String[]{
                MediaStore.Video.Media._ID,
                MediaStore.Video.Media.DISPLAY_NAME,
                MediaStore.Video.Media.DURATION,
                MediaStore.Video.Media.SIZE,
                MediaStore.Video.Media.DATA
        };
        String selection = MediaStore.Video.Media.DATA + " like?";
        String[] selectionArgs = new String[]{"%" + folderName + "%"};
        String sortOrder = sortBy[Utils.SORT_BY_VIDEOS] + order[Utils.SORT_ORDER_VIDEOS];

        try (
                Cursor cursor = context.getContentResolver().query(
                        collection,
                        projection,
                        selection,
                        selectionArgs,
                        sortOrder
                )) {
            // Cache column indices.
            int idColumn = cursor.getColumnIndexOrThrow(MediaStore.Video.Media._ID);
            int nameColumn =
                    cursor.getColumnIndexOrThrow(MediaStore.Video.Media.DISPLAY_NAME);
            int durationColumn =
                    cursor.getColumnIndexOrThrow(MediaStore.Video.Media.DURATION);
            int sizeColumn = cursor.getColumnIndexOrThrow(MediaStore.Video.Media.SIZE);


            String FName1 = folderName.substring(folderName.lastIndexOf("/") + 1);
            if (FName1.equals("0")) {
                while (cursor.moveToNext()) {
                    // Get values of columns for a given video.
                    long id = cursor.getLong(idColumn);
                    String name = cursor.getString(nameColumn);
                    int duration = cursor.getInt(durationColumn);
                    long size = cursor.getLong(sizeColumn);

                    Uri contentUri = ContentUris.withAppendedId(
                            MediaStore.Video.Media.EXTERNAL_CONTENT_URI, id);

                    String path = cursor.getString(cursor.getColumnIndex(MediaStore.Video.Media.DATA));
                    int index = path.lastIndexOf("/");
                    String ss = path.substring(0, index);
                    String FName = ss.substring(ss.lastIndexOf("/") + 1);
                    if (FName.equals(FName1)) {
                        videoList.add(new Video(contentUri, name, duration, size));
                    }
                }
            } else {
                while (cursor.moveToNext()) {
                    // Get values of columns for a given video.
                    long id = cursor.getLong(idColumn);
                    String name = cursor.getString(nameColumn);
                    int duration = cursor.getInt(durationColumn);
                    long size = cursor.getLong(sizeColumn);

                    Uri contentUri = ContentUris.withAppendedId(
                            MediaStore.Video.Media.EXTERNAL_CONTENT_URI, id);

                    videoList.add(new Video(contentUri, name, duration, size));

                }
            }
        }
        return videoList;
    }

    public static int getNumOfFolder(Context context, String folderName) {

        int numOfVideosInFolder = 0;

        Uri collection;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            collection = MediaStore.Video.Media.getContentUri(MediaStore.VOLUME_EXTERNAL);
        } else {
            collection = MediaStore.Video.Media.EXTERNAL_CONTENT_URI;
        }

        String[] projection = new String[]{
                MediaStore.Video.Media._ID,
                MediaStore.Video.Media.DATA
        };
        String selection = MediaStore.Video.Media.DATA + " like?";
        String[] selectionArgs = new String[]{"%" + folderName + "%"};
        String sortOrder = sortBy[Utils.SORT_BY_VIDEOS] + order[Utils.SORT_ORDER_VIDEOS];

        try (
                Cursor cursor = context.getContentResolver().query(
                        collection,
                        projection,
                        selection,
                        selectionArgs,
                        null
                )) {
            // Cache column indices.
            int idColumn = cursor.getColumnIndexOrThrow(MediaStore.Video.Media._ID);


            String FName1 = folderName.substring(folderName.lastIndexOf("/") + 1);
            if (FName1.equals("0")) {
                while (cursor.moveToNext()) {
                    // Get values of columns for a given video
                    String path = cursor.getString(cursor.getColumnIndex(MediaStore.Video.Media.DATA));
                    int index = path.lastIndexOf("/");
                    String ss = path.substring(0, index);
                    String FName = ss.substring(ss.lastIndexOf("/") + 1);
                    if (FName.equals(FName1)) {
                        numOfVideosInFolder++;
                    }
                }
            } else {
                while (cursor.moveToNext()) {
                    // Get values of columns for a given video.
                    numOfVideosInFolder++;

                }
            }
        }
        return numOfVideosInFolder;
    }

    public static class Folder{
        public String getName() {
            return Name;
        }

        public int getCount() {
            return count;
        }

        private String Name;
        private int count;

        public Folder(String name, int count) {
            Name = name;
            this.count = count;
        }
    }
    public static ArrayList<Folder> getFolders(Context context) {

        ArrayList<String> folderList = new ArrayList<>();
        ArrayList<Folder> folderListWithCount = new ArrayList<>();

        Uri collection;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            collection = MediaStore.Video.Media.getContentUri(MediaStore.VOLUME_EXTERNAL);
        } else {
            collection = MediaStore.Video.Media.EXTERNAL_CONTENT_URI;
        }

        String[] projection = new String[]{
                MediaStore.Video.Media._ID,
                MediaStore.Video.Media.DURATION,
                MediaStore.Video.Media.BUCKET_DISPLAY_NAME,
                MediaStore.Video.Media.DATA
        };
        String selection = MediaStore.Video.Media.DURATION +
                " >= ?";
        String[] selectionArgs = new String[]{
                String.valueOf(TimeUnit.MILLISECONDS.convert(0, TimeUnit.MILLISECONDS))
        };
        String sortOrder = sortByFolders[Utils.SORT_BY] + order[Utils.SORT_ORDER];

        try (
                Cursor cursor = context.getContentResolver().query(
                        collection,
                        projection,
                        selection,
                        selectionArgs,
                        sortOrder
                )) {

            while (cursor.moveToNext()) {
                // Get values of columns for a given video.
                String path = cursor.getString(cursor.getColumnIndex(MediaStore.Video.Media.DATA));


                int index = path.lastIndexOf("/");
                String ss = path.substring(0, index);

                if (!folderList.contains(ss)) {
                    folderList.add(ss);
                    folderListWithCount.add(new Folder(ss,VideoRead.getNumOfFolder(context,ss)));
                }


            }
        }
        return folderListWithCount;
    }

    public static ArrayList<Video> getVideo(Context context) {

        ArrayList<Video> videoList = new ArrayList<>();

        Uri collection;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            collection = MediaStore.Video.Media.getContentUri(MediaStore.VOLUME_EXTERNAL);
        } else {
            collection = MediaStore.Video.Media.EXTERNAL_CONTENT_URI;
        }

        String[] projection = new String[]{
                MediaStore.Video.Media._ID,
                MediaStore.Video.Media.DISPLAY_NAME,
                MediaStore.Video.Media.DURATION,
                MediaStore.Video.Media.SIZE
        };
        String selection = MediaStore.Video.Media.DURATION +
                " >= ?";
        String[] selectionArgs = new String[]{
                String.valueOf(TimeUnit.MILLISECONDS.convert(0, TimeUnit.MILLISECONDS))
        };
        String sortOrder = MediaStore.Video.Media.DISPLAY_NAME + " ASC";

        try (
                Cursor cursor = context.getContentResolver().query(
                        collection,
                        projection,
                        selection,
                        selectionArgs,
                        sortOrder
                )) {
            // Cache column indices.
            int idColumn = cursor.getColumnIndexOrThrow(MediaStore.Video.Media._ID);
            int nameColumn =
                    cursor.getColumnIndexOrThrow(MediaStore.Video.Media.DISPLAY_NAME);
            int durationColumn =
                    cursor.getColumnIndexOrThrow(MediaStore.Video.Media.DURATION);
            int sizeColumn = cursor.getColumnIndexOrThrow(MediaStore.Video.Media.SIZE);

            while (cursor.moveToNext()) {
                // Get values of columns for a given video.
                long id = cursor.getLong(idColumn);
                String name = cursor.getString(nameColumn);
                int duration = cursor.getInt(durationColumn);
                long size = cursor.getLong(sizeColumn);
                Uri contentUri = ContentUris.withAppendedId(
                        MediaStore.Video.Media.EXTERNAL_CONTENT_URI, id);

                videoList.add(new Video(contentUri, name, duration, size));

            }
        }
        return videoList;
    }

}
