package com.yjisolutions.video.code;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.IntentSender;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.net.Uri;
import android.os.Handler;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.AnimRes;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.card.MaterialCardView;
import com.google.android.material.snackbar.Snackbar;
import com.yjisolutions.video.R;
import com.yjisolutions.video.player;

import java.util.Collections;
import java.util.List;
import java.util.Objects;

public class VAdapter extends RecyclerView.Adapter<viewHolder> {

    List<Video> videos;
    Activity activity;
    boolean viewStyle;

    public VAdapter(List<Video> videos, Activity activity,boolean viewStyle) {
        this.videos = videos;
        this.activity = activity;
        this.viewStyle = viewStyle;
    }

    @NonNull
    @Override
    public viewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        int layout; if (viewStyle) layout = R.layout.preview; else layout = R.layout.compect_preview;
        return new viewHolder(LayoutInflater.from(parent.getContext()).inflate(layout, parent, false));
    }

    @SuppressLint("ClickableViewAccessibility")
    @Override
    public void onBindViewHolder(@NonNull viewHolder holder, int position) {
        Video video = videos.get(position);

        holder.title.setText(video.getName());
        holder.size.setText(sizeConversion.sizeConversion(video.getSize()));
        holder.duration.setText(sizeConversion.timerConversion(video.getDuration()));
        holder.seekBar.setClickable(false);
        holder.seekBar.setPadding(0, 0, 0, 0);

        Glide.with(activity.getBaseContext())
                .load(video.getUri())
                .override(200,160)
                .into(holder.thumb);

        // Playing videos in player Activity
        View view;
        if (viewStyle) view = holder.previewTile;
        else view = holder.thumb;
        view.setOnClickListener(v -> activity.startActivityForResult(
                new Intent(activity.getBaseContext(), player.class)
                        .putExtra("url", video.getUri().toString())
                        .putExtra("title", video.getName())
                , 1));


        holder.more.setOnClickListener(v -> showBottomSheetMore(video.getUri(), position));


        SharedPreferences sp = activity.getSharedPreferences("UserData", Context.MODE_PRIVATE);
        long lastPlayed = sp.getLong(video.getName(), 0);

        if (lastPlayed > 0) {
            holder.seekBar.setMax((int) video.getDuration());
            holder.seekBar.setProgress((int) lastPlayed);
        }

    }


    @Override
    public int getItemCount() {
        return videos.size();
    }

    @SuppressLint("NotifyDataSetChanged")
    public void update() {
        notifyDataSetChanged();
    }

    @SuppressLint("ShowToast")
    private void showBottomSheetMore(Uri uri, int position) {

        final BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(activity);
        bottomSheetDialog.setContentView(R.layout.bottom_sheet_dialog);

        LinearLayout share = bottomSheetDialog.findViewById(R.id.shareLinearLayout);
        LinearLayout delete = bottomSheetDialog.findViewById(R.id.delete);
        LinearLayout info = bottomSheetDialog.findViewById(R.id.videoInformation);

        // Click on Video Information
        Objects.requireNonNull(info).setOnClickListener(v -> {
            showBottomSheetMore(position);
            bottomSheetDialog.cancel();
        });

        // Click on Delete
        Objects.requireNonNull(delete).setOnClickListener(v -> {
            bottomSheetDialog.cancel();

            LayoutInflater factory = LayoutInflater.from(activity);
            final View deleteDialogView = factory.inflate(R.layout.delete_dialog, null);
            final AlertDialog deleteDialog = new AlertDialog.Builder(activity, R.style.Theme_Dialog).create();
            deleteDialog.setView(deleteDialogView);

            Video video = videos.get(position);

            Glide.with(activity)
                    .load(video.getUri())
                    .diskCacheStrategy(DiskCacheStrategy.NONE)
                    .into((ImageView) deleteDialogView.findViewById(R.id.thumbnailDelete));


            TextView deleteSize = deleteDialogView.findViewById(R.id.sizeDelete);
            TextView deleteTille = deleteDialogView.findViewById(R.id.DeleteDialogTitle);

            deleteSize.setText(sizeConversion.sizeConversion(video.getSize()));
            deleteTille.setText(video.getName());

            deleteDialogView.findViewById(R.id.DailogDeleteBtn).setOnClickListener(v13 -> {
                //your delete logic
                DeleteFile delete1 = new DeleteFile(activity);
                delete1.moveToBin(videos.get(position));
                videos.remove(videos.get(position));
                notifyItemRemoved(position);

                Snackbar.make(activity.findViewById(android.R.id.content), "Video will be deleted", Snackbar.LENGTH_LONG)
                        .setAction("Undo", v1 -> {
                            videos.add(position, delete1.getFromBin());
                            notifyItemInserted(position);
                        })
                        .setActionTextColor(Color.RED)
                        .show();


                deleteDialog.dismiss();
            });

            deleteDialogView.findViewById(R.id.DailogDeleteCancleTxt).setOnClickListener(v12 -> deleteDialog.dismiss());

            deleteDialog.show();

        });

        // Click on Share
        Objects.requireNonNull(share).setOnClickListener(v -> {
            activity.startActivity(
                    Intent.createChooser(
                            new Intent().setAction(Intent.ACTION_SEND)
                                    .setType("video/*")
                                    .setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                                    .putExtra(
                                            Intent.EXTRA_STREAM,
                                            uri
                                    ),
                            "Share Video"));
            bottomSheetDialog.cancel();
        });

        bottomSheetDialog.show();
    }


    // Shows Video Information
    @SuppressLint("SetTextI18n")
    private void showBottomSheetMore(int position) {

        final BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(activity);
        bottomSheetDialog.setContentView(R.layout.bottom_sheet_video_information);

        Video video = videos.get(position);

//        FFmpegMediaMetadataRetriever mmr = new FFmpegMediaMetadataRetriever();
//        mmr.setDataSource(mUri);
//        mmr.extractMetadata(FFmpegMediaMetadataRetriever.METADATA_KEY_ALBUM);
//        mmr.extractMetadata(FFmpegMediaMetadataRetriever.METADATA_KEY_ARTIST);
//
//        mmr.release();


        ImageView Preview = bottomSheetDialog.findViewById(R.id.VIPreview);
        TextView size = bottomSheetDialog.findViewById(R.id.VISize);
        TextView title = bottomSheetDialog.findViewById(R.id.VITitle);
        TextView duration = bottomSheetDialog.findViewById(R.id.VIDuration);
        TextView Width = bottomSheetDialog.findViewById(R.id.VIWidth);
        TextView Height = bottomSheetDialog.findViewById(R.id.VIHeight);

        Objects.requireNonNull(title).setText(video.getName());
        Objects.requireNonNull(size).setText(sizeConversion.sizeConversion(video.getSize()));
        Objects.requireNonNull(duration).setText(sizeConversion.timerConversion(video.getDuration()));

        Glide.with(activity)
                .load(video.getUri())
                .into(Objects.requireNonNull(Preview));

        bottomSheetDialog.show();

    }

}

// Handling File Deleting Task
//public class delete {
//    private static final int DELETE_REQUEST_CODE = 7;
//    private Video bin;
//    private final Activity activity;
//    private boolean deletecheck = true;
//
//    public delete(Activity activity) {
//        this.activity = activity;
//    }
//
//    void moveToBin(Video bin) {
//        this.bin = bin;
//        final Handler handler = new Handler();
//        handler.postDelayed(() -> {
//            // Do something after 5s = 5000ms
//            if (deletecheck) {
//                try {
//                    activity.getBaseContext().getContentResolver().delete(bin.getUri(), null, null);
//                } catch (SecurityException securityException) {
//
//                    List<Uri> urisToModify = Collections.singletonList(bin.getUri());
//
//                    PendingIntent editPendingIntent = null;
//
//                    if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.R) {
//                        editPendingIntent = MediaStore.createDeleteRequest(activity.getContentResolver(),
//                                urisToModify);
//                    }
//                    try {
//                        activity.startIntentSenderForResult(Objects.requireNonNull(editPendingIntent).getIntentSender(),
//                                DELETE_REQUEST_CODE, null, 0, 0, 0);
//                    } catch (IntentSender.SendIntentException e) {
//                        e.printStackTrace();
//                    }
//                }
//            } else {
//                Toast.makeText(activity, "Restored", Toast.LENGTH_SHORT).show();
//            }
//        }, 5000);
//    }
//
//    Video getFromBin() {
//        this.deletecheck = false;
//        return bin;
//    }
//
//}


class viewHolder extends RecyclerView.ViewHolder {

    ImageView thumb, more;
    TextView title, duration, size;
    ConstraintLayout previewTile;
    SeekBar seekBar;
//    MaterialCardView recViewItem;

    public viewHolder(@NonNull View itemView) {
        super(itemView);
        thumb = itemView.findViewById(R.id.thumbnail);
        more = itemView.findViewById(R.id.previewHomeMore);
        title = itemView.findViewById(R.id.titleV);
        duration = itemView.findViewById(R.id.durationV);
        size = itemView.findViewById(R.id.sizePreview);
        previewTile = itemView.findViewById(R.id.previewTileLayout);
        seekBar = itemView.findViewById(R.id.home_preview_seekbar);
//        recViewItem = itemView.findViewById(R.id.recViewItem);

    }
}