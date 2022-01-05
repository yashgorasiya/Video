package com.yjisolutions.video.Fragments;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.DecelerateInterpolator;
import android.widget.ImageButton;
import android.widget.PopupMenu;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton;
import com.yjisolutions.video.Activities.MainActivity;
import com.yjisolutions.video.Activities.PlayerActivity;
import com.yjisolutions.video.Adapters.FolderAdapter;
import com.yjisolutions.video.Interfaces.OnPermissionGranted;
import com.yjisolutions.video.Modal.Folder;
import com.yjisolutions.video.R;
import com.yjisolutions.video.code.Permissions;
import com.yjisolutions.video.code.Utils;
import com.yjisolutions.video.code.VideoRead;

import java.util.ArrayList;


public class FolderFragment extends Fragment implements OnPermissionGranted {

    private static ArrayList<Folder> folder = new ArrayList<>();
    private FolderAdapter adapter;
    private RecyclerView recyclerView;
    private ImageButton more;
    private android.widget.SearchView searchView;
    private ExtendedFloatingActionButton recentPlayed;
    private boolean recViewInitiated = false;

    public FolderFragment() {
    }

    @Override
    public void onStart() {
        Permissions.request(requireActivity(), this);
        super.onStart();
    }


    @SuppressLint({"NonConstantResourceId", "UseCompatLoadingForDrawables"})
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View v = inflater.inflate(R.layout.fragment_folder, container, false);

        recentPlayed = v.findViewById(R.id.recentPlayResume);
        recyclerView = v.findViewById(R.id.folderRecView);
        recyclerView.setHasFixedSize(true);
        more = v.findViewById(R.id.homeScreenMore);
        searchView = v.findViewById(R.id.searchFolder);

        initListeners();
        return v;
    }

    @SuppressLint({"NonConstantResourceId", "UseCompatLoadingForDrawables"})
    private void initListeners() {
        recentPlayed.setOnClickListener(v1 -> recentPlayedResume());

        more.setOnClickListener(v1 -> {
            PopupMenu popupMenu = new PopupMenu(getContext(), more);
            popupMenu.inflate(R.menu.menu);
            popupMenu.setOnMenuItemClickListener(item -> {
                switch (item.getItemId()) {
                    case R.id.Name:
                        Utils.SortBy(Utils.NAME, Utils.FOLDERS);
                        initRecViewFolders();
                        return true;
                    case R.id.DateAdded:
                        Utils.SortBy(Utils.DATE_ADDED, Utils.FOLDERS);
                        initRecViewFolders();
                        return true;
                    case R.id.DateModified:
                        Utils.SortBy(Utils.DATE_MODIFIED, Utils.FOLDERS);
                        initRecViewFolders();
                        return true;
                    case R.id.Size:
                        Utils.SortBy(Utils.SIZE, Utils.FOLDERS);
                        initRecViewFolders();
                        return true;
                    case R.id.Duration:
                        Utils.SortBy(Utils.DURATION, Utils.FOLDERS);
                        initRecViewFolders();
                        return true;
                    case R.id.Ascending:
                        Utils.setSortOrder(Utils.ASCENDING, Utils.FOLDERS);
                        initRecViewFolders();
                        return true;
                    case R.id.Descending:
                        Utils.setSortOrder(Utils.DESCENDING, Utils.FOLDERS);
                        initRecViewFolders();
                        return true;
                    default:
                        return false;
                }

            });
            popupMenu.show();
        });


        searchView.setOnQueryTextListener(new android.widget.SearchView.OnQueryTextListener() {
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
        searchView.setOnSearchClickListener(v -> {
            searchView.setBackground(requireContext().getResources().getDrawable(R.drawable.delete_dialog_bg));
            searchView.setPadding(40, 16, 8, 16);
        });
        searchView.setOnCloseListener(() -> {
            searchView.setBackgroundColor(Color.TRANSPARENT);
            searchView.setPadding(0, 0, 0, 0);
            return false;
        });
    }

    @SuppressLint("NewApi")
    private void initRecViewFolders() {
        Configuration configuration = requireActivity().getResources().getConfiguration();

        int grid = 1;
        if (configuration.orientation == Configuration.ORIENTATION_LANDSCAPE) grid = 2;

        folder = VideoRead.getFolders(getContext());
        adapter = new FolderAdapter(folder, getActivity());

        recyclerView.setLayoutManager(new GridLayoutManager(getContext(), grid));
        recyclerView.setAdapter(adapter);
        recyclerView.setOnFlingListener(new RecyclerView.OnFlingListener() {
            @Override
            public boolean onFling(int velocityX, int velocityY) {
                ConstraintLayout t = requireView().findViewById(R.id.ToolbarConstraintLayoutFolders);
                if (velocityY>0) {
                    t.animate().translationY(-t.getBottom()).setInterpolator(new AccelerateInterpolator()).start();
                    recentPlayed.setExtended(false);
                    t.setVisibility(View.GONE);
                } else {
                    t.animate().translationY(0).setInterpolator(new DecelerateInterpolator()).start();
                    recentPlayed.setExtended(true);
                    new Handler().postDelayed(() -> recentPlayed.setExtended(false), 1500);
                    t.setVisibility(View.VISIBLE);
                }
                return false;
            }
        });

        //Animate ResentPlayedButton
        new Handler().postDelayed(() -> recentPlayed.setExtended(false), 1500);
    }

    private void recentPlayedResume() {
        if (!Utils.RECENTLY_PLAYED_VIDEO_FOLDER.equals("0")) {
            try {
                FragmentManager fm = requireActivity().getSupportFragmentManager();
                VideosFragment videosFragment = new VideosFragment(Utils.RECENTLY_PLAYED_VIDEO_FOLDER);
                fm.beginTransaction().replace(R.id.homeScreenFrameLayout, videosFragment).commit();

                MainActivity.videoFisOpen = true;

                requireActivity().startActivityForResult(
                        new Intent(requireActivity().getBaseContext(), PlayerActivity.class)
                                .putExtra("position", Utils.RECENTLY_PLAYED_VIDEO_POSITION)
                        , 1);

            } catch (Exception E) {
                Toast.makeText(getContext(), "File may be Deleted " + E, Toast.LENGTH_SHORT).show();
            }

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

    @Override
    public void onGranted() {
        initRecViewFolders();
        recViewInitiated = true;
    }

    public void filter(String text) {
        ArrayList<Folder> temp = new ArrayList<>();
        for (Folder d : folder) {
            if (d.getName().toLowerCase().contains(text)) {
                temp.add(d);
            }
        }
        if (recViewInitiated) adapter.updateList(temp);
    }

}