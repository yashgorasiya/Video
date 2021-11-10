package com.yjisolutions.video.code;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;

import java.util.ArrayList;

public class Share {
    public static void videos(ArrayList<Uri> uris, Activity context){
            context.startActivity(
                    Intent.createChooser(
                            new Intent().setAction(Intent.ACTION_SEND)
                                    .setType("video/*")
                                    .setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                                    .putParcelableArrayListExtra(
                                            Intent.EXTRA_STREAM,
                                            uris
                                    ),
                            "Share Video"));
    }
}
