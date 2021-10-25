package com.yjisolutions.video.Fragments;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.SearchView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.yjisolutions.video.Activities.PlayerActivity;
import com.yjisolutions.video.Adapters.FolderAdapter;
import com.yjisolutions.video.Interfaces.OnPermissionGranted;
import com.yjisolutions.video.R;
import com.yjisolutions.video.code.Permissions;
import com.yjisolutions.video.code.Utils;
import com.yjisolutions.video.code.VideoRead;

import java.util.ArrayList;


public class FolderFragment extends Fragment implements OnPermissionGranted {
    private ArrayList<VideoRead.Folder> folder = new ArrayList<>();
    private FolderAdapter adapter;
    private RecyclerView recyclerView;
    private ImageView more;
    private SearchView searchView;

    @Override
    public void onStart() {
        Permissions.request(requireActivity(), this);
        super.onStart();
    }


    @SuppressLint("NonConstantResourceId")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View v = inflater.inflate(R.layout.fragment_folder, container, false);

        FloatingActionButton recentPlayed = v.findViewById(R.id.recentPlayResume);
        recentPlayed.setOnClickListener(v1 -> recentPlayedResume());

        recyclerView = v.findViewById(R.id.folderRecView);
        recyclerView.setHasFixedSize(true);
        more = v.findViewById(R.id.homeScreenMore);
        searchView = v.findViewById(R.id.searchFolder);

        initListeners();
        return v;
    }

    @SuppressLint("NonConstantResourceId")
    private void initListeners(){
        more.setOnClickListener(v1 -> {
            PopupMenu popupMenu = new PopupMenu(getContext(),more);
            popupMenu.inflate(R.menu.menu);
            popupMenu.setOnMenuItemClickListener(item -> {
                switch (item.getItemId()) {
                    case R.id.Name:
                        Utils.SortBy(Utils.NAME,Utils.FOLDERS);
                        initRecViewFolders();
                        return true;
                    case R.id.DateAdded:
                        Utils.SortBy(Utils.DATE_ADDED,Utils.FOLDERS);
                        initRecViewFolders();
                        return true;
                    case R.id.DateModified:
                        Utils.SortBy(Utils.DATE_MODIFIED,Utils.FOLDERS);
                        initRecViewFolders();
                        return true;
                    case R.id.Size:
                        Utils.SortBy(Utils.SIZE,Utils.FOLDERS);
                        initRecViewFolders();
                        return true;
                    case R.id.Duration:
                        Utils.SortBy(Utils.DURATION,Utils.FOLDERS);
                        initRecViewFolders();
                        return true;
                    case R.id.Ascending:
                        Utils.setSortOrder(Utils.ASCENDING,Utils.FOLDERS);
                        initRecViewFolders();
                        return true;
                    case R.id.Descending:
                        Utils.setSortOrder(Utils.DESCENDING,Utils.FOLDERS);
                        initRecViewFolders();
                        return true;
                    default:
                        return false;
                }
            });
            popupMenu.show();
        });
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
    }
    private void initRecViewFolders() {
        Configuration configuration = requireActivity().getResources().getConfiguration();
        int grid = 1;
        if (configuration.orientation == Configuration.ORIENTATION_LANDSCAPE) grid = 2;
        folder = VideoRead.getFolders(getContext());
        adapter = new FolderAdapter(folder, getContext());
        recyclerView.setLayoutManager(new GridLayoutManager(getContext(), grid));
        recyclerView.setAdapter(adapter);
//        recyclerView.setOnFlingListener(new RecyclerView.OnFlingListener() {
//            @Override
//            public boolean onFling(int velocityX, int velocityY) {
//                MaterialToolbar t = requireView().findViewById(R.id.materialToolbarFolders);
//                if (velocityY>0) t.setVisibility(View.GONE);
//                else t.setVisibility(View.VISIBLE);
//                return false;
//            }
//        });
    }

    private void recentPlayedResume() {

        if (!Utils.RECENTLY_PLAYED_VIDEO_URL.equals("0")) {
            requireActivity().startActivity(
                    new Intent(getContext(), PlayerActivity.class)
                            .putExtra("url", Utils.RECENTLY_PLAYED_VIDEO_URL)
                            .putExtra("title", Utils.RECENTLY_PLAYED_VIDEO_TITLE));
        }
        else Toast.makeText(getContext(), "Not Played any Video yet", Toast.LENGTH_SHORT).show();
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
        ArrayList<VideoRead.Folder> temp = new ArrayList<>();
        for (VideoRead.Folder d : folder) {
            if (d.getName().toLowerCase().contains(text)) {
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