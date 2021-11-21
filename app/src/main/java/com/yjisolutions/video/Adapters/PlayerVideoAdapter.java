package com.yjisolutions.video.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.yjisolutions.video.Fragments.VideosFragment;
import com.yjisolutions.video.Modal.Video;
import com.yjisolutions.video.R;
import com.yjisolutions.video.code.Conversion;
import com.yjisolutions.video.code.Utils;

public class PlayerVideoAdapter extends RecyclerView.Adapter<PlayerVideoViewHolder> {

    Context context;
    public PlayerVideoAdapter(Context context) {
        this.context = context;
    }

    @NonNull
    @Override
    public PlayerVideoViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new PlayerVideoViewHolder(LayoutInflater.from(context).inflate(R.layout.player_activity_list_preview_item,parent,false));
    }

    @Override
    public void onBindViewHolder(@NonNull PlayerVideoViewHolder holder, int position) {
        Video video = VideosFragment.videos.get(position);

        holder.title.setText(video.getName());
        holder.duration.setText(Conversion.timerConversion(video.getDuration()));
        holder.seekBar.setClickable(false);
        holder.seekBar.setPadding(0, 0, 0, 0);
        long lastPlayed = Utils.sp.getLong(video.getName(), 0);

        if (lastPlayed > 0) {
            holder.seekBar.setMax(video.getDuration());
            holder.seekBar.setProgress((int) lastPlayed);
        }

        holder.previewTile.setOnClickListener(view -> {
            // PLay Video
        });

        Glide.with(context)
                .load(video.getUri())
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .override(250, 200)
                .into(holder.thumb);
    }

    @Override
    public int getItemCount() {
        return VideosFragment.videos.size();
    }
}
