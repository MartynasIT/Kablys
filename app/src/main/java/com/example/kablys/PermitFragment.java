package com.example.kablys;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;


public class PermitFragment extends Fragment {
    TextView deletACC;
    TextView changePswd;
    Button add;
    Context ctx;
    DatabaseAPI db;
    SessionManager Session;

    public PermitFragment() {
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
        return inflater.inflate(R.layout.fragment_permit, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        deletACC = view.findViewById(R.id.deleteACC);
        changePswd = view.findViewById(R.id.changePasswd);
        Toolbar toolbar = getActivity().findViewById(R.id.toolbar);
        add =  toolbar.findViewById(R.id.add_permit);
        add.setVisibility(View.VISIBLE);

        add.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view) {
                DialogPermit dialog = new DialogPermit();
                dialog.show(getFragmentManager(), "Add permit");
            }
        });

    }


}
