package com.example.kablys;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.Nullable;

public class DatabaseAPI extends SQLiteOpenHelper {
    public static final String DatabaseName="kablys.db";
    public static final String TableName="Users";
    public static final String Col_1="ID";
    public static final String Col_2="Username";
    public static final String Col_3="Password";

    public DatabaseAPI(@Nullable Context context) {
        super(context, DatabaseName, null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        sqLiteDatabase.execSQL("Create Table Users (ID Integer PRIMARY KEY AUTOINCREMENT," +
                " Username Text, Password Text)");

    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        sqLiteDatabase.execSQL(" DROP TABLE IF EXISTS " + TableName);
        onCreate(sqLiteDatabase);

    }

    public  long addUser (String user, String password) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put("Username", user);
        contentValues.put("password", password);
        long result = db.insert("Users", null, contentValues);
        db.close();
        return  result;

    }

    public boolean CheckUser (String username, String password){
        String [] columns = {Col_1};
        SQLiteDatabase db  = getReadableDatabase();
        String selection = Col_2 + "=?" + " and " + Col_3 + "?=";
        String [] select_args =  {username, password};
        Cursor cursor = db.query(TableName, columns, selection,
                select_args, null, null, null);
        int count = cursor.getCount();
        cursor.close();
        if(count > 0)
            return  true;
        else
            return  false;

    }
    /*
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

     */
}
