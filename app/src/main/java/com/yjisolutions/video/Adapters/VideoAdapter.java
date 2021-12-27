package com.yjisolutions.video.Adapters;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.media.MediaMetadataRetriever;
import android.net.Uri;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.yjisolutions.video.Activities.PlayerActivity;
import com.yjisolutions.video.Fragments.VideosFragment;
import com.yjisolutions.video.Modal.Video;
import com.yjisolutions.video.R;
import com.yjisolutions.video.code.Conversion;
import com.yjisolutions.video.code.DeleteFile;
import com.yjisolutions.video.code.Share;
import com.yjisolutions.video.code.Utils;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class VideoAdapter extends RecyclerView.Adapter<VideoViewHolder> {

    List<Video> videos;
    Activity activity;
    boolean viewStyle;
    private boolean isSelected = false;
    private boolean firstLongPress = false;
    ArrayList<Video> selectedItems;
    private final View view = VideosFragment.parentView;

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
    public void onBindViewHolder(@NonNull VideoViewHolder holder, @SuppressLint("RecyclerView") int position) {
        Video video = videos.get(position);

        holder.title.setText(video.getName());
        holder.size.setText(Conversion.sizeConversion(video.getSize()));
        holder.duration.setText(Conversion.timerConversion(video.getDuration()));
        holder.seekBar.setClickable(false);
        holder.seekBar.setPadding(0, 0, 0, 0);

        // Playing videos in PlayerActivity Activity
        View view;
        if (viewStyle) view = holder.previewTile;
        else view = holder.thumb;


        view.setOnLongClickListener(v -> {
            selectedItems = new ArrayList<>();

            isSelected = true;
            firstLongPress = true;

            selectedItems.add(videos.get(position));
            holder.selectedIndicator.setVisibility(View.VISIBLE);
            setSelectedCount(selectedItems.size());

            changeToolbar();
            return false;
        });


        if (isSelected) {
            if (Objects.requireNonNull(selectedItems).contains(videos.get(position)))
                holder.selectedIndicator.setVisibility(View.VISIBLE);
            else holder.selectedIndicator.setVisibility(View.GONE);
        } else holder.selectedIndicator.setVisibility(View.GONE);

        view.setOnClickListener(v -> {
                    if (isSelected) {
                        if (firstLongPress) firstLongPress = false;
                        else {
                            if (Objects.requireNonNull(selectedItems).contains(videos.get(position))) {
                                selectedItems.remove(videos.get(position));
                                holder.selectedIndicator.setVisibility(View.GONE);
                                if (selectedItems.size() == 0) {
                                    isSelected = false;
                                    changeToolbar();
                                }
                            } else {
                                selectedItems.add(videos.get(position));
                                holder.selectedIndicator.setVisibility(View.VISIBLE);
                            }
                            setSelectedCount(selectedItems.size());
                        }
                    } else {
                        activity.startActivityForResult(
                                new Intent(activity.getBaseContext(), PlayerActivity.class)
                                        .putExtra("position", position)
                                , 1);
                    }
                }
        );


        holder.more.setOnClickListener(v -> {
            if (!isSelected) showBottomSheetMore(video.getUri(), position);
        });


        long lastPlayed = Utils.sp.getLong(video.getName(), 0);
        if (lastPlayed > 0) {
            holder.seekBar.setMax(video.getDuration());
            holder.seekBar.setProgress((int) lastPlayed);
        }

        Glide.with(activity.getBaseContext())
                .load(video.getUri())
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .override(250, 200)
                .into(holder.thumb);

    }


    @Override
    public int getItemCount() {
        return videos.size();
    }

    @SuppressLint("SetTextI18n")
    public void setSelectedCount(int num) {
        TextView textView = view.findViewById(R.id.videoFragmentNumOfSelected);
        textView.setText(num + " Selected");
    }

    public void changeToolbar() {
        ConstraintLayout defaultToolbar = view.findViewById(R.id.conLayoutToolbarVideos);
        ConstraintLayout selectionToolbar = view.findViewById(R.id.conLayoutToolbarVideosSelection);
        if (isSelected) {
            defaultToolbar.setVisibility(View.GONE);
            selectionToolbar.setVisibility(View.VISIBLE);
            setSelectionLis();
        } else {
            defaultToolbar.setVisibility(View.VISIBLE);
            selectionToolbar.setVisibility(View.GONE);
        }
    }

    @SuppressLint({"NotifyDataSetChanged", "UseCompatLoadingForDrawables"})
    public void setSelectionLis() {

        ImageView delete, selectAll, share, back;

        delete = view.findViewById(R.id.videoFragmentDelete);
        selectAll = view.findViewById(R.id.videoFragmentSelectAll);
        share = view.findViewById(R.id.videoFragmentShare);
        back = view.findViewById(R.id.videoFragmentExitSelectionMode);

        delete.setOnClickListener(v -> CustomDeleteDialog(selectedItems));

        selectAll.setOnClickListener(v -> {

            if (selectedItems.size() == videos.size()) {
                selectAll.setImageDrawable(activity.getDrawable(R.drawable.ic_baseline_select_all_24));
                selectedItems = new ArrayList<>();
                isSelected = false;
                changeToolbar();
            } else {
                for (Video video : videos) {
                    if (!selectedItems.contains(video)) selectedItems.add(video);
                }
                setSelectedCount(selectedItems.size());
                selectAll.setImageDrawable(activity.getDrawable(R.drawable.ic_baseline_deselect_all_24));
            }
            notifyDataSetChanged();

        });

        share.setOnClickListener(v -> {
            ArrayList<Uri> uris = new ArrayList<>();
            for (Video video : selectedItems) {
                uris.add(video.getUri());
            }
            Share.videos(uris, activity);
        });

        back.setOnClickListener(v -> {
            selectedItems = new ArrayList<>();
            isSelected = false;
            changeToolbar();
            notifyDataSetChanged();
        });
    }

    @SuppressLint("NotifyDataSetChanged")
    public void update() {
        notifyDataSetChanged();
    }

    @SuppressLint("ShowToast")
    private void showBottomSheetMore(Uri uri, int position) {

        final BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(activity, R.style.BottomSheetCustom);
        bottomSheetDialog.setContentView(R.layout.bottom_sheet_dialog);

        LinearLayout share = bottomSheetDialog.findViewById(R.id.shareLinearLayout);
        LinearLayout delete = bottomSheetDialog.findViewById(R.id.delete);
        LinearLayout info = bottomSheetDialog.findViewById(R.id.videoInformation);

        // Click on Video Information
        Objects.requireNonNull(info).setOnClickListener(v -> {
            showBottomSheetMore(position);
            bottomSheetDialog.cancel();
        });


        // Click on Share
        Objects.requireNonNull(share).setOnClickListener(v -> {
            ArrayList<Uri> uris = new ArrayList<>();
            uris.add(uri);
            Share.videos(uris, activity);
            bottomSheetDialog.cancel();
        });

        // Click on Delete
        Objects.requireNonNull(delete).setOnClickListener(v -> {
            bottomSheetDialog.cancel();
            ArrayList<Video> toDelete = new ArrayList<>();
            toDelete.add(videos.get(position));
            CustomDeleteDialog(toDelete);
        });
        bottomSheetDialog.show();

    }

    @SuppressLint("NotifyDataSetChanged")
    private void CustomDeleteDialog(ArrayList<Video> toDelete) {

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.R) {
            DeleteFile delete1 = new DeleteFile(activity);
            delete1.moveToBin(toDelete);
            videos.removeAll(toDelete);
            selectedItems = new ArrayList<>();
            isSelected = false;
            changeToolbar();
            notifyDataSetChanged();
        } else {

            LayoutInflater factory = LayoutInflater.from(activity);
            final View deleteDialogView = factory.inflate(R.layout.delete_dialog, null);
            final AlertDialog deleteDialog = new AlertDialog.Builder(activity, R.style.Theme_Dialog).create();
            deleteDialog.setView(deleteDialogView);
            deleteDialogView.setScaleX(-0.1f);
            deleteDialogView.setScaleY(-0.1f);

            Video video = toDelete.get(0);

            Glide.with(activity)
                    .load(video.getUri())
                    .diskCacheStrategy(DiskCacheStrategy.NONE)
                    .into((ImageView) deleteDialogView.findViewById(R.id.thumbnailDelete));

            TextView deleteSize = deleteDialogView.findViewById(R.id.sizeDelete);
            TextView deleteTille = deleteDialogView.findViewById(R.id.DeleteDialogTitle);
            SeekBar seekBar = deleteDialogView.findViewById(R.id.delete_preview_seekbar);

            seekBar.setPadding(0, 0, 0, 0);
            seekBar.setMax(video.getDuration());
            seekBar.setProgress(Integer.parseInt(String.valueOf(Utils.sp.getLong(video.getName(), 0))));

            deleteSize.setText(Conversion.sizeConversion(video.getSize()));
            deleteTille.setText(video.getName());

            deleteDialogView.findViewById(R.id.DialogDeleteBtn).setOnClickListener(v13 -> {
                //your delete logic
                DeleteFile delete1 = new DeleteFile(activity);
                delete1.moveToBin(toDelete);
                videos.removeAll(toDelete);
                selectedItems = new ArrayList<>();
                isSelected = false;
                changeToolbar();
                notifyDataSetChanged();
                deleteDialogView
                        .animate()
                        .scaleXBy(-1f)
                        .scaleYBy(-1f)
                        .setDuration(200);
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        deleteDialog.dismiss();
                    }
                }, 200);
            });

            deleteDialogView.findViewById(R.id.DialogDeleteCancelTxt).setOnClickListener(v12 -> {

                deleteDialogView
                        .animate()
                        .scaleXBy(-1f)
                        .scaleYBy(-1f)
                        .setDuration(200);
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        deleteDialog.dismiss();
                    }
                }, 200);
            });

            deleteDialog.show();
            deleteDialogView
                    .animate()
                    .scaleXBy(1.1f)
                    .scaleYBy(1.1f)
                    .setDuration(200);
        }

    }

    // Shows Video Information
    @SuppressLint("SetTextI18n")
    private void showBottomSheetMore(int position) {

        final BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(activity, R.style.BottomSheetCustom);
        bottomSheetDialog.setContentView(R.layout.bottom_sheet_video_information);

        Video video = videos.get(position);
        String w, h;
        long l;

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
        Objects.requireNonNull(location).setText(Conversion.sizeConversion(l) + "ps");

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