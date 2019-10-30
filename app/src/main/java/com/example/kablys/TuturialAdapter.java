package com.example.kablys;

import android.app.Activity;
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

public class TuturialAdapter extends ArrayAdapter <String> {

    String [] names = new String[6];
    String [] ids = new String[6];

    private Activity context;

    public TuturialAdapter(@NonNull  Activity context, String [] names, String [] ids) {
        super(context, R.layout.fish_list_layout, names);
        this.context = context;
        this.names = names;
        this.ids = ids;
    }


    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {


        View r =convertView;
        ViewHolder viewHolder =null;
        if (r==null)
        {
            LayoutInflater inflater = context.getLayoutInflater();
            r = inflater.inflate(R.layout.tutorial_list_layout, null, true);
            viewHolder =  new ViewHolder(r);
            r.setTag(viewHolder);
        }
        else
        {
            viewHolder = (ViewHolder) r.getTag();
        }

        viewHolder.image.setImageResource(R.drawable.bulb);
        viewHolder.text1.setText(names[position]);
        return r;
    }


    class ViewHolder
    {
        TextView text1;
        ImageView image;
        ViewHolder(View view)
        {

            text1 = view.findViewById(R.id.tut_name);
            image = view.findViewById(R.id.tut_image);

        }
    }

}
