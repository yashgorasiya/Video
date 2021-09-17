package com.yjisolutions.video;

import static android.view.WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS;

import android.Manifest;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.appbar.MaterialToolbar;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.MultiplePermissionsReport;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.multi.MultiplePermissionsListener;
import com.yjisolutions.video.code.VAdapter;
import com.yjisolutions.video.code.VideoRead;

import java.util.List;

public class MainActivity extends AppCompatActivity {

    RecyclerView rv;
    MaterialToolbar toolbar;
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



    }

    private void doStuff() {
        VAdapter adapter = new VAdapter(VideoRead.getVideo(this),this);
        rv = findViewById(R.id.recView);
        rv.setLayoutManager(new LinearLayoutManager(this));
        rv.setAdapter(adapter);

        toolbar = findViewById(R.id.materialToolbar);

        rv.setOnFlingListener(new RecyclerView.OnFlingListener() {
            @Override
            public boolean onFling(int velocityX, int velocityY) {
                if (velocityY<0){
                    toolbar.setVisibility(View.VISIBLE);
                }else if (velocityY>0){
                    toolbar.setVisibility(View.GONE);
            }
                return false;
            }
        });
    }


}