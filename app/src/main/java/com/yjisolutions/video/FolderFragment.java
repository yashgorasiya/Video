package com.yjisolutions.video;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.karumi.dexter.Dexter;
import com.karumi.dexter.MultiplePermissionsReport;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.multi.MultiplePermissionsListener;
import com.yjisolutions.video.code.VideoRead;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;


public class FolderFragment extends Fragment {
    private ArrayList<String> folder;

    @Override
    public void onStart() {

        folder = VideoRead.getFolders(getContext());
        super.onStart();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_folder, container, false);
        RecyclerView recyclerView = v.findViewById(R.id.folderRecView);
        recAdapter adapter = new recAdapter();
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setAdapter(adapter);
        return v;
    }

    class recAdapter extends RecyclerView.Adapter<viewHolder>{

        @NonNull
        @Override
        public viewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            return new viewHolder(LayoutInflater.from(getContext()).inflate(R.layout.folder_item, parent, false));
        }

        @SuppressLint("SetTextI18n")
        @Override
        public void onBindViewHolder(@NonNull viewHolder holder, @SuppressLint("RecyclerView") int position) {
            File file=new File(folder.get(position));
            File[] list = file.listFiles();
            int count = 0;
            for (File f: Objects.requireNonNull(list)){
                String name = f.getName();
                if (name.endsWith(".mp4") || name.endsWith(".mkv") || name.endsWith(".webm"))
                    count++;
            }
            if (count==1) holder.vCount.setText(count+" Video");
            else holder.vCount.setText(count+" Videos");
            String FName = folder.get(position).substring(folder.get(position).lastIndexOf("/")+1);
            holder.fName.setText(FName);
            holder.ly.setOnClickListener(v -> {
                Bundle bundle = new Bundle();
                bundle.putString("folderName", folder.get(position));
                Navigation.findNavController(v).navigate(R.id.folder_to_videos, bundle);

            });
        }

        @Override
        public int getItemCount() {
            return folder.size();
        }
    }
    static class viewHolder extends RecyclerView.ViewHolder{
        TextView fName,vCount;
        LinearLayout ly;
        public viewHolder(@NonNull View itemView) {
            super(itemView);
            fName = itemView.findViewById(R.id.folderName);
            vCount = itemView.findViewById(R.id.folderVideoCount);
            ly = itemView.findViewById(R.id.folderItemLayout);
        }
    }
}