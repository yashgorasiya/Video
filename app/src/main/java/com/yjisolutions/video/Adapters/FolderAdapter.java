package com.yjisolutions.video.Adapters;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.RecyclerView;

import com.yjisolutions.video.R;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class FolderAdapter extends RecyclerView.Adapter<FolderViewHolder>{
    private List<String> listOfFolders;
    protected Context c;
    public FolderAdapter(ArrayList<String> Folders, Context c){
        this.listOfFolders = Folders;
        this.c = c;
    }
    @NonNull
    @Override
    public FolderViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new FolderViewHolder(LayoutInflater.from(c).inflate(R.layout.folder_item, parent, false));
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull FolderViewHolder holder, @SuppressLint("RecyclerView") int position) {
        File file=new File(listOfFolders.get(position));
        File[] list = file.listFiles();
        int count = 0;
//        long size = 0;
        for (File f: Objects.requireNonNull(list)){
            String name = f.getName();
            if (name.endsWith(".mp4") || name.endsWith(".mkv") || name.endsWith(".webm")) {
                count++;
//                size = size + Conversion.sizeToMB(f.length());
            }
        }

//        holder.fSize.setText(Conversion.sizeTotal(size));

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
        if(listOfFolders!=null) return listOfFolders.size();
        else return 0;
    }

    @SuppressLint("NotifyDataSetChanged")
    public void updateList(ArrayList<String> temp) {
        listOfFolders = temp;
        notifyDataSetChanged();
    }

}
