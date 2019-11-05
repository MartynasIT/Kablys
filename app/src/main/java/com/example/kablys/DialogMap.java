package com.example.kablys;

import android.Manifest;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatDialogFragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.model.LatLng;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

import static android.content.ContentValues.TAG;

public class DialogMap extends DialogFragment {

    private TextView btn_ok, btn_cancel;
    private EditText edit_fish, edit_weight, edit_descr;
    public Button button_pic;
    public ImageView imageView;
    public LatLng latLng;
    DatabaseAPI db;
    SessionManager Session;
    private static final int GALLERY_REQUEST_CODE = 100;
    private static final int PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE = 101;
    private Uri selectedImage;
    private Bitmap bitmap;
    private  byte[] image;


    private void getFilePerms()
    {
        ActivityCompat.requestPermissions(getActivity(),
                new String[]{
                        Manifest.permission.READ_EXTERNAL_STORAGE
                },
                PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE);
    }

    public static byte[] getImageBits(Bitmap bitmap) {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 50, outputStream);
        return outputStream.toByteArray();
    }


    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = getActivity().getLayoutInflater();
        final View view = inflater.inflate(R.layout.layout_map_dialog, null);
        db = new DatabaseAPI(getContext());
        Session = new SessionManager(getContext());
        edit_fish = view.findViewById(R.id.map_fish);
        edit_weight = view.findViewById(R.id.map_weight);
        edit_descr = view.findViewById(R.id.map_description);
        button_pic = view.findViewById(R.id.upload_pic);
        imageView = view.findViewById(R.id.photo);
        imageView.setImageResource(R.drawable.no_image);
        getFilePerms();
        builder.setView(view);

                builder.setPositiveButton("Gerai", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                       String fish = edit_fish.getText().toString();
                       String descr = edit_descr.getText().toString();
                       String weight = edit_weight.getText().toString();
                       if (!fish.isEmpty())
                       {
                           long result = db.addLocation(Session.get_username(),  String.valueOf(latLng.latitude),  String.valueOf(latLng.longitude),
                                   fish, weight, descr, image);

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

        button_pic.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                Intent photoPickerIntent = new Intent(Intent.ACTION_PICK);
                photoPickerIntent.setType("image/*");
                startActivityForResult(photoPickerIntent, GALLERY_REQUEST_CODE);

            }

        });

                return builder.create();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == Activity.RESULT_OK)
            switch (requestCode){
                case GALLERY_REQUEST_CODE:
                    selectedImage = data.getData();
                    try
                    {bitmap = MediaStore.Images.Media.getBitmap(getActivity().getContentResolver(), selectedImage);}
                    catch (FileNotFoundException e)
                    {

                    }
                    catch (IOException e)
                    {

                    }

                    if (bitmap != null)
                    image = getImageBits(bitmap);
                    imageView.setImageURI(selectedImage);

            }
    }

}
