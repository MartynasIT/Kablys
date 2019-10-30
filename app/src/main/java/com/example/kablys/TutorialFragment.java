package com.example.kablys;

import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.Toolbar;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Switch;
import android.widget.TextView;

import com.google.android.youtube.player.YouTubeInitializationResult;
import com.google.android.youtube.player.YouTubePlayer;
import com.google.android.youtube.player.YouTubePlayerSupportFragment;

import java.util.ArrayList;
import java.util.Iterator;


public class TutorialFragment extends Fragment {
    Context ctx;
    ListView list;
    DatabaseAPI db;
    FrameLayout yt;
    LinearLayout Tuts;
    private YouTubePlayer YPlayer;
    private static  String GoogleAPI = "AIzaSyCf_4GpWcrv_4dOwxWhFvxgs0jTyrZqqoA";
    private ArrayList<String[]> tutorials = new ArrayList<String[]>();
    String[] names = new String[7];
    String[] VIdeoids = new String[7];
    SessionManager Session;

    public TutorialFragment() {
    }


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        ctx=context;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        db = new DatabaseAPI(ctx);
        Session = new SessionManager(ctx);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle SavedInstanceState)
    {

        return inflater.inflate(R.layout.fragment_tutorial, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        tutorials = db.getTuturials();
        yt = view.findViewById(R.id.youtube_fragment);
        list = view.findViewById(R.id.Tutoriallist);
        Tuts = view.findViewById(R.id.Tutorials);
        Iterator<String[]> itr = tutorials.iterator();
        while (itr.hasNext()) {
            String[] array = itr.next();
            names[Integer.valueOf(array[2])] = array[0];
            VIdeoids[Integer.valueOf(array[2])] = array[1];
        }

        TuturialAdapter adapter = new TuturialAdapter(getActivity(), names, VIdeoids);
        list.setAdapter(adapter);

        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapter, View v, int position, long arg3) {
                String value = (String) adapter.getItemAtPosition(position);
                final int index = findIndex(names, value);
                YouTubePlayerSupportFragment youTubePlayerFragment = YouTubePlayerSupportFragment.newInstance();

                youTubePlayerFragment.initialize(GoogleAPI, new YouTubePlayer.OnInitializedListener() {

                    @Override
                    public void onInitializationSuccess(YouTubePlayer.Provider provider, YouTubePlayer player, boolean wasRestored) {
                        Tuts.setVisibility(View.GONE);
                        yt.setVisibility(View.VISIBLE);
                        if (!wasRestored) {
                            YPlayer = player;
                            YPlayer.setFullscreen(true);
                            YPlayer.loadVideo(VIdeoids[index]);
                            YPlayer.play();
                        }

                    }

                    @Override
                    public void onInitializationFailure(YouTubePlayer.Provider provider, YouTubeInitializationResult youTubeInitializationResult) {

                    }


                });

                FragmentTransaction transaction = getChildFragmentManager().beginTransaction();
                transaction.add(R.id.youtube_fragment, youTubePlayerFragment).commit();

            }
        });
    }

    @Override
    public void onDestroyView() {
        if(YPlayer!=null) {

            YPlayer.release();
            Intent SuccessIntent = new Intent(ctx, DrawerActivity.class);
            startActivity(SuccessIntent);
        }
        super.onDestroyView();
    }

    public int findIndex(String arr[], String fish)
    {

        int len = arr.length;
        int i = 0;
        while (i < len) {

            if (fish.equals(arr[i])) {
                return i;
            }
            else {
                i = i + 1;
            }
        }
        return -1;
    }




}
