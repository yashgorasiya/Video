package com.yjisolutions.video.code;

import android.annotation.SuppressLint;

public class Conversion {

    @SuppressLint("DefaultLocale")
    public static String sizeConversion (long size){

        double KB = Double.parseDouble(String.valueOf(size/1024));
        double MB = KB/1024;
        double GB = MB/1024;

        if (GB>1){
            return String.format("%.2f",GB)+" "+"GB";
        }else if (MB>1){
            return  String.format("%.0f",MB)+" "+"MB";
        }else if (KB>1){
            return String.format("%.0f",KB)+" "+"KB";
        }
        return size+" "+"Bytes";
    }

   @SuppressLint("DefaultLocale")
    public static String sizeTotal (long size){

        double GB = Double.parseDouble(String.valueOf(size))/1024;

        if (GB>1) return String.format("%.2f",GB)+" "+"GB";

        return size+" "+"MB";
    }
 @SuppressLint("DefaultLocale")
    public static long sizeToMB (long size){
        long KB = size/1024;
     return Long.parseLong(String.valueOf(KB / 1024));
    }


    @SuppressLint("DefaultLocale")
    public static String timerConversion(long value) {
        String audioTime;
        int dur = (int) value;
        int hrs = (dur / 3600000);
        int mns = (dur / 60000) % 60000;
        int scs = dur % 60000 / 1000;

        if (hrs > 0) {
            mns = mns - hrs*60;
            audioTime = String.format("%02d:%02d:%02d", hrs, mns, scs);
        } else {
            audioTime = String.format("%02d:%02d", mns, scs);
        }
        return audioTime;
    }

}
