package com.yjisolutions.video.Adapters;

import android.view.View;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.yjisolutions.video.R;

public class PlayerVideoViewHolder extends RecyclerView.ViewHolder {
    ImageView thumb;
    TextView title, duration;
    ConstraintLayout previewTile;
    SeekBar seekBar;
    public PlayerVideoViewHolder(@NonNull View itemView) {
        super(itemView);
        thumb = itemView.findViewById(R.id.thumbnail);
        title = itemView.findViewById(R.id.titleV);
        duration = itemView.findViewById(R.id.durationV);
        previewTile = itemView.findViewById(R.id.previewTileLayout);
        seekBar = itemView.findViewById(R.id.home_preview_seekbar);
    }
}
