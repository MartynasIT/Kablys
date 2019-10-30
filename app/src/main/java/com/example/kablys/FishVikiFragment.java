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
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.ArrayList;
import java.util.Iterator;


public class FishVikiFragment extends Fragment {
    TextView allPermits;
    TextView changePswd;
    ListView list;
    Button add;
    Context ctx;
    DatabaseAPI db;
    SessionManager Session;
    private ArrayList<Object[]> fishes = new ArrayList<Object[]>();
    String[] names = new String[9];
    String[] desriptions = new String[9];
    String[] tips = new String[9];
    String[] baits = new String[9];
    byte[][] bytesArray = new byte[9][];


    public FishVikiFragment() {
    }


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        ctx = context;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        db = new DatabaseAPI(ctx);
        Session = new SessionManager(ctx);

        fishes = db.getFishes();
        Iterator<Object[]> itr = fishes.iterator();
        while (itr.hasNext()) {
            Object[] array = itr.next();
            names[(int) array[5]] = (String) array[0];
            desriptions[(int) array[5]] = (String) array[1];
            tips[(int) array[5]] = (String) array[2];
            baits[(int) array[5]] = (String) array[3];
            bytesArray[(int) array[5]] = (byte[]) array[4];
        }
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle SavedInstanceState) {
        return inflater.inflate(R.layout.fragment_fish, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        list = view.findViewById(R.id.fishlist);
        FishAdapter adapter = new FishAdapter(getActivity(), names, desriptions, tips, baits, bytesArray);
        list.setAdapter(adapter);
        Toolbar toolbar = getActivity().findViewById(R.id.toolbar);
        add = toolbar.findViewById(R.id.add_permit);
        add.setVisibility(View.GONE);

        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapter, View v, int position, long arg3) {
                String value = (String) adapter.getItemAtPosition(position);
                int index = findIndex(names, value);
                DialogFishInfo dialog = new DialogFishInfo();
                dialog.name = names[index];
                dialog.baait = baits[index];
                dialog.des = desriptions[index];
                dialog.image = bytesArray[index];
                dialog.tiips = tips[index];
                dialog.show(getFragmentManager(), "Fish info");
            }
        });


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
