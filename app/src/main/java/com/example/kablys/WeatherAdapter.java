package com.example.kablys;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;



import java.util.ArrayList;

/**
 * Created by Milind Amrutkar on 29-11-2017.
 */

public class WeatherAdapter extends ArrayAdapter<WeatherObject> {
    public WeatherAdapter(@NonNull Context context, ArrayList<WeatherObject> weatherArrayList) {
        super(context, 0, weatherArrayList);
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        WeatherObject weatherObject = getItem(position);

        if(convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.list_item, parent, false);
        }

        TextView dateTextView = convertView.findViewById(R.id.tvDate);
        TextView minTextView = convertView.findViewById(R.id.tvLowTemperature);
        TextView maxTextView = convertView.findViewById(R.id.tvHighTemperature);
        ImageView condition = convertView.findViewById(R.id.condition);


        dateTextView.setText(weatherObject.getDate());
        minTextView.setText(weatherObject.getMinTemp());
        maxTextView.setText(weatherObject.getMaxTemp());
        switch (weatherObject.getCondition())
        {
            case 1:
                condition.setImageResource(R.drawable.vienas);
                break;

            case 2:
                condition.setImageResource(R.drawable.du);
                break;
            case 3:
                condition.setImageResource(R.drawable.trys);
                break;
            case 4:
                condition.setImageResource(R.drawable.keturi);
                break;
            case 5:
                condition.setImageResource(R.drawable.penki);
                break;
            case 6:
                condition.setImageResource(R.drawable.sesi);
                break;
            case 7:
                condition.setImageResource(R.drawable.septyni);
                break;

            case 8:
                condition.setImageResource(R.drawable.astuoni);
                break;
            case 11:
                condition.setImageResource(R.drawable.vienolika);
                break;
            case 12:
                condition.setImageResource(R.drawable.dvylika);
                break;
            case 13:
                condition.setImageResource(R.drawable.trylika);
                break;
            case 14:
                condition.setImageResource(R.drawable.keturiolika);
                break;

            case 15:
                condition.setImageResource(R.drawable.penkiolika);
                break;
            case 16:
                condition.setImageResource(R.drawable.sesiolika);
                break;
            case 17:
                condition.setImageResource(R.drawable.septyniolika);
                break;
            case 18:
                condition.setImageResource(R.drawable.astuoniolika);
                break;
            case 19:
                condition.setImageResource(R.drawable.devyniolika);
                break;
            case 20:
                condition.setImageResource(R.drawable.dvidesimt);
                break;
            case 21:
                condition.setImageResource(R.drawable.dvidesimtvienas);
                break;
            case 22:
                condition.setImageResource(R.drawable.dvidesimtdu);
                break;
            case 23:
                condition.setImageResource(R.drawable.dvidesimttrys);
                break;
            case 24:
                condition.setImageResource(R.drawable.dvidesimtketuri);
                break;
            case 25:
                condition.setImageResource(R.drawable.dvidesimtpenki);
                break;
            case 26:
                condition.setImageResource(R.drawable.dvidesimtsesi);
                break;
            case 27:
                condition.setImageResource(R.drawable.dvidesimtdevyni);
                break;
            case 29:
                condition.setImageResource(R.drawable.dvidesimtdevyni);
                break;
            case 31:
                condition.setImageResource(R.drawable.tridesimtvienas);
                break;
            case 32:
                condition.setImageResource(R.drawable.tridesimtdu);
                break;
            case 39:
                condition.setImageResource(R.drawable.trisdesimtdevyni);
                break;
        }

        return convertView;

    }

}
