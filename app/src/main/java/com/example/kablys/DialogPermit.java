package com.example.kablys;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.model.LatLng;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class DialogPermit extends DialogFragment {

    private TextView urlText;
    private EditText expireDate, description;
    private Button button;
    private Spinner duration;
    private Spinner spinner_time;
    DatabaseAPI db;
    private  String startTime;
    private  String EndTime;
    private Context ctx;
    SessionManager Session;
    DatePickerDialog picker;
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
        final View view = inflater.inflate(R.layout.layout_permit_dialog, null);
        db = new DatabaseAPI(getContext());
        Session = new SessionManager(getContext());
        expireDate = view.findViewById(R.id.start_date);
        urlText = view.findViewById(R.id.text_permit);
        button = view.findViewById(R.id.upload_permit);
        spinner_time = (Spinner) view.findViewById(R.id.spinner_time);
        description = view.findViewById(R.id.permit_notes);
        duration = view.findViewById(R.id.spinner_duration);
        final Calendar myCalendar = Calendar.getInstance();
        builder.setView(view);
        expireDate.setInputType(InputType.TYPE_NULL);
        expireDate.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view) {

                final Calendar cldr = Calendar.getInstance();
                int day = cldr.get(Calendar.DAY_OF_MONTH);
                int month = cldr.get(Calendar.MONTH);
                int year = cldr.get(Calendar.YEAR);

                // date picker dialog
                picker = new DatePickerDialog(ctx,
                        new DatePickerDialog.OnDateSetListener() {
                            @Override
                            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                                expireDate.setText(dayOfMonth + "/" + (monthOfYear + 1) + "/" + year);
                            }
                        }, year, month, day);
                picker.show();
            }
        });


        urlText.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view) {

                String url = "https://alis.am.lt/actionFishingApplicationNew.action";
                Intent intent = new Intent(Intent.ACTION_VIEW).setData(Uri.parse(url));
                startActivity(intent);
            }
        });

        button.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view) {


             if (!String.valueOf(spinner_time.getSelectedItem()).equals("Pasirinkite laiką") &&
                     !String.valueOf(duration.getSelectedItem()).equals("Pasirinkite trukmę") &&
                     !expireDate.getText().toString().equals(""))
             {
                 startTime = expireDate.getText().toString() + " " +
                         String.valueOf(spinner_time.getSelectedItem());

                 if (String.valueOf(duration.getSelectedItem()).equals("Dvi dienos"))
                 {
                     SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyy hh:mm");
                     Calendar c = Calendar.getInstance();
                     try {
                         c.setTime(sdf.parse(startTime));
                     } catch (ParseException e) {
                         e.printStackTrace();
                     }
                     c.add(Calendar.DATE, 2);
                     sdf = new SimpleDateFormat("dd/MM/yyy hh:mm");
                     Date resultdate = new Date(c.getTimeInMillis());
                     EndTime = sdf.format(resultdate);
                     Toast.makeText(getContext(), EndTime,
                             Toast.LENGTH_SHORT).show();
                 }

                 else if (String.valueOf(duration.getSelectedItem()).equals("Mėnuo"))
                 {
                     SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyy hh:mm");
                     Calendar c = Calendar.getInstance();
                     try {
                         c.setTime(sdf.parse(EndTime));
                     } catch (ParseException e) {
                         e.printStackTrace();
                     }
                     c.add(Calendar.DATE, 30);
                     sdf = new SimpleDateFormat("dd/MM/yyy hh:mm");
                     Date resultdate = new Date(c.getTimeInMillis());
                     EndTime = sdf.format(resultdate);
                     Toast.makeText(getContext(), EndTime,
                             Toast.LENGTH_SHORT).show();
                 }

                 else if (String.valueOf(duration.getSelectedItem()).equals("Mėnuo"))
                 {
                     SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyy hh:mm");
                     Calendar c = Calendar.getInstance();
                     try {
                         c.setTime(sdf.parse(EndTime));
                     } catch (ParseException e) {
                         e.printStackTrace();
                     }
                     c.add(Calendar.DATE, 30);
                     sdf = new SimpleDateFormat("dd/MM/yyy hh:mm");
                     Date resultdate = new Date(c.getTimeInMillis());
                     EndTime = sdf.format(resultdate);
                     Toast.makeText(getContext(), EndTime,
                             Toast.LENGTH_SHORT).show();
                 }

                 else if (String.valueOf(duration.getSelectedItem()).equals("Metai"))
                 {
                     SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyy hh:mm");
                     Calendar c = Calendar.getInstance();
                     try {
                         c.setTime(sdf.parse(EndTime));
                     } catch (ParseException e) {
                         e.printStackTrace();
                     }
                     c.add(Calendar.DATE, 365);
                     sdf = new SimpleDateFormat("dd/MM/yyy hh:mm");
                     Date resultdate = new Date(c.getTimeInMillis());
                     EndTime = sdf.format(resultdate);
                     Toast.makeText(getContext(), EndTime,
                             Toast.LENGTH_SHORT).show();
                 }



             }
             else
                 Toast.makeText(getContext(), "Pasirinkite laiką!",
                         Toast.LENGTH_SHORT).show();
            }
        });

                return builder.create();


    }

    private void putPermit()
    {
        String notes = "";
        notes = description.getText().toString();

        db.addPermit(Session.get_username(), startTime, EndTime, notes );

    }


    private  void pickDate(final Calendar myCalendar)
    {
        DatePickerDialog.OnDateSetListener date = new DatePickerDialog.OnDateSetListener() {

            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear,
                                  int dayOfMonth) {
                // TODO Auto-generated method stub
                myCalendar.set(Calendar.YEAR, year);
                myCalendar.set(Calendar.MONTH, monthOfYear);
                myCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                updateLabel(myCalendar);
            }

        };
    }

    private void updateLabel(Calendar myCalendar) {
        String myFormat = "MM/dd/yy"; //In which you need put here
        SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.ITALIAN);
        expireDate.setText(sdf.format(myCalendar.getTime()));
    }



}
