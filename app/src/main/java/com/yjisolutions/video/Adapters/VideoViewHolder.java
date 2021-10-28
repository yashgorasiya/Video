package com.yjisolutions.video.Adapters;

import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.yjisolutions.video.R;

public class VideoViewHolder extends RecyclerView.ViewHolder {

    ImageView thumb, more;
    TextView title, duration, size;
    ConstraintLayout previewTile;
    SeekBar seekBar;
    FrameLayout selectedIndicator;

    public VideoViewHolder(@NonNull View itemView) {
        super(itemView);
        thumb = itemView.findViewById(R.id.thumbnail);
        more = itemView.findViewById(R.id.previewHomeMore);
        title = itemView.findViewById(R.id.titleV);
        duration = itemView.findViewById(R.id.durationV);
        size = itemView.findViewById(R.id.sizePreview);
        previewTile = itemView.findViewById(R.id.previewTileLayout);
        seekBar = itemView.findViewById(R.id.home_preview_seekbar);
        selectedIndicator = itemView.findViewById(R.id.selectedIndicator);
    }
}