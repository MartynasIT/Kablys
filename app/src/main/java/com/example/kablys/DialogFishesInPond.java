package com.example.kablys;

import android.Manifest;
import android.app.Activity;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.model.LatLng;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

public class DialogFishesInPond extends DialogFragment {


    private EditText fishes;
    public LatLng latLng;
    DatabaseAPI db;
    SessionManager Session;

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = getActivity().getLayoutInflater();
        final View view = inflater.inflate(R.layout.layout_fishes_in_pond_dialog, null);
        db = new DatabaseAPI(getContext());
        Session = new SessionManager(getContext());
        fishes = view.findViewById(R.id.fishes_pond);

        builder.setView(view);

                builder.setPositiveButton("Gerai", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                       String fish = fishes.getText().toString();

                       if (!fish.isEmpty())
                       {
                           long result = db.addFishesToPond(Session.get_username(),  String.valueOf(latLng.latitude),  String.valueOf(latLng.longitude),
                                   fish);

                           if (result <= 0)
                               Toast.makeText(view.getContext(), "Klaida!",
                                       Toast.LENGTH_SHORT).show();
                           else
                           {
                               dialogInterface.dismiss();
                           }
                       }
                       else  Toast.makeText(view.getContext(), "Įveskite tekstą!",
                               Toast.LENGTH_SHORT).show();
                    }

                });

                builder.setNegativeButton("Atšaukti", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.cancel();
                    }

                });


                return builder.create();
    }


}
