package com.yjisolutions.video.Fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;

import com.yjisolutions.video.R;

public class FeedBackFragment extends Fragment {


    public FeedBackFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_feed_back, container, false);
//        String urlString = "https://api.telegram.org/bot%s/sendMessage?chat_id=%s&text=%s";
//

//        String chatId = "@videoplayerreport";
//        String text = "Hello world!";

        return v;
    }


}