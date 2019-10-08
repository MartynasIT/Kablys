package com.example.kablys;

import android.Manifest;
import android.app.Activity;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.StrictMode;
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
import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class DialogAccount extends DialogFragment {

    private EditText oldPasswd, newPasswd, comfPasswd;
    public Button submit;
    DatabaseAPI db;
    SessionManager Session;
    EmailSender gmail;

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        final AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = getActivity().getLayoutInflater();
        final View view = inflater.inflate(R.layout.layout_account_dialog, null);
        db = new DatabaseAPI(getContext());
        Session = new SessionManager(getContext());
        gmail = new EmailSender();
        oldPasswd = view.findViewById(R.id.old_passwd);
        newPasswd = view.findViewById(R.id.new_passwd);
        comfPasswd = view.findViewById(R.id.comf_passwd);
        submit = view.findViewById(R.id.btn_change);
        builder.setView(view);

        submit.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
                StrictMode.setThreadPolicy(policy); // kad leistu is vienu threado siusti mail
                boolean equals = false;
                String old = oldPasswd.getText().toString();
                String newp = newPasswd.getText().toString();
                String comf = comfPasswd.getText().toString();
                if (!old.isEmpty()) {
                    equals = db.comfirmPassword(makeMD5(old), (String) Session.get_username());
                    if (equals) {
                        if (newp.equals(comf) && !newp.isEmpty()) {
                            boolean result = db.ChangePassword(makeMD5(newp), (String) Session.get_username());
                            if (!result)
                                Toast.makeText(getContext(), "Sistemos klaida atliekant veiksmą!",
                                        Toast.LENGTH_SHORT).show();
                            else {
                                Toast.makeText(getContext(), "Pakeista!",
                                        Toast.LENGTH_SHORT).show();
                                gmail.sendChangedPasswd(Session.get_email(), newp, (String) Session.get_username());
                                Session.set_logged_in(false);
                                Intent Login = new Intent(getContext(), LoginActivicty.class);
                                startActivity(Login);
                            }
                        } else Toast.makeText(getContext(), "Nesutampa!",
                                Toast.LENGTH_SHORT).show();
                    }
                    else Toast.makeText(getContext(), "Blogas slaptažodis!",
                            Toast.LENGTH_SHORT).show();
                }
                else
                { Toast.makeText(getContext(), "Įveskite seną slaptažodį!",
                            Toast.LENGTH_SHORT).show();}

            }

        });

                return builder.create();
    }

    public String makeMD5(String passwd) {
        try {
            MessageDigest md5 = MessageDigest.getInstance("MD5");
            byte[] Digest = md5.digest(passwd.getBytes());
            BigInteger number = new BigInteger(1, Digest);
            String hash = number.toString(16);

            while (hash.length() < 32)
                hash = "0" + hash;
            return hash;
        } catch (NoSuchAlgorithmException e) {
            return null;
        }
    }

}
