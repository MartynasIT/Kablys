package com.example.kablys;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.maps.model.LatLng;

public class DialogPermit extends DialogFragment {

    private TextView btn_ok, btn_cancel;
    private TextView edit_fish, edit_weight, edit_descr;
    public Button butt;
    public LatLng latLng;
    DatabaseAPI db;
    private Context ctx;
    SessionManager Session;
    String fish, weight, descr;
    public ImageView imageView;
    byte [] image;
    public int markerID;

    public DialogPermit() {
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
        final View view = inflater.inflate(R.layout.layout_catch_dialog, null);
        db = new DatabaseAPI(getContext());
        Session = new SessionManager(getContext());
        edit_fish = view.findViewById(R.id.map_fish);
        edit_weight = view.findViewById(R.id.map_weight);
        edit_descr = view.findViewById(R.id.map_description);
        edit_fish.setText(fish);
        edit_descr.setText(descr);
        edit_weight.setText(weight);
        imageView = view.findViewById(R.id.show_pic);
        if (image != null)
        {imageView.setImageBitmap(getImage(image));}
        else  imageView.setImageResource(R.drawable.no_image);
        builder.setView(view);

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
                        .setMessage("Pašalinti pagautą žuvį?")
                        .setPositiveButton("Taip", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        db.removeLocation((String) Session.get_username(), markerID);
                        MapFragment map = new MapFragment();
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


                return builder.create();


    }

    public static Bitmap getImage(byte[] image) {
        return BitmapFactory.decodeByteArray(image, 0, image.length);
    }



}
