package com.yjisolutions.video.Activities;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import com.google.android.material.navigation.NavigationBarView;
import com.yjisolutions.video.Fragments.FolderFragment;
import com.yjisolutions.video.Fragments.VideosFragment;
import com.yjisolutions.video.Interfaces.OnPlayerActivityDestroy;
import com.yjisolutions.video.R;

public class MainActivity extends AppCompatActivity {

    public static SharedPreferences sharedPreferences;
    private static OnPlayerActivityDestroy onPlayerActivityDestroy;
    public int oldConfig;

    public static void setOnPlayerActivityDestroyIF(OnPlayerActivityDestroy onPlayerActivityDestroy) {
        MainActivity.onPlayerActivityDestroy = onPlayerActivityDestroy;
    }

    @SuppressLint("NotifyDataSetChanged")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        oldConfig = getResources().getConfiguration().uiMode;
        sharedPreferences = getSharedPreferences("UserData", Context.MODE_PRIVATE);

        switchToFragment1(new FolderFragment());

        NavigationBarView bottomNavigationView = findViewById(R.id.bottom_navigation);
        bottomNavigationView.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener() {
            @SuppressLint("NonConstantResourceId")
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.folders:
                        //do something
                        switchToFragment1(new FolderFragment());
                        return true;
                    case R.id.videos:
                        //do something
                        switchToFragment1(new VideosFragment());
                        return true;
                    case R.id.feedback:
                        //do something
                        return true;
                }
                bottomNavigationView.setSelectedItemId(item.getItemId());
                return false;
            }
        });
        bottomNavigationView.animate().translationY(200).setDuration(0);
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                bottomNavigationView.animate().translationY(0).setDuration(500);
            }
        }, 500);


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

    public void switchToFragment1(Fragment fragment) {
        FragmentManager manager = getSupportFragmentManager();
        manager.beginTransaction().replace(R.id.homeScreenFrameLayout, fragment).commit();
    }


    @Override
    public void onConfigurationChanged(@NonNull Configuration newConfig) {
        super.onConfigurationChanged(newConfig);

        int currentNightMode = newConfig.uiMode & Configuration.UI_MODE_NIGHT_MASK;

        if (oldConfig != currentNightMode) {
            this.recreate();
            oldConfig=currentNightMode;
        }


    }


}