package com.example.kablys;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.List;


public class DatabaseAPI extends SQLiteOpenHelper {
    public static final String DatabaseName="kablys.db";
    public static final String TableName="Users";
    public static final String Col_1="ID";
    public static final String Col_2="Username";
    public static final String Col_3="Password";
    public static final String Col_4="Email";
    private Context context;
    private Bitmap bitmap;
    private  byte[] image;

    public DatabaseAPI(@Nullable Context context) {
        super(context, DatabaseName, null, 1);
        this.context=context;
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        sqLiteDatabase.execSQL("Create Table Users (ID Integer PRIMARY KEY," +
                " Username Text, Password Text, Email Text)");
        sqLiteDatabase.execSQL("Create Table Locations (ID Integer PRIMARY KEY AUTOINCREMENT, User Text," +
                "Longitude Text, Latitude Text, Fish Text, Weight Text, Description Text, Image Blob)");

        sqLiteDatabase.execSQL("Create Table Permits (ID Integer PRIMARY KEY AUTOINCREMENT, User Text," +
                "StartTime Text, EndTIme Text, Notes Text, Notified Text)");

        sqLiteDatabase.execSQL("Create Table ForbiddenPlaces (ID Integer PRIMARY KEY AUTOINCREMENT, long Text," +
                "lat Text, Name Text)");

        sqLiteDatabase.execSQL("Create Table Fishes (ID Integer PRIMARY KEY AUTOINCREMENT, Fish Text," +
                "Description Text, Tips Text, Bait Text, Image Blob)");

        sqLiteDatabase.execSQL("Create Table FishesinPond (ID Integer PRIMARY KEY AUTOINCREMENT, User Text, Fishes Text, " +
                "Longitude Text, Latitude Text, Pond Text)");

        sqLiteDatabase.execSQL("Create Table Calendar (ID Integer PRIMARY KEY AUTOINCREMENT, Fish Text, Month Text, " +
                "Bait Text, Gear Text, Bite Text, Forbid Text)");

        sqLiteDatabase.execSQL("Create Table Challenges (ID Integer PRIMARY KEY AUTOINCREMENT, Challenge Text)");

        addFish(sqLiteDatabase);
        addForbiddenLocations(sqLiteDatabase);
        addCalendar(sqLiteDatabase);
        addChallenge(sqLiteDatabase);


    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        sqLiteDatabase.execSQL(" DROP TABLE IF EXISTS " + TableName);
        onCreate(sqLiteDatabase);

    }

    public ArrayList<Object[]> getFishes() {
        ArrayList<Object[]> fishes = new ArrayList<Object[]>();

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM Fishes", null);

        if (cursor.moveToFirst()) {
            do {
                int id = cursor.getInt(cursor.getColumnIndex("ID"));
                String fish = cursor.getString(cursor.getColumnIndex("Fish"));
                String Description = cursor.getString(cursor.getColumnIndex("Description"));
                String Tips = cursor.getString(cursor.getColumnIndex("Tips"));
                String Bait = cursor.getString(cursor.getColumnIndex("Bait"));
                byte [] Image = cursor.getBlob(cursor.getColumnIndex("Image"));
                fishes.add( new Object[]{fish,Description,Tips,Bait, Image, id});

            } while (cursor.moveToNext());
        }

        // return student list
        db.close();
        cursor.close();
        return fishes;
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

    public ArrayList getChallenges() {
        ArrayList challenges = new ArrayList();

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM Challenges", null);

        if (cursor.moveToFirst()) {
            do {
                String challenge = cursor.getString(cursor.getColumnIndex("Challenge"));
                challenges.add(challenge);

            } while (cursor.moveToNext());
        }

        db.close();
        cursor.close();
        return challenges;
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

    public  void removePond(Object username, String id)
    {
        SQLiteDatabase db  = getReadableDatabase();
        db.delete("FishesinPond", "ID=? and User=?", new String[]{id, (String) username});
        db.close();
    }


    public  void removePermits(String endTime, Object username)
    {
        SQLiteDatabase db  = getReadableDatabase();
        db.delete("Permits", "EndTime=? and User=?", new String[]{endTime, (String) username});
        db.close();
    }

    public ArrayList<String[]> getCalendar() {
        ArrayList<String[]> calendar = new ArrayList<String[]>();

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM Calendar", null);

        if (cursor.moveToFirst()) {
            do {
                String fish = cursor.getString(cursor.getColumnIndex("Fish"));
                String month = cursor.getString(cursor.getColumnIndex("Month"));
                String bite = cursor.getString(cursor.getColumnIndex("Bite"));
                String gear = cursor.getString(cursor.getColumnIndex("Gear"));
                String Bait = cursor.getString(cursor.getColumnIndex("Bait"));
                String forbid = cursor.getString(cursor.getColumnIndex("Forbid"));
                calendar.add( new String[]{fish,month,bite, gear, Bait, forbid });

            } while (cursor.moveToNext());
        }

        // return student list
        db.close();
        cursor.close();
        return calendar;
    }


    public ArrayList<String[]> getFishesInPond(Object username) {
        ArrayList<String[]> fishes = new ArrayList<String[]>();

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM FishesinPond WHERE User = ?", new String[] {String.valueOf(username)});

        // Looping through all rows and adding to list
        if (cursor.moveToFirst()) {
            do {
                int id = cursor.getInt(cursor.getColumnIndex("ID"));
                String loni = cursor.getString(cursor.getColumnIndex("Longitude"));
                String lati = cursor.getString(cursor.getColumnIndex("Latitude"));
                String fish = cursor.getString(cursor.getColumnIndex("Fishes"));
                String pond = cursor.getString(cursor.getColumnIndex("Pond"));
                fishes.add( new String[]{loni,lati,fish, pond, Integer.toString(id)});

            } while (cursor.moveToNext());
        }

        // return student list
        db.close();
        cursor.close();
        return fishes;
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

    public  long addFishesToPond (Object user, String lat, String longi, String fish) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put("User", (String) user);
        contentValues.put("Longitude", longi);
        contentValues.put("Latitude", lat);
        contentValues.put("Fishes", fish);
        contentValues.put("Pond", "1");
        long result = db.insert("FishesinPond", null, contentValues);
        db.close();
        return result;

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


    public void addForbiddenLocations(SQLiteDatabase db) {
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

        }

    public void addChallenge(SQLiteDatabase db) {

        ContentValues c1 = new ContentValues();
        ContentValues c2 = new ContentValues();
        ContentValues c3 = new ContentValues();
        ContentValues c4 = new ContentValues();
        ContentValues c5 = new ContentValues();
        ContentValues c6 = new ContentValues();
        ContentValues c7 = new ContentValues();
        ContentValues c8 = new ContentValues();
        ContentValues c9 = new ContentValues();
        ContentValues c10 = new ContentValues();

        c1.put("Challenge", "Pagauk 1kg Lydeką, naudojant meškerę");
        db.insert("Challenges", null, c1);
        c2.put("Challenge", "Pagauk 0,5kg Karpį, naudojant slieką");
        db.insert("Challenges", null, c2);
        c3.put("Challenge", "Pagauk 2,5kg Šamą, naudojant dirbtini jauką");
        db.insert("Challenges", null, c3);
        c4.put("Challenge", "Pagauk 1kg žuvį Neryje");
        db.insert("Challenges", null, c4);
        c5.put("Challenge", "Pagauk 5kg žuvį ežere");
        db.insert("Challenges", null, c5);
        c6.put("Challenge", "Pagauk 1 kuoja");
        db.insert("Challenges", null, c6);
        c7.put("Challenge", "Pagauk 2 karpius");
        db.insert("Challenges", null, c7);
        c8.put("Challenge", "Pagauk 2 lydekas");
        db.insert("Challenges", null, c8);
        c9.put("Challenge", "Pagauk kažką");
        db.insert("Challenges", null, c9);
        c10.put("Challenge", "Pagauk 2 ešerius");
        db.insert("Challenges", null, c10);
    }


    public void addCalendar(SQLiteDatabase db) {
        ContentValues c1 = new ContentValues();
        ContentValues c2 = new ContentValues();
        ContentValues c3 = new ContentValues();
        ContentValues c4 = new ContentValues();
        ContentValues c5 = new ContentValues();
        ContentValues c6 = new ContentValues();
        ContentValues c7 = new ContentValues();
        ContentValues c8 = new ContentValues();
        ContentValues c9 = new ContentValues();
        ContentValues c10 = new ContentValues();
        ContentValues c11 = new ContentValues();
        ContentValues c12 = new ContentValues();
        ContentValues c13 = new ContentValues();
        ContentValues c14 = new ContentValues();
        ContentValues c15 = new ContentValues();
        ContentValues c16 = new ContentValues();
        ContentValues c17 = new ContentValues();
        ContentValues c18 = new ContentValues();
        ContentValues c19 = new ContentValues();
        ContentValues c20 = new ContentValues();
        ContentValues c21 = new ContentValues();
        ContentValues c22 = new ContentValues();
        ContentValues c23 = new ContentValues();
        ContentValues c24 = new ContentValues();
        ContentValues c25 = new ContentValues();
        
        c1.put("Fish", "Aukšlė");
        c1.put("Forbid", "nėra");
        c1.put("Month", "rugsėjis");
        c1.put("Gear", "plūdine meškere");
        c1.put("Bait", "musė, musės lerva, sliekas, apsiuva, miltinė tešla, duona");
        c1.put("Bite", "vidutinis");
        db.insert("Calendar", null, c1);
        c2.put("Fish", "Ešerys");
        c2.put("Forbid", "nėra");
        c2.put("Month", "rugsėjis");
        c2.put("Gear", "plūdine ir dugnine meškere, spiningu");
        c2.put("Bait", "sliekas, vabzdžių lervos, apsiuva, šoniplauka, dėle, blizgė, mikromasalai");
        c2.put("Bite", "geras");
        db.insert("Calendar", null, c2);
        c3.put("Fish", "Gružlys");
        c3.put("Forbid", "nėra");
        c3.put("Month", "rugsėjis");
        c3.put("Gear", "plūdine ir dugnine meškere, palaidyne");
        c3.put("Bait", "ssliekas, vabzdžių lervos, apsiuva, dėle");
        c3.put("Bite", "geras");
        db.insert("Calendar", null, c3);
        c4.put("Fish", "Karpis");
        c4.put("Forbid", "nėra");
        c4.put("Month", "rugsėjis");
        c4.put("Gear", "plūdine meškere, dugnine meškere");
        c4.put("Bait", "sliekas, vabzdžių lervos, apsiuva");
        c4.put("Bite", "vidutinis");
        db.insert("Calendar", null, c4);
        c5.put("Fish", "Karosas");
        c5.put("Forbid", "nėra");
        c5.put("Month", "rugsėjis");
        c5.put("Gear", "plūdine meškere");
        c5.put("Bait", "sliekas, vabzdžių lervos, apsiuva");
        c5.put("Bite", "vidutinis");
        db.insert("Calendar", null, c5);
        c6.put("Fish", "Karšis");
        c6.put("Forbid", "nėra");
        c6.put("Month", "rugsėjis");
        c6.put("Gear", "plūdine meškere, dugnine meškere");
        c6.put("Bait", "sliekas, apsiuva, žirniai, kukurūzai");
        c6.put("Bite", "vidutinis");
        db.insert("Calendar", null, c6);
        c7.put("Fish", "Kiršlys");
        c7.put("Forbid", "draudžiama gaudyti natūralios kilmės masalais");
        c7.put("Month", "rugsėjis");
        c7.put("Gear", "museline meškere");
        c7.put("Bait", "dirbtinės muselės");
        c7.put("Bite", "geras");
        db.insert("Calendar", null, c7);
        c8.put("Fish", "Kuoja");
        c8.put("Forbid", "nėra");
        c8.put("Month", "rugsėjis");
        c8.put("Gear", "plūdine meškere, palaidyne");
        c8.put("Bait", "sliekas, musė ir jos lerva, apsiuva, dėlė, miltinė tešla, duona");
        c8.put("Bite", "prastas");
        db.insert("Calendar", null, c8);

        c9.put("Fish", "Ešerys");
        c9.put("Forbid", "nra");
        c9.put("Month", "gruodis");
        c9.put("Gear", "žiemine meškerėle");
        c9.put("Bait", "sliekas, apsiuva, uodo trūklio lerva, blizgė, mikromasalai, avižėlė");
        c9.put("Bite", "geras");
        db.insert("Calendar", null, c9);
        c10.put("Fish", "Karšis");
        c10.put("Forbid", "nėra");
        c10.put("Month", "gruodis");
        c10.put("Gear", "dugnine ir žiemine meškerėle");
        c10.put("Bait", "sliekas, apsiuva, uodo trūklio lerva, avižėlė");
        c10.put("Bite", "prastas");
        db.insert("Calendar", null, c10);
        c11.put("Fish", "Kuoja");
        c11.put("Forbid", "nėra");
        c11.put("Month", "gruodis");
        c11.put("Gear", "žiemine meškerėle, plūdine meškere");
        c11.put("Bait", "sliekas, musės lerva, miltinė tešla, avižėlė");
        c11.put("Bite", "vidutinis");
        db.insert("Calendar", null, c11);

        c12.put("Fish", "Aukšlė");
        c12.put("Forbid", "nėra");
        c12.put("Month", "lapkritis");
        c12.put("Gear", "plūdine meškere");
        c12.put("Bait", "musės lerva, sliekas");
        c12.put("Bite", "prastas");
        db.insert("Calendar", null, c12);
        c13.put("Fish", "Ešerys");
        c13.put("Forbid", "nėra");
        c13.put("Month", "lapkritis");
        c13.put("Gear", "plūdine ir dugnine meškere, spiningu");
        c13.put("Bait", "sliekas, vabzdžių lervos, apsiuva, šoniplauka, blizgė, mikromasalai, avižėlė");
        c13.put("Bite", "geras");
        db.insert("Calendar", null, c13);
        c14.put("Fish", "Kuoja");
        c14.put("Forbid", "nėra");
        c14.put("Month", "lapkritis");
        c14.put("Gear", "plūdine meškere, palaidyne");
        c14.put("Bait", "sliekas, musės lerva, šoniplauka, uodo trūklio lerva, miltinė tešla, avižėlė");
        c14.put("Bite", "geras");
        db.insert("Calendar", null, c14);

        c15.put("Fish", "Aukšlė");
        c15.put("Forbid", "nėra");
        c15.put("Month", "spalis");
        c15.put("Gear", "plūdine meškere");
        c15.put("Bait", "musė, musės lerva, sliekas, apsiuva, miltinė tešla, duona");
        c15.put("Bite", "vidutinis");
        db.insert("Calendar", null, c15);
        c16.put("Fish", "Ešerys");
        c16.put("Forbid", "nėra");
        c16.put("Month", "spalis");
        c16.put("Gear", "plūdine ir dugnine meškere, spiningu");
        c16.put("Bait", "sliekas, vabzdžių lervos, apsiuva, šoniplauka, dėle, blizgė, mikromasalai");
        c16.put("Bite", "geras");
        db.insert("Calendar", null, c16);
        c17.put("Fish", "Karpis");
        c17.put("Forbid", "nėra");
        c17.put("Month", "spalis");
        c17.put("Gear", "plūdine meškere, dugnine meškere");
        c17.put("Bait", "sliekas, vabzdžių lervos, apsiuva");
        c17.put("Bite", "prastas");
        db.insert("Calendar", null, c17);
        c18.put("Fish", "Karosas");
        c18.put("Forbid", "nėra");
        c18.put("Month", "spalis");
        c18.put("Gear", "plūdine meškere");
        c18.put("Bait", "sliekas, vabzdžių lervos, apsiuva");
        c18.put("Bite", "prastas");
        db.insert("Calendar", null, c18);
        c19.put("Fish", "Karšis");
        c19.put("Forbid", "nėra");
        c19.put("Month", "spalis");
        c19.put("Gear", "plūdine meškere, dugnine meškere");
        c19.put("Bait", "sliekas, apsiuva, žirniai, kukurūzai");
        c19.put("Bite", "prastas");
        db.insert("Calendar", null, c19);
        c20.put("Fish", "Kuoja");
        c20.put("Forbid", "nėra");
        c20.put("Month", "spalis");
        c20.put("Gear", "plūdine meškere, palaidyne");
        c20.put("Bait", "sliekas, musė ir jos lerva, apsiuva");
        c20.put("Bite", "prastas");
        db.insert("Calendar", null, c20);

        c21.put("Fish", "Karpis");
        c21.put("Forbid", "nėra");
        c21.put("Month", "rugpjūtis");
        c21.put("Gear", "plūdine meškere");
        c21.put("Bait", "musė, musės lerva, sliekas, apsiuva, miltinė tešla, duona");
        c21.put("Bite", "vidutinis");
        db.insert("Calendar", null, c21);
        c22.put("Fish", "Aukšlė");
        c22.put("Forbid", "nėra");
        c22.put("Month", "rugpjūtis");
        c22.put("Gear", "plūdine meškere, dugnine meškere");
        c22.put("Bait", "sliekas, vabzdžių lervos, apsiuva");
        c22.put("Bite", "geras");
        db.insert("Calendar", null, c22);
        c23.put("Fish", "Karosas");
        c23.put("Forbid", "nėra");
        c23.put("Month", "rugpjūtis");
        c23.put("Gear", "plūdine meškere");
        c23.put("Bait", "sliekas, vabzdžių lervos, apsiuva");
        c23.put("Bite", "geras");
        db.insert("Calendar", null, c23);
        c24.put("Fish", "Kiršlys");
        c24.put("Forbid", "draudžiama gaudyti natūralios kilmės masalais");
        c24.put("Month", "rugpjūtis");
        c24.put("Gear", "museline meškere");
        c24.put("Bait", "dirbtinės muselės");
        c24.put("Bite", "prastas");
        db.insert("Calendar", null, c24);

        c25.put("Fish", "Aukšlė");
        c25.put("Forbid", "draudžiama gaudyti natūralios kilmės masalais");
        c25.put("Month", "liepa");
        c25.put("Gear", "plūdine meškere");
        c25.put("Bait", "musė, musės lerva, sliekas, apsiuva, miltinė tešla, duona");
        c25.put("Bite", "prastas");
        db.insert("Calendar", null, c25);
    }
    public void addFish(SQLiteDatabase db) {

        ContentValues c1 = new ContentValues();
        ContentValues c2 = new ContentValues();
        ContentValues c3 = new ContentValues();
        ContentValues c4 = new ContentValues();
        ContentValues c5 = new ContentValues();
        ContentValues c6 = new ContentValues();
        ContentValues c7 = new ContentValues();
        ContentValues c8 = new ContentValues();
        ContentValues c9 = new ContentValues();

        c1.put("ID", 0);
        c1.put("Fish", "Lynas");
        c1.put("Description", "Kūnas kresnas, labai gleivėtas. Uodegos stiebelis trumpas. Nugara juodai žalsva, šonai žalsvai auksinės spalvos, papilvė pilkšva arba gelsva. Akys mažos, raudonos. Žiotys mažos, galinės, jų kampuos yra po trumpą ūselį. Žvynai pailgi, ploni, smulkūs, giliai įaugę į gana storą odą. Pelekai apvaliais pakraščiais, tamsūs. Gyvena nedideliais tuntais. Dėl jo atsargumo bei apdairumo, lynas atrodo lėtas ir nerangus.");
        c1.put("Tips", "Dažna verslinė žuvis, geriausiai kimba rugpjūčio mėnesį. Sveriantis virš 1 kg labai stiprus ir užkibęs ant kabliuko galingai priešinasi. Daugiausia lynų sugaunama vasarą, ypač jų neršto metu – birželio ir liepos mėnesiais");
        c1.put("Bait", "Raudonos musės lervos, Musės lervų kokonai, Baltyminiai kukuliai, Konservuoti kukurūzai");
        Drawable drawable = ContextCompat.getDrawable(context, R.drawable.lynas);
        Bitmap bitmap = ((BitmapDrawable)drawable).getBitmap();
        c1.put("Image", getImageBits(bitmap));
        db.insert("Fishes", null, c1);
        c2.put("ID", 1);
        c2.put("Fish", "Paprastoji saulažuvė");
        c2.put("Description", "Nedidelė, panaši į aukšlę žuvis. Kūnas 6-8 cm. Nugara žalsva, šonai sidabriški. Šoninė linija nepilna, apima 2-12 žvynų. Pelekai šviesūs, bespalviai. Apatinis žandas užsirietęs į viršų.");
        c2.put("Description", "Gyvena stovinčiame arba lėtai tekančiame vandenyje – kai kuriuose ežeruose, Nemuno žemupyje, Kuršių marių įlankose, tvenkiniuose. Laikosi būriais užžėlusiuose vandens telkinių pakraščiuose.");
        c2.put("Tips", "Saulažuvės minta zooplanktonu, dumbliais, lervomis, vabzdžiais. Pačios yra daugelio plėšrių žuvų grobis. Neršia gegužės–balandžio mėnesiais (ant augalų, kitų paviršių)." );
        c2.put("Bait", "Įprastai saulažuvės gaudomos nedideliais graibštais su tankiu tinklu. Užtenka sudrumsti vandenį ir saulažuvių būrys be jokios baimės puola į jį.");
        drawable = ContextCompat.getDrawable(context, R.drawable.saulazuve);
        bitmap = ((BitmapDrawable)drawable).getBitmap();
        c2.put("Image", getImageBits(bitmap));
        db.insert("Fishes", null, c2);
        c3.put("ID", 2);
        c3.put("Fish", "Ešerys");
        c3.put("Description", "aukštas, kiek kuprotas, apaugęs kietais, smulkiais, dantytais pakraščiais, giliai į odą įaugusiais žvynais. Nugara tamsiai žalsva, šonai žaliai gelsvi, senų ešerių - gelsvi, su 5-9 skersiniais tamsiais dryžiais. Pilvas gelsvai baltas. Akys didelės, tamsios, galva didelė, lūpos plonos, žiotys plačios su daug smulkių dantukų. Turi 2 nugarinius pelekus: pirmasis aukštas, tamsiai pilkas su juoda dėme užpakalinėje dalyje, antras žalsvai gelsvas, nedygliuotas. Krūtininiai pelekai oranžiniai arba gelsvi, pilvo, analinis ir uodeginis raudoni, prie pagrindo truputi juosvi. Ešeriai užauga iki 50 cm ilgio ir 3,5 kg svorio. Auga lėtai. Dažniausiai sugaunami 70-500 g svorio ešeriai");
        c3.put("Tips", "Ešeriai žvejojami nuo ankstyvo pavasario, nuslūgus potvynio vandeniui. Po neršto, ešerių kibimas kiek pagerėja, vėliau susilpnėja, bet vėl pagerėja liepos mėnesį, ir tęsiasi iki vandens užšalimo. Ypač gerai ešeriai kimba spalio-lapkričio mėnesiais. Ešeriai gerai kimba ir žiemą, ypač 10-20 dienų po pirmojo ledo, yra pagrindinė žvejojama žuvis šiuo žiemos laikotarpiu. Ešeriai geriau kimba ryte ir vakare, o esant apsiniaukusiam orui, lyjant - visą dieną. Juos galima gaudyti ir šviesiomis naktimis. Ešeriai gaudomi vandens telkiniuose akmenuotu dugnu, prie statesnių krantų, vandeninių augalų, kerplėšų, ar akmenų, duobėse (ypač vėlyvą rudenį ir žiemos pradžioje), upelių žiotyse, duburiuose. Buvimo vietas galima nustatyti pagal ešerių puolamas, šokinėjančias iš vandens žuveles.");
        c3.put("Bait", "Sukriukės, Crank ir Minnow tipo vobleriai");
        drawable = ContextCompat.getDrawable(context, R.drawable.eserys);
        bitmap = ((BitmapDrawable)drawable).getBitmap();
        c3.put("Image", getImageBits(bitmap));
        db.insert("Fishes", null, c3);
        c4.put("ID", 3);
        c4.put("Fish", "Starkis");
        c4.put("Description", "verpsto formos, pailgas, galva smaili. Nugara žalsvai pilka, šonai gelsvos plieno spalvos su 8-12 skersinių dryžių, pilvas baltas. Akys didelės, šviesios. Žvynai vidutinio dydžio, kieti. Turi daug smulkių dantų ir dvejas poras didesnių, ilties formos dantų. Žiotys nedidelės, ant žiaunadangčių yra žvynų, jų kraštas būna su spygliais. Nugariniai ir analinis pelekai pilkšvi, kiti - šviesiai geltoni. Du nugariniai pelekai yra su tamsiomis dėmelėmis ir kietais duriančiais spinduliais. Starkiai užauga iki 135 cm ilgio ir iki 20 kg svorio. Dažniausiai sugaunamos 0,5-3 kg svorio, 35-55 cm ilgio žuvys. Starkiai - sėslios arba pusiau sėslios žuvys. Auga sparčiai, 4 metų amžiaus užauga iki 40-45 cm ilgio ir 600-800 g svorio.");
        c4.put("Tips", "Starkiai žvejojami nuo balandžio mėnesio, kai atslūgsta ir praskaidrėja potvynio vanduo. Starkis pradeda kibti po neršto ir kimba iki spalio mėnesio. Gaudomi spiningu, plūdine meškere, velkiaujant ir skrituliais gilesnėse vietose, tarp akmenų, kerplėšų, prie vandens sūkurių.");
        c4.put("Bait", "Žvejojant spiningu geriausiai tinka nedidelės, žuvelės formos blizgės, aukšlės imitacija. Gaudant plūdine meškere geriausi masalai - gružlys ar aukšlė. Masalas turi būti smulkus ir arti dugno.");
        drawable = ContextCompat.getDrawable(context, R.drawable.starkis);
        bitmap = ((BitmapDrawable)drawable).getBitmap();
        c4.put("Image", getImageBits(bitmap));
        db.insert("Fishes", null, c4);
        c5.put("ID", 4);
        c5.put("Fish", "Lydeka");
        c5.put("Description", "Patelės kiek didesnės už patinus, pasiekia iki 1,5 m ilgį. Nugara žalsvai pilka, šonai pilkšvai žalsvi. Pilvas šviesus. Šonai dėmėti, neretai ant kūno esančios dėmės sudaro skersines juostas. Ši slepiamoji spalva padeda slėptis. Lydeka gali būti pilkai žalsva, pilkšvai ruda, pilkai gelsva - tai lemia gyvenamoji aplinka. Gyvena 7-10 metus (kai kurioslLydekos išgyvena iki 25 metų(. Žuvienoje yra tik 2-3 % riebalų, todėl ji tinka dietiniam maistui. Galva ilga ir plokščia iš viršaus. Žvynai pailgi. Šoninė linija beveik tiesi, ištisinė arba punktyrinė. Kūnas ištįsęs ir tam tikrose vietose esantys pelekai leidžia lydekai staiga užpulti grobį. Žandai su aštriais įvairaus dydžio dantimis, palinkusiais į ryklės pusę. Dantų taip pat būna ant gomurio, liežuvio, žiauninių lankų. Viršutinio žandikaulio dantys pakrypę atgal, kad grobis neišslystų. Apatinio žandikaulio dantys didesni ir išaugę statmenai. Patelės auga greičiau ir gyvena ilgiau nei patinėliai. Įprastai sugaunamos 30-60 cm ilgio, 0,3-2,5 kg svorio lydekos.");
        c5.put("Tips", "Nuo birželio mėnesio iki liepos mėnesio pabaigos kimba žymiai blogiau arba visiškai nekimba. Nuo rugpjūčio mėnesio kimba vėl geriau, ir tai trunka iki vandens užšalimo. Ypač intensyviai lydeka ima masalą rugsėjo mėnesį ir spalio pradžioje. Esant giedram orui, lydeka masalą noriau ima ryte ir vakare, o esant apsiniaukusiam orui arba truputį lyjant, ji gerai kimba visą dieną.");
        c5.put("Bait", "Žvejojama spiningu, plūdine, velkiaujant, blizgiavimu, skrituliais, šakotine. Pagrindinis ir sportiškiausias lydekos gaudymas yra spiningu.");
        drawable = ContextCompat.getDrawable(context, R.drawable.lydeka);
        bitmap = ((BitmapDrawable)drawable).getBitmap();
        c5.put("Image", getImageBits(bitmap));
        db.insert("Fishes", null, c5);
        c6.put("Fish", "Šamas");
        c6.put("ID", 5);
        c6.put("Description", "Žuvies kūnas: pailgas, galva didelė, iš viršaus plokščia. Kūnas ties galva apvalus. Uodeginė kūno dalis ilga, iš šonų plokštėjanti ir žemėjanti. Nugara tamsiai žalsva, šonai žalsvai gelsvi, dėmėti. Pilvas baltas, išmargintas juodai melsvomis dėmelėmis. Oda be žvynų, minkšta, gleivėta. Ties burna 3 poros ūsų: ant viršutinio žando 1 pora ilgų, ant apatinio 2 poros trumpų. Apatinis žandas ilgesnis už viršutinį. Žiotys plačios. Burnoje daugybė smulkių aštrių dantų. Pelekai tamsūs. Šamas stambiausia plėšrūnė gyvenanti mūsų vandenyse. Lietuvoje ši dugninė žuvis gyvena iki 30 metų, užauga iki 100-150 kg svorio, tačiau Pietų Europoje šamas gali užaugti iki 300 kg svorio ir 5 m ilgio. Šamai didžiąją savo gyvenimo dalį praleidžia tūnodami duobėse ar tarp dugno kliuvinių, tik maitindamiesi nakties metu jie pakyla į vandens paviršių.");
        c6.put("Tips", "Šamai mėgsta gilesnes vietas ir dažniausiai slepiasi duobėse, po vandenin suvirtusiais medžiais, tarp kerplėšų, didelių akmenų šešėlyje. Prieš orų pabjurimą, prieš audras, perkūnijas šamai, kaip ir unguriai, darosi neramūs ir dažniausiai palieka savo slėptuves. Ir po audros šamai dar kurį laiką negrįžta į savo slėptuves ir medžioja šalia jų. Čia jų dažniausiai ir sugaunama.");
        c6.put("Bait", "Karvės kraujas, Obuolių skonio kramtomoji guma, Bulvytės fri, Konservuotas šunų ėdalas");
        drawable = ContextCompat.getDrawable(context, R.drawable.samas);
        bitmap = ((BitmapDrawable)drawable).getBitmap();
        c6.put("Image", getImageBits(bitmap));
        db.insert("Fishes", null, c6);
        c7.put("Fish", "Ungurys");
        c7.put("ID", 6);
        c7.put("Description", "Žuvies kūnas: ilgas cilindriškos formos. Galva nedidelė, pailga šonuose kiek suplota. nugara rusvai-žalsva, pilvas gelsvai-baltas. Akys nedidelės, išsidėsčiusios virš žiočių kampų, geltonai-bronzinės spalvos. Nugara tamsi, pilvas gelsvas ar baltas. Oda stora, gleivėta, slidi padengta labai smulkiais žydais. Burna pilna smulkių ir aštrių dantų. Unguriai gyvena iki 25 metų ir užauga iki 2 m ilgio ir 5 kg svorio.");
        c7.put("Tips", "Laikosi prie dugno apatiniuose vandens sluoksniuose nepriklausomai nuo grunto struktūros, tačiau labiau mėgsta dumblėtą ar molingą dugną. Esant dideliems karščiams unguriai gali pakilti į vandens paviršių ir slėptis augalų šešėlyje. Dažniausiai tūno įvairiose slėptuvėse: povandeniniuose urvuose, po akmenimis, kelmais, tankiuose žolynuose ar dumble. Mažesni unguriai laikosi arčiau krantų tuo tarpu didesnieji gentainiai laikosi gelmėje.");
        c7.put("Bait", "Unguriui suvilioti, geriausiai tiks negyvos aukšlės bei naktiniai sliekai.");
        drawable = ContextCompat.getDrawable(context, R.drawable.ungurys);
        bitmap = ((BitmapDrawable)drawable).getBitmap();
        c7.put("Image", getImageBits(bitmap));
        db.insert("Fishes", null, c7);
        c8.put("Fish", "Vėgėlė");
        c8.put("ID", 7);
        c8.put("Description", "Žuvies kūnas: pailgas, galva didelė, iš viršaus plokščia. Kūnas buožgalviškas- ilga ir iš šonų plokščia uodeginė dalis, siaurėjanti link uodegos. Nugara tamsiai juodai ar rudai žalsva su juodomis netaisyklingos formos dėmelėmis. Šonai šviesesni, gelsvai žalsvi, gelsvai rusvi, išmarginti netaisyklingomis šviesiomis dėmėmis. Pilvas šviesiai pilkas ar baltas. Oda gleivėta, stora su giliai įaugusiais labai smulkiais redukuotais žvynais, todėl atrodo jog jų ir nėra. Pelekai minkšti, tamsūs, tamsiai rudi, išmarginti šviesiomis dėmėmis, tik krūtininiai pelekai balti.. Ant apatinio žando vienas ilgas ūsas, ant viršutinio ties šnervėmis du trumpi ūsai. Burna pilna mažų dantukų.");
        c8.put("Tips", "Kuršių marių vėgėlės užauga didžiausios, dažniausiai sutinkamos 1,5-3 kgsvorio, tačiau gali užaugti ir iki 8kg. Labiausiai vėgėlės mėgsta tas vietas, kur į upę ar ežerą įteka šaltiniai. Lyg sustingusi guli vėgėlė ant molėto ar akmenuoto dugno, slėpdamasi už kerplėšos, akmens arba dauboje. ");
        c8.put("Bait", "Gaudo jas pagrinde ant naktinių sliekų, gyvų ir negyvų žuvelių ir varlių.");
        drawable = ContextCompat.getDrawable(context, R.drawable.vegele);
        bitmap = ((BitmapDrawable)drawable).getBitmap();
        c8.put("Image", getImageBits(bitmap));
        db.insert("Fishes", null, c8);
        c9.put("Fish", "Perpelė");
        c9.put("ID", 8);
        c9.put("Description", "Žuvies kūnas: Nugara melsvai žalia, galva rusvai auksinė, šonai ir pilvas sidabriški, kartais su bronziniais atspalviais, Išilgai šonų tęsiasi 6-10 tamsių dėmių eilė, kurios ryškesnės ir didesnės prie galvos. Uodegos pelekas ilgas, giliai įkirptas, jo pamatą dengia pora pailgėjusių didelių žvynų, ant pilvo iš žvynų susidariusi briauna. Žiotys galinės. Panaši į Baltijos silkę žuvis, nuo jos skiriasi labiau suapvalintu pilvu, yra spalvingesnė. Perpelės užauga iki 60 cm ilgio ir 1,5 kg svorio, dažniausiai sugaunamos 5-6 metų amžiaus, 25-40 cm ilgio žuvys. Patelės auga greičiau ir užauga didesnės už patinus.");
        c9.put("Tips", " reta žuvis, buvo įtraukta į Lietuvos Raudonąją knygą. Šiuo metu Kuršių mariose perpelių populiacija atkurta.");
        c9.put("Bait", "Gaudyti mulkiomis žuvimis, vėžiagyviais. Jaunesnės - silkių, brėtlingių, grundalų mailiumi.");
        drawable = ContextCompat.getDrawable(context, R.drawable.perpele);
        bitmap = ((BitmapDrawable)drawable).getBitmap();
        c9.put("Image", getImageBits(bitmap));
        db.insert("Fishes", null, c9);

    }

    public static byte[] getImageBits(Bitmap bitmap) {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 90, outputStream);
        return outputStream.toByteArray();
    }


}
