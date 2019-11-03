package com.example.kablys;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Iterator;


public class LimitsFragment extends Fragment {
    TextView  bait, gear, forbid, bite, noInfo;
    Button add;
    Context ctx;
    DatabaseAPI db;
    SessionManager Session;
    Spinner fish, amount, lenght;
    RelativeLayout body;


    public LimitsFragment() {
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
        return inflater.inflate(R.layout.fragment_limits, container, false);
    }


    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {

        fish = (Spinner) view.findViewById(R.id.spinner_fish);
        amount = (Spinner) view.findViewById(R.id.spinner_amount);
        lenght = (Spinner) view.findViewById(R.id.spinner_lenght);
        bait = view.findViewById(R.id.calendar_bait);
        gear = view.findViewById(R.id.calendar_gear);
        bite = view.findViewById(R.id.calendar_bite);
        forbid = view.findViewById(R.id.calendar_forbid);
        noInfo = view.findViewById(R.id.CalendarNoInfo);
        body = view.findViewById(R.id.CalendarBody);

        fish.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
               if ((!fish.getSelectedItem().equals("Pasirinkite žuvį")) && (!amount.getSelectedItem().equals("Pasirinkite kiekį")) && (!lenght.getSelectedItem().equals("Pasirinkite ilgį")))
               {

               }

               else
               {

               }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                noInfo.setVisibility(View.GONE);
            }

        });

        month.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                if ((!fish.getSelectedItem().equals("Pasirinkite žuvį")) && (!month.getSelectedItem().equals("Pasirinkite mėnesį")))
                {

                }

                else
                {

                }


            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }


    });


}
}
