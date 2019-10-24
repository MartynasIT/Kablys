package com.example.kablys;

import android.content.Context;
import android.content.DialogInterface;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.widget.Toolbar;
import android.text.method.ScrollingMovementMethod;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.model.LatLng;

import java.util.ArrayList;
import java.util.Iterator;


public class CalendarFragment extends Fragment {
    TextView  bait, gear, forbid, bite;
    Button add;
    Context ctx;
    DatabaseAPI db;
    SessionManager Session;
    Spinner month, fish;
    private ArrayList<String[]> calendar = new ArrayList<String[]>();


    public CalendarFragment() {
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
        calendar = db.getCalendar();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle SavedInstanceState)
    {
        return inflater.inflate(R.layout.layout_calendar_dialog, container, false);
    }

    private void putData(String month, String fish)
    {
        Iterator<String[]> itr = calendar.iterator();
        while (itr.hasNext()) {
            Object[] array = itr.next();
            if (array[0].equals(fish) && array[1].equals(month))
            {
                bite.setText(array[2].toString());
                bait.setText(array[4].toString());
                forbid.setText(array[5].toString());
                gear.setText(array[3].toString());

            }

        }
    }
    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {

        fish = (Spinner) view.findViewById(R.id.spinner_fish);
        month = (Spinner) view.findViewById(R.id.spinner_month);
        bait = view.findViewById(R.id.calendar_bait);
        gear = view.findViewById(R.id.calendar_gear);
        bite = view.findViewById(R.id.calendar_bite);
        forbid = view.findViewById(R.id.calendar_forbid);

        fish.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
               if ((!fish.getSelectedItem().equals("Pasirinkite žuvį")) && (!month.getSelectedItem().equals("Pasirinkite mėnesį")))
               {
                 putData(month.getSelectedItem().toString(), fish.getSelectedItem().toString());
               }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }

        });

        month.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                fish.getSelectedItem();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }


    });


}
}
