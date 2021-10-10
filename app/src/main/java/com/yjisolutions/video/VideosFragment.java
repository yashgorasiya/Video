package com.yjisolutions.video;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
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
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.snackbar.Snackbar;
import com.yjisolutions.video.code.DeleteFile;
import com.yjisolutions.video.code.Video;
import com.yjisolutions.video.code.VideoRead;
import com.yjisolutions.video.code.sizeConversion;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;


public class VideosFragment extends Fragment {
    ArrayList<Video> videos = new ArrayList<>();


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        View v = inflater.inflate(R.layout.fragment_videos, container, false);
        if (getArguments() != null) {
            String folderName = getArguments().getString("folderName");
            videos = VideoRead.getVideoFromFolder(getContext(),folderName);
        }
        RecyclerView recyclerView = v.findViewById(R.id.recViewVideo);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        VFAdapter adapter = new VFAdapter(videos,getActivity(),true);
        recyclerView.setAdapter(adapter);
        return v;
    }
    public class VFAdapter extends RecyclerView.Adapter<viewHolderF> {

        List<Video> videos;
        Activity activity;
        boolean viewStyle;

        public VFAdapter(List<Video> videos, Activity activity,boolean viewStyle) {
            this.videos = videos;
            this.activity = activity;
            this.viewStyle = viewStyle;
        }

        @NonNull
        @Override
        public viewHolderF onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            int layout; if (viewStyle) layout = R.layout.preview; else layout = R.layout.compect_preview;
            return new viewHolderF(LayoutInflater.from(parent.getContext()).inflate(layout, parent, false));
        }

        @SuppressLint("ClickableViewAccessibility")
        @Override
        public void onBindViewHolder(@NonNull viewHolderF holder, int position) {
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



    static class viewHolderF extends RecyclerView.ViewHolder {

        ImageView thumb, more;
        TextView title, duration, size;
        ConstraintLayout previewTile;
        SeekBar seekBar;

        public viewHolderF(@NonNull View itemView) {
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
}