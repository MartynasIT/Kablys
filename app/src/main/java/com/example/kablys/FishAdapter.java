package com.example.kablys;

import android.app.Activity;
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
import android.widget.Toast;

public class FishAdapter extends ArrayAdapter <String> {

    String [] names = new String[9];
    String [] desriptions = new String[9];
    String [] tips = new String[9];
    String [] baits = new String[9];
    byte[][] bytesArray = new byte[9][];
    private Activity context;

    public FishAdapter(@NonNull  Activity context,  String [] names, String [] descr, String [] tips, String [] baits, byte[][] bytesArray) {
        super(context, R.layout.fish_list_layout, names);
        this.context = context;
        this.names = names;
        this.desriptions = descr;
        this.tips = tips;
        this.baits = baits;
        this.bytesArray = bytesArray;

    }


    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {


        View r =convertView;
        ViewHolder viewHolder =null;
        if (r==null)
        {
            LayoutInflater inflater = context.getLayoutInflater();
            r = inflater.inflate(R.layout.fish_list_layout, null, true);
            viewHolder =  new ViewHolder(r);
            r.setTag(viewHolder);
        }
        else
        {
            viewHolder = (ViewHolder) r.getTag();
        }

        viewHolder.image.setImageBitmap(getImage(bytesArray[position]));
        viewHolder.text1.setText(names[position]);
        viewHolder.text2.setText(desriptions[position].substring(0, 78)+ "...");
        return r;
    }


    class ViewHolder
    {
        TextView text1;
        TextView text2;
        ImageView image;
        ViewHolder(View view)
        {

            text1 = view.findViewById(R.id.fish_name);
            text2 = view.findViewById(R.id.fish_descr);
            image = view.findViewById(R.id.fish_image);

        }
    }

    public static Bitmap getImage(byte[] image) {
        return BitmapFactory.decodeByteArray(image, 0, image.length);
    }
}
