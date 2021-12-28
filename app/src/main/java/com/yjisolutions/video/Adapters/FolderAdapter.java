package com.yjisolutions.video.Adapters;

import android.annotation.SuppressLint;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.RecyclerView;

import com.yjisolutions.video.Fragments.VideosFragment;
import com.yjisolutions.video.Modal.Folder;
import com.yjisolutions.video.R;

import java.util.ArrayList;
import java.util.List;

public class FolderAdapter extends RecyclerView.Adapter<FolderViewHolder> {
    private List<Folder> listOfFolders;
    protected FragmentActivity c;

    public FolderAdapter(ArrayList<Folder> Folders, FragmentActivity c) {
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
        Folder temp = listOfFolders.get(position);

        if (temp.getCount()==1) holder.vCount.setText(temp.getCount()+" Video");
        else holder.vCount.setText(temp.getCount()+" Videos");

        String FName = temp.getName().substring(temp.getName().lastIndexOf("/")+1);
        holder.fName.setText(FName);

        holder.ly.setOnClickListener(v -> {
            FragmentManager fm = c.getSupportFragmentManager();
            VideosFragment videosFragment = new VideosFragment(temp.getName());
            fm.beginTransaction().replace(R.id.homeScreenFrameLayout, videosFragment).commit();
        });

    }

    @Override
    public int getItemCount() {
        if(listOfFolders!=null) return listOfFolders.size();
        else return 0;
    }

    @SuppressLint("NotifyDataSetChanged")
    public void updateList(ArrayList<Folder> temp) {
        listOfFolders = temp;
        notifyDataSetChanged();
    }

}
