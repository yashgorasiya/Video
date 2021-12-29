package com.yjisolutions.video.code;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.provider.Settings;

import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.MultiplePermissionsReport;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.multi.MultiplePermissionsListener;
import com.yjisolutions.video.Interfaces.OnPermissionGranted;

import java.util.List;

public class Permissions {

    public static void request(Activity context, OnPermissionGranted onPermissionGranted) {
        Dexter.withContext(context)
                .withPermissions(
                        Manifest.permission.READ_EXTERNAL_STORAGE,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE
                ).withListener(new MultiplePermissionsListener() {
            @Override
            public void onPermissionsChecked(MultiplePermissionsReport multiplePermissionsReport) {

                if (multiplePermissionsReport.areAllPermissionsGranted()) {
                    onPermissionGranted.onGranted();
                } else {
                    requestDialog(context, onPermissionGranted);
                }

            }

            @Override
            public void onPermissionRationaleShouldBeShown(List<PermissionRequest> list, PermissionToken
                    permissionToken) {
                permissionToken.continuePermissionRequest();
            }
        }).withErrorListener(error -> requestDialog(context, onPermissionGranted))
                .onSameThread()
                .check();

    }

    static void requestDialog(Activity context, OnPermissionGranted onPermissionGranted) {
        MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(context);

        builder.setTitle("Alert");
        builder.setMessage("This app needs storage permission to play videos");

        builder.setPositiveButton("Retry", (dialog, which) -> {
            request(context, onPermissionGranted);
        });

        builder.setNegativeButton("Setting", (dialog, which) -> {
            dialog.cancel();
            Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
            Uri uri = Uri.fromParts("package", context.getPackageName(), null);
            intent.setData(uri);
            context.startActivityIfNeeded(intent, 101);
        });

        builder.setNeutralButton("Close", (dialog, which) -> {
            dialog.cancel();
            context.finish();
        });

        builder.show();
    }
}
