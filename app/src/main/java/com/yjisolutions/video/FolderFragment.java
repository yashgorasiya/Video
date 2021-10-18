package com.yjisolutions.video;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.SearchView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.appbar.MaterialToolbar;
import com.yjisolutions.video.code.OnPermissionGranted;
import com.yjisolutions.video.code.Permissions;
import com.yjisolutions.video.code.VideoRead;
import com.yjisolutions.video.code.recFolderAdapter;

import java.util.ArrayList;
import java.util.Objects;


public class FolderFragment extends Fragment implements OnPermissionGranted {
    private ArrayList<String> folder;
    private recFolderAdapter adapter;
    private RecyclerView recyclerView;

    @Override
    public void onStart() {
        Permissions.request(requireActivity(), this);
        super.onStart();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment


        View v = inflater.inflate(R.layout.fragment_folder, container, false);

        ImageView recentPlayed = v.findViewById(R.id.recentPlayResume);
        SearchView searchView = v.findViewById(R.id.searchFolder);

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                filter(newText.toLowerCase());
                return false;
            }
        });

        recyclerView = v.findViewById(R.id.folderRecView);
        recyclerView.setHasFixedSize(true);
        recentPlayed.setOnClickListener(v1 -> recentPlayedResume());
        return v;
    }

    private void initRecViewFolders() {
        Configuration configuration = requireActivity().getResources().getConfiguration();
        int grid = 1;
        if (configuration.orientation == Configuration.ORIENTATION_LANDSCAPE) grid = 2;
        folder = VideoRead.getFolders(getContext());
        adapter = new recFolderAdapter(folder, getContext());
        recyclerView.setLayoutManager(new GridLayoutManager(getContext(), grid));
        recyclerView.setAdapter(adapter);
        recyclerView.setOnFlingListener(new RecyclerView.OnFlingListener() {
            @Override
            public boolean onFling(int velocityX, int velocityY) {
                MaterialToolbar t = requireView().findViewById(R.id.materialToolbarFolders);
                if (velocityY>0) t.setVisibility(View.GONE);
                else t.setVisibility(View.VISIBLE);
                return false;
            }
        });
    }


    private void recentPlayedResume() {
        SharedPreferences sp = requireActivity().getSharedPreferences("UserData", Context.MODE_PRIVATE);
        String videoTitle = sp.getString("recentVideoTitle", "0");
        String videoUrl = sp.getString("recentVideoUrl", "0");
        Activity activity = getActivity();
        if (!videoUrl.equals("0") && activity != null) {
            activity.startActivity(
                    new Intent(activity, player.class)
                            .putExtra("url", videoUrl)
                            .putExtra("title", videoTitle));
        } else Toast.makeText(activity, "Not Played any Video yet", Toast.LENGTH_SHORT).show();
    }


    @Override
    public void onConfigurationChanged(@NonNull Configuration newConfig) {
        if (newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE) {
            recyclerView.setLayoutManager(new GridLayoutManager(getContext(), 2));
        } else if (newConfig.orientation == Configuration.ORIENTATION_PORTRAIT) {
            recyclerView.setLayoutManager(new GridLayoutManager(getContext(), 1));
        }
        super.onConfigurationChanged(newConfig);
    }

    void filter(String text) {
        ArrayList<String> temp = new ArrayList<>();
        for (String d : folder) {
            if (d.toLowerCase().contains(text)) {
                temp.add(d);
            }
        }
        adapter.updateList(temp);
    }


    @Override
    public void onGranted() {
        initRecViewFolders();
    }
}