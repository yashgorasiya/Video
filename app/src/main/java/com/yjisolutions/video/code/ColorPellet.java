package com.yjisolutions.video.code;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.WallpaperManager;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;

import androidx.core.app.ActivityCompat;
import androidx.palette.graphics.Palette;

import com.yjisolutions.video.R;

public class ColorPellet {
    private Palette p;
    private int Light = 0;
    private int Dark = 0 ;
    private int LightMuted = 0;
    private int DarkMuted = 0;


    @SuppressLint("ResourceAsColor")
    public ColorPellet(Activity activity) {
        final WallpaperManager wallpaperManager = WallpaperManager.getInstance(activity);

        if (ActivityCompat.checkSelfPermission(activity, Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
            Drawable wallpaperDrawable = wallpaperManager.getDrawable();
            Bitmap bitmap = ((BitmapDrawable) wallpaperDrawable).getBitmap();
            p = Palette.from(bitmap).generate();
            Light = p.getLightVibrantColor(R.color.PrimaryLight);
            Dark = p.getDarkVibrantColor(R.color.PrimaryLight);
            LightMuted = p.getLightMutedColor(R.color.PrimaryLight);
            DarkMuted = p.getDarkMutedColor(R.color.PrimaryLight);
        }

    }


    public int getLight() {
        return Light;
    }
    public int getDark() {
        return Dark;
    }
    public int getLightMuted() {
        return LightMuted;
    }
    public int getDarkMuted() {
        return DarkMuted;
    }
}
