package com.yjisolutions.video.code;

import android.annotation.SuppressLint;

public class sizeConversion {


    @SuppressLint("DefaultLocale")
    public static String sizeConversion (int sizel){

        float KB = sizel/1024;
        float MB = KB/1024;
        float GB = MB/1024;

        if (GB>1){
            return String.format("%.1f",GB)+" "+"GB";
        }else if (MB>1){
            return  String.format("%.0f",MB)+" "+"MB";
        }else if (KB>1){
            return String.format("%.0f",KB)+" "+"KB";
        }
        return sizel+" "+"Bytes";
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
