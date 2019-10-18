package com.example.kablys;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.StrictMode;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class DialogFishInfo extends DialogFragment {

    private TextView fish, descr, bait, tips;
    private ImageView fish_pic;
    public  String name, des, baait, tiips;
    public byte [] image;
    DatabaseAPI db;

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        final AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = getActivity().getLayoutInflater();
        final View view = inflater.inflate(R.layout.layout_fish_dialog, null);
        db = new DatabaseAPI(getContext());
        fish = view.findViewById(R.id.fish_name);
        descr = view.findViewById(R.id.fis_info);
        bait = view.findViewById(R.id.fish_bait);
        tips = view.findViewById(R.id.fish_tips);
        fish_pic = view.findViewById(R.id.fish_info_image);
        builder.setView(view);
        fish.setText(name);
        descr.setText(des);
        bait.setText(baait);
        tips.setText(tiips);
        fish_pic.setImageBitmap(getImage(image));

                return builder.create();
    }

    public static Bitmap getImage(byte[] image) {
        return BitmapFactory.decodeByteArray(image, 0, image.length);
    }

}
