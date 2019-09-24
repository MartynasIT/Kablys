package com.example.kablys;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatDialogFragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.model.LatLng;

import static android.content.ContentValues.TAG;

public class DialogMap extends DialogFragment {

    private TextView btn_ok, btn_cancel;
    private EditText edit_fish, edit_weight, edit_descr;
    public Button butt;
    public LatLng latLng;
    DatabaseAPI db;
    SessionManager Session;


    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = getActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.layout_map_dialog, null);
        db = new DatabaseAPI(getContext());
        Session = new SessionManager(getContext());
        edit_fish = view.findViewById(R.id.map_fish);
        edit_weight = view.findViewById(R.id.map_weight);
        edit_descr = view.findViewById(R.id.map_description);
        builder.setView(view)
                .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                       String fish = edit_fish.getText().toString();
                       String descr = edit_descr.getText().toString();
                       String weight = edit_weight.getText().toString();
                       if (!fish.isEmpty())
                       {
                           db.addLocation(Session.get_username(),  String.valueOf(latLng.latitude),  String.valueOf(latLng.longitude),
                                   fish, weight, descr);
                       }

                    }
                });
                return builder.create();

    }



}
