package com.yjisolutions.video.code;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.RecyclerView;

import com.yjisolutions.video.R;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public  class recFolderAdapter extends RecyclerView.Adapter<recFolderAdapter.recFolderViewHolder>{
    private List<String> listOfFolders = new ArrayList<>();
    protected Context c;

    public recFolderAdapter(ArrayList<String> Folders, Context c){
            this.listOfFolders = Folders;
            this.c = c;
    }
    @NonNull
    @Override
    public recFolderViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new recFolderViewHolder(LayoutInflater.from(c).inflate(R.layout.folder_item, parent, false));
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull recFolderViewHolder holder, @SuppressLint("RecyclerView") int position) {
        File file=new File(listOfFolders.get(position));
        File[] list = file.listFiles();
        int count = 0;
        for (File f: Objects.requireNonNull(list)){
            String name = f.getName();
            if (name.endsWith(".mp4") || name.endsWith(".mkv") || name.endsWith(".webm"))
                count++;
        }
        if (count==1) holder.vCount.setText(count+" Video");
        else holder.vCount.setText(count+" Videos");
        String FName = listOfFolders.get(position).substring(listOfFolders.get(position).lastIndexOf("/")+1);
        holder.fName.setText(FName);
        holder.ly.setOnClickListener(v -> {
            Bundle bundle = new Bundle();
            bundle.putString("folderName", listOfFolders.get(position));
            Navigation.findNavController(v).navigate(R.id.folder_to_videos, bundle);

        });
    }


    @Override
    public int getItemCount() {
        return listOfFolders.size();
    }

    @SuppressLint("NotifyDataSetChanged")
    public void updateList(ArrayList<String> temp) {
        listOfFolders = temp;
        notifyDataSetChanged();
    }

    static class recFolderViewHolder extends RecyclerView.ViewHolder{
        TextView fName,vCount;
        LinearLayout ly;
        public recFolderViewHolder(@NonNull View itemView) {
            super(itemView);
            fName = itemView.findViewById(R.id.folderName);
            vCount = itemView.findViewById(R.id.folderVideoCount);
            ly = itemView.findViewById(R.id.folderItemLayout);
        }
    }
}
