package com.yjisolutions.video.code;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.android.material.card.MaterialCardView;
import com.yjisolutions.video.R;
import com.yjisolutions.video.player;

import java.util.List;

public class VAdapter extends RecyclerView.Adapter<viewHolder> {

    List<Video> videos;
    Context c;

    public VAdapter(List<Video> videos,Context context) {
        this.videos = videos;
        this.c = context;
    }

    @NonNull
    @Override
    public viewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new viewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.preview,parent,false));
    }

    @Override
    public void onBindViewHolder(@NonNull viewHolder holder, int position) {
        Video video = videos.get(position);
        holder.title.setText(video.getName());
        holder.size.setText(sizeConversion.sizeConversion(video.getSize()));
        holder.duration.setText(sizeConversion.timerConversion(video.getDuration()));
        holder.previewTile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(c, player.class);
                intent.putExtra("url", video.getUri().toString());
                intent.putExtra("title", video.getName());
                c.startActivity(intent);
            }
        });

        Glide.with(c)
                .load(video.getUri())
                .into(holder.thumb);
    }

    @Override
    public int getItemCount() {
        return videos.size();
    }
}
class viewHolder extends RecyclerView.ViewHolder{

    ImageView thumb;
    TextView title,duration,size;
    MaterialCardView previewTile;
    public viewHolder(@NonNull View itemView) {
        super(itemView);
        thumb = itemView.findViewById(R.id.thumbnail);
        title = itemView.findViewById(R.id.titleV);
        duration = itemView.findViewById(R.id.durationV);
        size = itemView.findViewById(R.id.sizePreview);
        previewTile = itemView.findViewById(R.id.previewTile);
    }
}