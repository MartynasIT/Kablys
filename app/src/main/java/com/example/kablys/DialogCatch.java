package com.example.kablys;

import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.model.LatLng;

public class DialogCatch extends DialogFragment {

    private TextView btn_ok, btn_cancel;
    private TextView edit_fish, edit_weight, edit_descr;
    public Button butt;
    public LatLng latLng;
    DatabaseAPI db;
    SessionManager Session;
    String fish, weight, descr;

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = getActivity().getLayoutInflater();
        final View view = inflater.inflate(R.layout.layout_catch_dialog, null);
        db = new DatabaseAPI(getContext());
        Session = new SessionManager(getContext());
        edit_fish = view.findViewById(R.id.map_fish);
        edit_weight = view.findViewById(R.id.map_weight);
        edit_descr = view.findViewById(R.id.map_description);
        edit_fish.setText(fish);
        edit_descr.setText(descr);
        edit_weight.setText(weight);
        builder.setView(view);


                return builder.create();


    }


}
