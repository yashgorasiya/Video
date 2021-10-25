package com.yjisolutions.video.Adapters;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.media.MediaMetadataRetriever;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.snackbar.Snackbar;
import com.yjisolutions.video.Activities.PlayerActivity;
import com.yjisolutions.video.R;
import com.yjisolutions.video.code.Conversion;
import com.yjisolutions.video.code.DeleteFile;
import com.yjisolutions.video.code.Utils;
import com.yjisolutions.video.code.Video;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class VideoAdapter extends RecyclerView.Adapter<VideoViewHolder> {

    List<Video> videos;
    Activity activity;
    boolean viewStyle;

    public VideoAdapter(List<Video> videos, Activity activity, boolean viewStyle) {
        this.videos = videos;
        this.activity = activity;
        this.viewStyle = viewStyle;
    }

    @NonNull
    @Override
    public VideoViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        int layout;
        if (viewStyle) layout = R.layout.preview;
        else layout = R.layout.compect_preview;
        return new VideoViewHolder(LayoutInflater.from(parent.getContext()).inflate(layout, parent, false));
    }

    @SuppressLint("ClickableViewAccessibility")
    @Override
    public void onBindViewHolder(@NonNull VideoViewHolder holder, int position) {
        Video video = videos.get(position);

        holder.title.setText(video.getName());
        holder.size.setText(Conversion.sizeConversion(video.getSize()));
        holder.duration.setText(Conversion.timerConversion(video.getDuration()));
        holder.seekBar.setClickable(false);
        holder.seekBar.setPadding(0, 0, 0, 0);

        Glide.with(activity.getBaseContext())
                .load(video.getUri())
                .override(250, 200)
                .into(holder.thumb);

        // Playing videos in PlayerActivity Activity
        View view;
        if (viewStyle) view = holder.previewTile;
        else view = holder.thumb;



        view.setOnClickListener(v -> activity.startActivityForResult(
                new Intent(activity.getBaseContext(), PlayerActivity.class)
                        .putExtra("url", video.getUri().toString())
                        .putExtra("title", video.getName())
                , 1));


        holder.more.setOnClickListener(v -> showBottomSheetMore(video.getUri(), position));


        long lastPlayed = Utils.sp.getLong(video.getName(), 0);
        if (lastPlayed > 0) {
            holder.seekBar.setMax(video.getDuration());
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

        final BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(activity,R.style.BottomSheetCustom);
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
            SeekBar seekBar = deleteDialogView.findViewById(R.id.delete_preview_seekbar);

            seekBar.setPadding(0,0,0,0);
            seekBar.setMax(video.getDuration());
            seekBar.setProgress(Integer.parseInt(String.valueOf(Utils.sp.getLong(video.getName(),0))));

            deleteSize.setText(Conversion.sizeConversion(video.getSize()));
            deleteTille.setText(video.getName());

            deleteDialogView.findViewById(R.id.DialogDeleteBtn).setOnClickListener(v13 -> {
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

            deleteDialogView.findViewById(R.id.DialogDeleteCancelTxt).setOnClickListener(v12 -> deleteDialog.dismiss());

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

        final BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(activity,R.style.BottomSheetCustom);
        bottomSheetDialog.setContentView(R.layout.bottom_sheet_video_information);

        Video video = videos.get(position);
        String w="0",h="0";
        long l=0;

        try (MediaMetadataRetriever mmr = new MediaMetadataRetriever()) {
            mmr.setDataSource(activity, video.getUri());
            w = mmr.extractMetadata(MediaMetadataRetriever.METADATA_KEY_VIDEO_WIDTH);
            h = mmr.extractMetadata(MediaMetadataRetriever.METADATA_KEY_VIDEO_HEIGHT);
            l = Long.parseLong(mmr.extractMetadata(MediaMetadataRetriever.METADATA_KEY_BITRATE));
            mmr.release();
        }


        ImageView Preview = bottomSheetDialog.findViewById(R.id.VIPreview);
        TextView size = bottomSheetDialog.findViewById(R.id.VISize);
        TextView title = bottomSheetDialog.findViewById(R.id.VITitle);
        TextView duration = bottomSheetDialog.findViewById(R.id.VIDuration);
        TextView Width = bottomSheetDialog.findViewById(R.id.VIWidth);
        TextView Height = bottomSheetDialog.findViewById(R.id.VIHeight);
        TextView location = bottomSheetDialog.findViewById(R.id.VILocation);
        Objects.requireNonNull(Width).setText(w);
        Objects.requireNonNull(Height).setText(h);
        Objects.requireNonNull(location).setText(Conversion.sizeConversion(l)+"ps");

        Objects.requireNonNull(title).setText(video.getName());
        Objects.requireNonNull(size).setText(Conversion.sizeConversion(video.getSize()));
        Objects.requireNonNull(duration).setText(Conversion.timerConversion(video.getDuration()));

        Glide.with(activity)
                .load(video.getUri())
                .diskCacheStrategy(DiskCacheStrategy.NONE)
                .into(Objects.requireNonNull(Preview));

        bottomSheetDialog.show();

    }

    @SuppressLint("NotifyDataSetChanged")
    public void updateList(ArrayList<Video> temp) {
        videos = temp;
        notifyDataSetChanged();
    }
}