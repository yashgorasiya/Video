package com.yjisolutions.video;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.yjisolutions.video.code.VideoRead;

import java.util.ArrayList;
import java.util.List;


public class FolderFragment extends Fragment {
    private ArrayList<String> folder;
//    public FolderFragment(List<String> folders){
//        this.folder = folders;
//    }

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
        recAdapter adpter = new recAdapter(v);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setAdapter(adpter);
        return v;
    }

    class recAdapter extends RecyclerView.Adapter<viewHolder>{
        private final View v;
        public recAdapter(View v){
                this.v = v;
        }
        @NonNull
        @Override
        public viewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            return new viewHolder(LayoutInflater.from(getContext()).inflate(R.layout.folder_item, parent, false));
        }

        @Override
        public void onBindViewHolder(@NonNull viewHolder holder, @SuppressLint("RecyclerView") int position) {
            holder.fname.setText(folder.get(position));
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
        TextView fname;
        LinearLayout ly;
        public viewHolder(@NonNull View itemView) {
            super(itemView);
            fname = itemView.findViewById(R.id.folderName);
            ly = itemView.findViewById(R.id.folderItemLayout);
        }
    }
}