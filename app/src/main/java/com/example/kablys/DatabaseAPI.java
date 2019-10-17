package com.example.kablys;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
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

        sqLiteDatabase.execSQL("Create Table Permits (ID Integer PRIMARY KEY AUTOINCREMENT, User Text," +
                "StartTime Text, EndTIme Text, Notes Text, Notified Text)");

        sqLiteDatabase.execSQL("Create Table ForbiddenPlaces (ID Integer PRIMARY KEY AUTOINCREMENT, long Text," +
                "lat Text, Name Text)");


    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        sqLiteDatabase.execSQL(" DROP TABLE IF EXISTS " + TableName);
        onCreate(sqLiteDatabase);

    }


    public void  UpdatePermit (Object username, String endTime){
        SQLiteDatabase db  = getReadableDatabase();
        ContentValues cv = new ContentValues();
        cv.put("Notified","1");
        try {
            db.update("Permits", cv, "User=? and EndTIme=?", new String[] {(String) username, endTime});
        } catch (SQLException e) {
        }
        cv.clear();
        db.close();

    }

    public ArrayList<String[]> getForbiddenLocations() {
        ArrayList<String[]> forbidden = new ArrayList<String[]>();

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM ForbiddenPlaces", null);

        // Looping through all rows and adding to list
        if (cursor.moveToFirst()) {
            do {
                String longi = cursor.getString(cursor.getColumnIndex("long"));
                String lat = cursor.getString(cursor.getColumnIndex("lat"));
                String name = cursor.getString(cursor.getColumnIndex("Name"));
                forbidden.add( new String[]{longi, lat, name});

            } while (cursor.moveToNext());
        }

        db.close();
        cursor.close();
        return forbidden;
    }

    public ArrayList<String[]> getPermits(Object username) {
        ArrayList<String[]> permits = new ArrayList<String[]>();

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM Permits WHERE User = ?", new String[] {String.valueOf(username)});

        // Looping through all rows and adding to list
        if (cursor.moveToFirst()) {
            do {
                String start = cursor.getString(cursor.getColumnIndex("StartTime"));
                String end = cursor.getString(cursor.getColumnIndex("EndTIme"));
                String notes = cursor.getString(cursor.getColumnIndex("Notes"));
                String notified = cursor.getString(cursor.getColumnIndex("Notified"));

                permits.add( new String[]{start, end, notes, notified});

            } while (cursor.moveToNext());
        }

        db.close();
        cursor.close();
        return permits;
    }

    public  void removePermits(String endTime, Object username)
    {
        SQLiteDatabase db  = getReadableDatabase();
        db.delete("Permits", "EndTime=? and User=?", new String[]{endTime, (String) username});
        db.close();
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

    public void  addPermit (Object user, String start_Date, String end_Date, String notes){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put("User", (String) user);
        contentValues.put("StartTime", start_Date);
        contentValues.put("EndTime", end_Date);
        contentValues.put("Notes", notes);
        contentValues.put("Notified", "0");
        db.insert("Permits", null, contentValues);
        db.close();
    }


    public void removeLocation (String username, int id){
        SQLiteDatabase db  = getReadableDatabase();
        db.delete("Locations", "ID=? and User=?", new String[]{Integer.toString(id), username});
        db.close();

    }
    public boolean comfirmPassword (String passwd, String username){
        String oldPasswd = "";
        SQLiteDatabase db  = getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT Password FROM Users WHERE Username = ?", new String[] {username});
        while (cursor.moveToNext())
        {
            oldPasswd = cursor.getString(0);
        }
        cursor.close();
        db.close();
        if (oldPasswd.equals(passwd))

        return  true;
        else
            return false;

    }

    public boolean  ChangePassword (String passwd, String username){
        SQLiteDatabase db  = getReadableDatabase();
        ContentValues cv = new ContentValues();
        boolean result = true;
        cv.put("Password",passwd); //These Fields should be your String values of actual column names
        try {
            db.update(TableName, cv, "Username=?", new String[] {username});
        } catch (SQLException e) {
            result = false;
        }
        cv.clear();
        db.close();
        return result;
    }


    public void removeAccount (String username){
        SQLiteDatabase db  = getReadableDatabase();
        db.delete("Locations", "User=?", new String[]{username});
        db.delete("Users", "Username=?", new String[]{username});
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


    public void addForbiddenLocations() {

        SQLiteDatabase db = this.getWritableDatabase();
            ContentValues c1 = new ContentValues();
            ContentValues c2 = new ContentValues();
            c1.put("long", "55.367950");
            c1.put("lat", "21.330580");
            c1.put("Name", "Krokų lankos botaninis–zoologinis draustinis");
            db.insert("ForbiddenPlaces", null, c1);
            c2.put("long", "54.472722");
            c2.put("lat", "23.641855");
            c2.put("Name", "Žuvinto biosferos rezervatas");
            db.insert("ForbiddenPlaces", null, c2);
            db.close();
        }


}
