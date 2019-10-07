package com.example.kablys;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;


public class AccountFragment extends Fragment {
    TextView deletACC;
    TextView changePswd;
    Context ctx;
    DatabaseAPI db;
    SessionManager Session;

    public AccountFragment() {
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
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle SavedInstanceState)
    {
        return inflater.inflate(R.layout.fragment_account, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        deletACC = view.findViewById(R.id.deleteACC);
        changePswd = view.findViewById(R.id.changePasswd);

        deletACC.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view) {

                new AlertDialog.Builder(ctx)
                        .setTitle("Dėmesio!")
                        .setMessage("Pašalinti Paskyrą?")
                        .setPositiveButton("Taip", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                db.removeAccount((String) Session.get_username());
                                Session.set_logged_in(false);
                                Session.set_username("");
                                Session.set_email("");
                                Intent LoginIntent = new Intent(ctx, LoginActivicty.class);
                                startActivity(LoginIntent);
                                dialog.cancel();
                            }
                        })
                        .setNegativeButton("Ne", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.cancel();
                            }
                        })
                        .setIcon(android.R.drawable.ic_dialog_alert)
                        .show();
        }
        });

        changePswd.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view) {
            DialogAccount dialog = new DialogAccount();
            dialog.show(getFragmentManager(), "Chnage Passwd");
            }
        });
    }


}
