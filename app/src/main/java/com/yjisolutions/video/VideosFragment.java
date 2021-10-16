package com.yjisolutions.video;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.widget.SearchView;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentResultListener;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.card.MaterialCardView;
import com.google.android.material.snackbar.Snackbar;
import com.yjisolutions.video.code.Conversion;
import com.yjisolutions.video.code.DeleteFile;
import com.yjisolutions.video.code.VFAdapter;
import com.yjisolutions.video.code.Video;
import com.yjisolutions.video.code.VideoRead;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;


public class VideosFragment extends Fragment {

    private ArrayList<Video> videos = new ArrayList<>();
    private SharedPreferences sp;
    private boolean viewStyle;
    private RecyclerView recyclerView;

    @SuppressLint("StaticFieldLeak")
    public static VFAdapter adapter;

    public static void Update() {
        adapter.update();
    }

    @SuppressLint("SetTextI18n")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View v = inflater.inflate(R.layout.fragment_videos, container, false);

        sp = requireActivity().getSharedPreferences("UserData", Context.MODE_PRIVATE);
        viewStyle = sp.getBoolean("homeScreenLayoutType", true);

        ImageView backButton = v.findViewById(R.id.videoFragmentBack);
        ImageView viewGrid = v.findViewById(R.id.videoFragmentGrid);
        TextView toolBarTitle = v.findViewById(R.id.videoFragmentTitle);
        TextView toolBarSubTitle = v.findViewById(R.id.videoFragmentSubTitle);
        SearchView searchView = v.findViewById(R.id.searchVideos);

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


        backButton.setOnClickListener(v1 -> requireActivity().onBackPressed());
        viewGrid.setOnClickListener(v1 -> {
            @SuppressLint("CommitPrefEdits") SharedPreferences.Editor spe = sp.edit();
            spe.putBoolean("homeScreenLayoutType", !viewStyle);
            if (!spe.commit())
                Toast.makeText(getContext(), "Failed to Save", Toast.LENGTH_SHORT).show();
            requireActivity().onBackPressed();
            Bundle bundle = new Bundle();
            bundle.putString("folderName", requireArguments().getString("folderName"));
            Navigation.findNavController(v).navigate(R.id.folder_to_videos, bundle);
        });

        if (getArguments() != null) {
            String folderName = getArguments().getString("folderName");
            videos = VideoRead.getVideoFromFolder(getContext(), folderName);

            toolBarTitle.setText(folderName.substring(folderName.lastIndexOf("/") + 1));

            if (videos.size() == 1) toolBarSubTitle.setText(videos.size() + " Video");
            else toolBarSubTitle.setText(videos.size() + " Videos");

        }
        recyclerView = v.findViewById(R.id.recViewVideo);
        recyclerView.setHasFixedSize(true);

        int grid;
        if (viewStyle) grid = 1;
        else grid = 2;

        recyclerView.setLayoutManager(new GridLayoutManager(getContext(), grid));
        adapter = new VFAdapter(videos, getActivity(), viewStyle);
        recyclerView.setAdapter(adapter);

        return v;
    }

    @Override
    public void onConfigurationChanged(@NonNull Configuration newConfig) {
        int grid;
        if (viewStyle) grid = 1;
        else grid = 2;
        if (newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE) {
            adapter.OrientationChanged(true);
            recyclerView.setLayoutManager(new GridLayoutManager(getContext(), grid * 2));
        } else if (newConfig.orientation == Configuration.ORIENTATION_PORTRAIT) {
            adapter.OrientationChanged(false);
            recyclerView.setLayoutManager(new GridLayoutManager(getContext(), grid));
        }
        super.onConfigurationChanged(newConfig);
    }

    void filter(String text) {
        ArrayList<Video> temp = new ArrayList<>();
        for (Video d : videos) {
            if (d.getName().toLowerCase().contains(text)) {
                temp.add(d);
            }
        }
        adapter.updateList(temp);
    }
}