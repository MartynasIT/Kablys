package com.example.kablys;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.model.LatLng;

public class DialogViewInPond extends DialogFragment {


    private TextView fishes;
    public LatLng latLng;
    public String fish;
    public  String id;
    DatabaseAPI db;
    SessionManager Session;
    private Context ctx;

    public DialogViewInPond() {
    }


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        ctx=context;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = getActivity().getLayoutInflater();
        final View view = inflater.inflate(R.layout.layout_view_fishes_in_pond_dialog, null);
        db = new DatabaseAPI(getContext());
        Session = new SessionManager(ctx);
        fishes = view.findViewById(R.id.fishes_pond);
        fishes.setText(fish);

        builder.setPositiveButton("Gerai", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
            }

        });

        builder.setNegativeButton("Pašalinti", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                new AlertDialog.Builder(ctx)
                        .setTitle("Dėmesio!")
                        .setMessage("Pašalinti žuvis?")
                        .setPositiveButton("Taip", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                db.removePond((String) Session.get_username(), id);

                            }
                        })
                        .setNegativeButton("Ne", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                // nieko nedaryti
                            }
                        })
                        .setIcon(android.R.drawable.ic_dialog_alert)
                        .show();

            }

        });
        builder.setView(view);


                return builder.create();
    }


}
