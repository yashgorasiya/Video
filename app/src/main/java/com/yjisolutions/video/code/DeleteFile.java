package com.yjisolutions.video.code;

import android.app.Activity;
import android.app.PendingIntent;
import android.content.IntentSender;
import android.net.Uri;
import android.provider.MediaStore;

import com.yjisolutions.video.Modal.Video;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class DeleteFile {
    private static final int DELETE_REQUEST_CODE = 7;
    private final Activity activity;

    public DeleteFile(Activity activity) {
        this.activity = activity;
    }

    public void moveToBin(ArrayList<Video> bin) {
        try {
            for (Video v : bin) {
                activity.getBaseContext().getContentResolver().delete(v.getUri(), null, null);
            }

        } catch (SecurityException securityException) {
            List<Uri> urisToModify = new ArrayList<>();
            for (Video video : bin) {
                urisToModify.add(video.getUri());
            }
            PendingIntent editPendingIntent = null;

            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.R) {
                editPendingIntent = MediaStore.createDeleteRequest(activity.getContentResolver(),
                        urisToModify);
            }
            try {
                activity.startIntentSenderForResult(Objects.requireNonNull(editPendingIntent).getIntentSender(),
                        DELETE_REQUEST_CODE, null, 0, 0, 0);
            } catch (IntentSender.SendIntentException e) {
                e.printStackTrace();
            }
        }
    }

}
