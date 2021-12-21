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


    public ColorPellet(Activity activity) {
        final WallpaperManager wallpaperManager = WallpaperManager.getInstance(activity);

        if (ActivityCompat.checkSelfPermission(activity, Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
            Drawable wallpaperDrawable = wallpaperManager.getDrawable();
            Bitmap bitmap = ((BitmapDrawable) wallpaperDrawable).getBitmap();
            p = Palette.from(bitmap).generate();
        }

    }


    @SuppressLint("ResourceAsColor")
    public int getLight() {
        return p.getLightVibrantColor(R.color.PrimaryLight);
    }
    @SuppressLint("ResourceAsColor")
    public int getDark() {
        return p.getDarkVibrantColor(R.color.PrimaryLight);
    }
    @SuppressLint("ResourceAsColor")
    public int getLightMuted() {
        return p.getLightMutedColor(R.color.PrimaryLight);
    }
    @SuppressLint("ResourceAsColor")
    public int getDarkMuted() {
        return p.getDarkMutedColor(R.color.PrimaryLight);
    }
}
