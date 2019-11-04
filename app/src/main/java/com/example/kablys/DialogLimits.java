package com.example.kablys;

import android.app.Dialog;
import android.content.Context;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;

import android.view.LayoutInflater;
import android.view.View;


import android.widget.TextView;


public class DialogLimits extends DialogFragment {

    private TextView  more;

    private Context ctx;

    public DialogLimits() {
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
        final View view = inflater.inflate(R.layout.layout_limits_dialog, null);
        more = view.findViewById(R.id.more_text_limits);
        more.setText(getString(R.string.moreLims));
        builder.setView(view);

                return builder.create();


    }




}
