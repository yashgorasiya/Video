package com.yjisolutions.video.Fragments;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.fragment.app.Fragment;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.yjisolutions.video.R;
import com.yjisolutions.video.code.API;

public class FeedBackFragment extends Fragment {


    public FeedBackFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_feed_back, container, false);

        String base = API.TOKEN;
        String getmsg = "/getUpdates";

        TextView textView = v.findViewById(R.id.helloWorld);
        EditText editText = v.findViewById(R.id.editText);
        ImageButton sendBtn = v.findViewById(R.id.sendButton);
        final String[] msg = {""};

        // Instantiate the RequestQueue.
        RequestQueue queue = Volley.newRequestQueue(requireContext());


        // Request a string response from the provided URL.
        StringRequest stringRequest = new StringRequest(Request.Method.GET, base + getmsg,
                new Response.Listener<String>() {
                    @SuppressLint("SetTextI18n")
                    @Override
                    public void onResponse(String response) {
                        // Display the first 500 characters of the response string.
                        textView.setText("Connected");
                    }
                }, new Response.ErrorListener() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onErrorResponse(VolleyError error) {
                textView.setText("Please Check Your Internet Connection");
            }
        });

        // Add the request to the RequestQueue.
        queue.add(stringRequest);

        sendBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                msg[0] = String.valueOf(editText.getText());
                editText.setText("");

                String msgf = "/sendMessage?chat_id=@videoplayerreport&text=" + msg[0];

                // Request a string response from the provided URL.
                StringRequest stringRequest = new StringRequest(Request.Method.GET, base + msgf,
                        new Response.Listener<String>() {
                            @SuppressLint("SetTextI18n")
                            @Override
                            public void onResponse(String response) {
                                // Display the first 500 characters of the response string.
                                textView.setText("Send Successfully");
                            }
                        }, new Response.ErrorListener() {
                    @SuppressLint("SetTextI18n")
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        textView.setText("Please Check Your Internet Connection");
                    }
                });

                // Add the request to the RequestQueue.
                queue.add(stringRequest);

            }
        });


        return v;
    }


}