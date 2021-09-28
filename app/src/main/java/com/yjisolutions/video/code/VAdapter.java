package com.yjisolutions.video.code;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.media.MediaMetadataRetriever;
import android.net.Uri;
import android.os.Environment;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.card.MaterialCardView;
import com.google.android.material.snackbar.Snackbar;
import com.yjisolutions.video.R;
import com.yjisolutions.video.player;

import java.io.File;
import java.util.List;
import java.util.Objects;

public class VAdapter extends RecyclerView.Adapter<viewHolder> {

    List<Video> videos;
    Activity activity;

    public VAdapter(List<Video> videos, Activity activity) {
        this.videos = videos;
        this.activity = activity;
    }

    @NonNull
    @Override
    public viewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new viewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.preview, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull viewHolder holder, int position) {
        Video video = videos.get(position);

        holder.title.setText(video.getName());
        holder.size.setText(sizeConversion.sizeConversion(video.getSize()));
        holder.duration.setText(sizeConversion.timerConversion(video.getDuration()));
        holder.seekBar.setClickable(false);
        holder.seekBar.setPadding(0,0,0,0);
        Glide.with(activity.getBaseContext())
                .load(video.getUri())
                .into(holder.thumb);

        holder.previewTile.setOnClickListener(v -> {
            activity.startActivityForResult(
                    new Intent(activity.getBaseContext(),player.class)
                    .putExtra("url", video.getUri().toString())
                    .putExtra("title", video.getName())
                ,1);
        });
        holder.more.setOnClickListener(v -> showBottomSheetMore(video.getUri(),position));


        SharedPreferences sp =  activity.getSharedPreferences("UserData",Context.MODE_PRIVATE);
        long lastPlayed = sp.getLong(video.getName(), 0);

        if (lastPlayed>0){
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

        Objects.requireNonNull(info).setOnClickListener(v -> {showBottomSheetMore(position);
        bottomSheetDialog.cancel();});

        Objects.requireNonNull(delete).setOnClickListener(v -> {
            bottomSheetDialog.cancel();
            AlertDialog.Builder builder1 = new AlertDialog.Builder(activity);
            builder1.setMessage("Are you sure you want to delete");
            builder1.setCancelable(true);

            builder1.setPositiveButton(
                    "Yes",
                    (dialog, id) -> {
                        dialog.cancel();

                        com.yjisolutions.video.code.delete delete1 = new delete(activity);
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
                    });


            builder1.setNegativeButton(
                    "No",
                    (dialog, id) -> dialog.cancel());

            AlertDialog alert11 = builder1.create();
            alert11.show();

        });

        Objects.requireNonNull(share).setOnClickListener(v -> {activity.startActivity(
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


    @SuppressLint("SetTextI18n")
    private void showBottomSheetMore(int position){

        final BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(activity);
        bottomSheetDialog.setContentView(R.layout.bottom_sheet_video_information);

        Video video = videos.get(position);

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

class delete{
    private Video bin;
    private Activity activity;
    private boolean deletecheck = true;
    public delete(Activity activity){
        this.activity = activity;
    }

    void moveToBin(Video bin){
        this.bin = bin;
        final Handler handler = new Handler();
        handler.postDelayed(() -> {
            // Do something after 5s = 5000ms
            if (deletecheck) {
                int isdeleted = activity.getContentResolver().delete(bin.getUri(),null,null);
                if (isdeleted>0) {
                    Toast.makeText(activity, "Deleted", Toast.LENGTH_SHORT).show();
                }else {
                    Toast.makeText(activity,"Failed To Delete",Toast.LENGTH_SHORT).show();
                }
            }else {
                Toast.makeText(activity, "Restored", Toast.LENGTH_SHORT).show();
            }
        }, 5000);
    }
    Video getFromBin(){
        this.deletecheck = false;
        return bin;
    }

}


class viewHolder extends RecyclerView.ViewHolder {

    ImageView thumb,more;
    TextView title, duration, size;
    ConstraintLayout previewTile;
    SeekBar seekBar;

    public viewHolder(@NonNull View itemView) {
        super(itemView);
        thumb = itemView.findViewById(R.id.thumbnail);
        more = itemView.findViewById(R.id.previewHomeMore);
        title = itemView.findViewById(R.id.titleV);
        duration = itemView.findViewById(R.id.durationV);
        size = itemView.findViewById(R.id.sizePreview);
        previewTile = itemView.findViewById(R.id.previewTileLayout);
        seekBar = itemView.findViewById(R.id.home_preview_seekbar);

    }
}