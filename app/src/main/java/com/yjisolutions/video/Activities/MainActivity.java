package com.yjisolutions.video.Activities;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.yjisolutions.video.Interfaces.OnPlayerActivityDestroy;
import com.yjisolutions.video.R;

public class MainActivity extends AppCompatActivity {

    public static SharedPreferences sharedPreferences;
    private static OnPlayerActivityDestroy onPlayerActivityDestroy;
    public static void setOnPlayerActivityDestroyIF(OnPlayerActivityDestroy onPlayerActivityDestroy){
        MainActivity.onPlayerActivityDestroy = onPlayerActivityDestroy;
    }

    @SuppressLint("NotifyDataSetChanged")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        sharedPreferences = getSharedPreferences("UserData", Context.MODE_PRIVATE);

        if ((getResources().getConfiguration().uiMode & Configuration.UI_MODE_NIGHT_MASK) == Configuration.UI_MODE_NIGHT_NO) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
            }
            getWindow().setStatusBarColor(Color.TRANSPARENT);
        }



    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1) {
            onPlayerActivityDestroy.refreshVideoFragment();
        }
        // API 30+ Requesting for delete file
        if (requestCode == 7) {
            if (resultCode != MainActivity.RESULT_OK) {
                Toast.makeText(getApplicationContext(), "Permission Denied", Toast.LENGTH_SHORT).show();
            }
        }
    }


}