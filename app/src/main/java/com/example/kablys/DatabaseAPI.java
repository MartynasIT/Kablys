package com.example.kablys;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.support.annotation.Nullable;

import java.util.ArrayList;
import java.util.List;


public class DatabaseAPI extends SQLiteOpenHelper {
    public static final String DatabaseName="kablys.db";
    public static final String TableName="Users";
    public static final String Col_1="ID";
    public static final String Col_2="Username";
    public static final String Col_3="Password";
    public static final String Col_4="Email";

    public DatabaseAPI(@Nullable Context context) {
        super(context, DatabaseName, null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        sqLiteDatabase.execSQL("Create Table Users (ID Integer PRIMARY KEY AUTOINCREMENT," +
                " Username Text, Password Text, Email Text)");
        sqLiteDatabase.execSQL("Create Table Locations (ID Integer PRIMARY KEY AUTOINCREMENT, User Text," +
                "Longitude Text, Latitude Text, Fish Text, Weight Text, Description Text, Image Blob)");

    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        sqLiteDatabase.execSQL(" DROP TABLE IF EXISTS " + TableName);
        onCreate(sqLiteDatabase);

    }
    public ArrayList<Object[]> getLocations(Object username) {
        ArrayList<Object[]> locations = new ArrayList<Object[]>();
        ArrayList<byte[]> images = new ArrayList<byte[]>();

        // Select All Query
        String selectQuery = "SELECT  * FROM " + "Locations";

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM Locations WHERE User = ?", new String[] {String.valueOf(username)});

        // Looping through all rows and adding to list
        if (cursor.moveToFirst()) {
            do {
                int id = cursor.getInt(cursor.getColumnIndex("ID"));
                String loni = cursor.getString(cursor.getColumnIndex("Longitude"));
                String lati = cursor.getString(cursor.getColumnIndex("Latitude"));
                String fish = cursor.getString(cursor.getColumnIndex("Fish"));
                String weight = cursor.getString(cursor.getColumnIndex("Weight"));
                String desription = cursor.getString(cursor.getColumnIndex("Description"));
                byte [] imgae = cursor.getBlob(cursor.getColumnIndex("Image"));
                locations.add( new Object[]{loni,lati,fish,weight,desription, imgae, id});

            } while (cursor.moveToNext());
        }

        // return student list
        db.close();
        cursor.close();
        return locations;
    }

    public void removeLocation (String username, int id){
        SQLiteDatabase db  = getReadableDatabase();
        db.delete("Locations", "ID=? and User=?", new String[]{Integer.toString(id), username});
        db.close();

    }

    public  long addLocation (Object user, String lat, String longi, String fish, String weitht, String description, byte[] image) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put("User", (String) user);
        contentValues.put("Longitude", longi);
        contentValues.put("Latitude", lat);
        contentValues.put("Fish", fish);
        contentValues.put("Weight", weitht);
        contentValues.put("Description", description);
        contentValues.put("Image", image);
        long result = db.insert("Locations", null, contentValues);
        db.close();
        return result;

    }

    public  long addUser (String user, String password, String email) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put("Username", user);
        contentValues.put("password", password);
        contentValues.put("Email", email);
        long result = db.insert(TableName, null, contentValues);
        db.close();
        return  result;

    }

    public String CheckUser (String username, String password){
        String email="";
        SQLiteDatabase db  = getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT Email FROM Users WHERE Username = ? AND Password = ?", new String[] {username, password});
        while (cursor.moveToNext())
        {
            email = cursor.getString(0);
        }
        cursor.close();
        db.close();

        return  email;
    }

    public boolean EmailExists (String email){
        String [] columns = {Col_1};
        SQLiteDatabase db  = getReadableDatabase();
        String selection = Col_4 + "=?";
        String [] select_args =  {email};
        Cursor cursor = db.query(TableName, columns, selection,
                select_args, null, null, null);
        int count = cursor.getCount();
        cursor.close();
        if(count > 0)
            return  true;
        else
            return  false;

    }

    public boolean UserExists (String username){
        String [] columns = {Col_1};
        SQLiteDatabase db  = getReadableDatabase();
        String selection = Col_2 + "=?";
        String [] select_args =  {username};
        Cursor cursor = db.query(TableName, columns, selection,
                select_args, null, null, null);
        int count = cursor.getCount();
        cursor.close();
        if(count > 0)
            return  true;
        else
            return  false;

    }


}
