package com.yjisolutions.video.Adapters;

import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.yjisolutions.video.R;

public class FolderViewHolder extends RecyclerView.ViewHolder{
    TextView fName,vCount;
    ConstraintLayout ly;
    public FolderViewHolder(@NonNull View itemView) {
        super(itemView);
        fName = itemView.findViewById(R.id.folderName);
        vCount = itemView.findViewById(R.id.folderVideoCount);
        ly = itemView.findViewById(R.id.folderItemLayout);
    }
}