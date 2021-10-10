package com.yjisolutions.video;

import static android.view.WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.database.Cursor;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.loader.content.Loader;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.jiajunhui.xapp.medialoader.MediaLoader;
import com.jiajunhui.xapp.medialoader.bean.VideoFolder;
import com.jiajunhui.xapp.medialoader.bean.VideoResult;
import com.jiajunhui.xapp.medialoader.callback.OnVideoLoaderCallBack;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.MultiplePermissionsReport;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.multi.MultiplePermissionsListener;
import com.yjisolutions.video.code.VAdapter;
import com.yjisolutions.video.code.VideoRead;

import java.util.List;

public class MainActivity extends AppCompatActivity {

    private RecyclerView rv;
    private Toolbar toolbar;
    private VAdapter adapter;
    SharedPreferences sp;
    boolean viewStyle;

    @SuppressLint("NotifyDataSetChanged")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        getWindow().addFlags(FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);

        switch (getResources().getConfiguration().uiMode & Configuration.UI_MODE_NIGHT_MASK) {
            case Configuration.UI_MODE_NIGHT_YES:
                getWindow().setStatusBarColor(Color.BLACK);
                break;
            case Configuration.UI_MODE_NIGHT_NO:
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
                }
                getWindow().setStatusBarColor(Color.TRANSPARENT);
                break;
        }



        Dexter.withContext(getApplicationContext())
                .withPermissions(
                        Manifest.permission.READ_EXTERNAL_STORAGE,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE
                ).withListener(new MultiplePermissionsListener() {
            @Override
            public void onPermissionsChecked(MultiplePermissionsReport multiplePermissionsReport) {

                if (multiplePermissionsReport.areAllPermissionsGranted()) {
                    doStuff();
                } else {
                    AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                    builder.setTitle("Alert");
                    builder.setMessage("This app needs storage permission");
                    builder.setPositiveButton("GOTO SETTINGS", (dialog, which) -> {
                        dialog.cancel();
                        Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                        Uri uri = Uri.fromParts("package", getPackageName(), null);
                        intent.setData(uri);
                        startActivityIfNeeded(intent, 101);
                    });
                    builder.setNegativeButton("Cancel", (dialog, which) -> {
                        dialog.cancel();
                        MainActivity.this.finish();
                    });
                    builder.show();
                }
            }

            @Override
            public void onPermissionRationaleShouldBeShown(List<PermissionRequest> list, PermissionToken permissionToken) {
                permissionToken.continuePermissionRequest();
            }
        }).withErrorListener(error -> Toast.makeText(getApplicationContext(), "Error occurred! ", Toast.LENGTH_SHORT).show())
                .onSameThread()
                .check();


        // temp code
        ImageView imageView = findViewById(R.id.homeScreenMore);
        imageView.setOnClickListener(v -> {
            @SuppressLint("CommitPrefEdits") SharedPreferences.Editor spe = sp.edit();


            spe.putBoolean("homeScreenLayoutType", !viewStyle);
            if (!spe.commit())
                Toast.makeText(getApplicationContext(), "Failed to Save", Toast.LENGTH_SHORT).show();
            viewStyle = sp.getBoolean("homeScreenLayoutType",true);
            applySetting();
        });


    }

    void applySetting(){
        startActivity(new Intent(this,MainActivity.class));
        finish();
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1) {
            adapter.update();
        }
        // API 30+
        // Requesting for delete file
        if (requestCode == 7) {
            if (resultCode != MainActivity.RESULT_OK) {
                Toast.makeText(getApplicationContext(), "Permission Denied", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void doStuff() {
        sp = getSharedPreferences("UserData", Context.MODE_PRIVATE);
        viewStyle = sp.getBoolean("homeScreenLayoutType",true);

        adapter = new VAdapter(VideoRead.getVideo(this), MainActivity.this,viewStyle);
        rv = findViewById(R.id.recView);

        if (viewStyle) setRVGrid(1);
        else setRVGrid(2);

        rv.setAdapter(adapter);

        toolbar = findViewById(R.id.materialToolbar);

        rv.setHasFixedSize(true);
        rv.setOnFlingListener(new RecyclerView.OnFlingListener() {
            @Override
            public boolean onFling(int velocityX, int velocityY) {
                if (velocityY < 0) {
                    toolbar.setVisibility(View.VISIBLE);
                } else if (velocityY > 0) {
                    toolbar.setVisibility(View.GONE);
                }
                return false;
            }
        });
    }



    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    public void onConfigurationChanged(@NonNull Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        if (newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE) {
            if (viewStyle) setRVGrid(2);
            else setRVGrid(4);
        } else if (newConfig.orientation == Configuration.ORIENTATION_PORTRAIT) {
            if (viewStyle) setRVGrid(1);
            else setRVGrid(2);
        }
    }


    void setRVGrid(int grid){
        rv.setLayoutManager(new GridLayoutManager(this, grid));
    }
}