package com.yjisolutions.video.code;


import android.content.SharedPreferences;

import com.yjisolutions.video.Activities.MainActivity;

public class Utils {

    public static SharedPreferences sp = MainActivity.sharedPreferences;
    public static SharedPreferences.Editor spe = sp.edit();

    public static int VIEW_STYLE = sp.getInt("previewStyle", 0);
    public static int SORT_BY = sp.getInt("sortBy", 0);
    public static int SORT_ORDER = sp.getInt("sortOrder",0);
    public static int SORT_BY_VIDEOS = sp.getInt("sortByVideos",0);
    public static int SORT_ORDER_VIDEOS = sp.getInt("sortOrderVideos",0);

    public static boolean changeVideoTileStyle() {
        if (VIEW_STYLE == 0) spe.putInt("previewStyle", 1);
        else if (VIEW_STYLE == 1) spe.putInt("previewStyle", 2);
        else if (VIEW_STYLE == 2) spe.putInt("previewStyle", 0);
        boolean commit = spe.commit();
        VIEW_STYLE = sp.getInt("previewStyle", 0);
        return commit;
    }

    public static final int NAME = 2;
    public static final int DATE_MODIFIED = 1;
    public static final int DATE_ADDED = 0;
    public static final int SIZE = 4;
    public static final int DURATION = 3;
    public static final boolean FOLDERS = true;
    public static final boolean VIDEOS = false;

    public static void SortBy(int type,boolean folderOrVideos){
        if (folderOrVideos) {
            spe.putInt("sortBy", type);
            spe.commit();
            SORT_BY = sp.getInt("sortBy", 0);
        }else{
            spe.putInt("sortByVideos", type);
            spe.commit();
            SORT_BY_VIDEOS = sp.getInt("sortByVideos", 0);
        }
    }

    public static final int ASCENDING = 1;
    public static final int DESCENDING = 0;

    public static void setSortOrder(int order,boolean folderOrVideos){
        if (folderOrVideos) {
            spe.putInt("sortOrder", order);
            spe.commit();
            SORT_ORDER = sp.getInt("sortOrder", 0);
        }else{
            spe.putInt("sortOrderVideos", order);
            spe.commit();
            SORT_ORDER_VIDEOS = sp.getInt("sortOrderVideos", 0);
        }
    }

    public static int RECENTLY_PLAYED_VIDEO_POSITION = sp.getInt("recentVideoPosition", 0);
    public static String RECENTLY_PLAYED_VIDEO_FOLDER = sp.getString("recentVideoFolder", "0");

    public static boolean setRecentlyPlayed(int videoPosition,String videoFolder,String videoTitle,long duration){
        spe.putInt("recentVideoPosition", videoPosition);
        spe.putString("recentVideoFolder", videoFolder);
        spe.putLong(videoTitle,duration);
        boolean commit = spe.commit();
        RECENTLY_PLAYED_VIDEO_POSITION = sp.getInt("recentVideoPosition", 0);
        RECENTLY_PLAYED_VIDEO_FOLDER = sp.getString("recentVideoFolder", "0");
        return commit;
    }

}
