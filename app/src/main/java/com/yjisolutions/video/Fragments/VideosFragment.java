package com.yjisolutions.video.Fragments;

import android.annotation.SuppressLint;
import android.content.res.Configuration;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.SearchView;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.yjisolutions.video.Activities.MainActivity;
import com.yjisolutions.video.Adapters.VideoAdapter;
import com.yjisolutions.video.Interfaces.OnPlayerActivityDestroy;
import com.yjisolutions.video.R;
import com.yjisolutions.video.code.Utils;
import com.yjisolutions.video.code.Video;
import com.yjisolutions.video.code.VideoRead;

import java.util.ArrayList;


public class VideosFragment extends Fragment implements OnPlayerActivityDestroy {

    private ArrayList<Video> videos = new ArrayList<>();
    private final boolean viewStyle = Utils.VIEW_STYLE;
    private RecyclerView recyclerView;
    private VideoAdapter adapter;
    private ImageView videoFragmentMore;
    private SearchView searchView;
    private String folderName;
    private TextView toolBarSubTitle;

    @SuppressLint({"SetTextI18n", "UseCompatLoadingForDrawables"})
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View v = inflater.inflate(R.layout.fragment_videos, container, false);

        MainActivity.setOnPlayerActivityDestroyIF(this);
        ImageView backButton = v.findViewById(R.id.videoFragmentBack);
        ImageView viewGrid = v.findViewById(R.id.videoFragmentGrid);
        TextView toolBarTitle = v.findViewById(R.id.videoFragmentTitle);
        toolBarSubTitle = v.findViewById(R.id.videoFragmentSubTitle);
        searchView = v.findViewById(R.id.searchVideos);
        videoFragmentMore = v.findViewById(R.id.videoFragmentMore);
        recyclerView = v.findViewById(R.id.recViewVideo);

        recyclerView.setHasFixedSize(true);

        if (viewStyle) viewGrid.setImageDrawable(getResources().getDrawable(R.drawable.ic_baseline_grid_view_24));
        else viewGrid.setImageDrawable(getResources().getDrawable(R.drawable.ic_baseline_view_list_24));

        backButton.setOnClickListener(v1 -> requireActivity().onBackPressed());

        viewGrid.setOnClickListener(v1 -> {
            if (!Utils.changeVideoTileStyle()) Toast.makeText(getContext(), "Failed to Save", Toast.LENGTH_SHORT).show();
            requireActivity().onBackPressed();
            Bundle bundle = new Bundle();
            bundle.putString("folderName", requireArguments().getString("folderName"));
            Navigation.findNavController(v).navigate(R.id.folder_to_videos, bundle);
        });

        if (getArguments() != null) {
            folderName = getArguments().getString("folderName");
            toolBarTitle.setText(folderName.substring(folderName.lastIndexOf("/") + 1));
        }



        initRecViewVideos();
        initListeners();
        return v;
    }

    @Override
    public void onConfigurationChanged(@NonNull Configuration newConfig) {
        int grid;
        if (viewStyle) grid = 1;
        else grid = 2;
        if (newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE) {
            recyclerView.setLayoutManager(new GridLayoutManager(getContext(), grid * 2));
        } else if (newConfig.orientation == Configuration.ORIENTATION_PORTRAIT) {
            recyclerView.setLayoutManager(new GridLayoutManager(getContext(), grid));
        }
        super.onConfigurationChanged(newConfig);
    }

    @SuppressLint("SetTextI18n")
    private void initRecViewVideos(){
        videos = VideoRead.getVideoFromFolder(getContext(), folderName);
        int grid;
        if (viewStyle) grid = 1;
        else grid = 2;

        Configuration configuration = requireActivity().getResources().getConfiguration();
        if (configuration.orientation == Configuration.ORIENTATION_LANDSCAPE) grid = grid * 2;

        recyclerView.setLayoutManager(new GridLayoutManager(getContext(), grid));
        adapter = new VideoAdapter(videos, getActivity(), viewStyle);
        recyclerView.setAdapter(adapter);

        if (videos.size() == 1) toolBarSubTitle.setText(videos.size() + " Video");
        else toolBarSubTitle.setText(videos.size() + " Videos");
    }

    @SuppressLint("NonConstantResourceId")
    private void initListeners(){
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

        videoFragmentMore.setOnClickListener(v -> {
            PopupMenu popupMenu = new PopupMenu(getContext(),videoFragmentMore);
            popupMenu.inflate(R.menu.menu);
            popupMenu.setOnMenuItemClickListener(item -> {
                switch (item.getItemId()) {
                    case R.id.Name:
                        Utils.SortBy(Utils.NAME,Utils.VIDEOS);
                        initRecViewVideos();
                        return true;
                    case R.id.DateAdded:
                        Utils.SortBy(Utils.DATE_ADDED,Utils.VIDEOS);
                        initRecViewVideos();
                        return true;
                    case R.id.DateModified:
                        Utils.SortBy(Utils.DATE_MODIFIED,Utils.VIDEOS);
                        initRecViewVideos();
                        return true;
                    case R.id.Size:
                        Utils.SortBy(Utils.SIZE,Utils.VIDEOS);
                        initRecViewVideos();
                        return true;
                    case R.id.Duration:
                        Utils.SortBy(Utils.DURATION,Utils.VIDEOS);
                        initRecViewVideos();
                        return true;
                    case R.id.Ascending:
                        Utils.setSortOrder(Utils.ASCENDING,Utils.VIDEOS);
                        initRecViewVideos();
                        return true;
                    case R.id.Descending:
                        Utils.setSortOrder(Utils.DESCENDING,Utils.VIDEOS);
                        initRecViewVideos();
                        return true;
                    default:
                        return false;
                }
            });
            popupMenu.show();
        });

    }

    private void filter(String text) {
        ArrayList<Video> temp = new ArrayList<>();
        for (Video d : videos) {
            if (d.getName().toLowerCase().contains(text)) {
                temp.add(d);
            }
        }
        adapter.updateList(temp);
    }

    @Override
    public void refreshVideoFragment() {
        adapter.update();
    }
}