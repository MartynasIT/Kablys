package com.example.kablys;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.support.annotation.Nullable;


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
        long result = db.insert(TableName, null, contentValues);
        db.close();
        return  result;

    }

    public long CheckUser (String username, String password){
        long id = 0;
        SQLiteDatabase db  = getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT ID FROM Users WHERE Username = ? AND Password = ?", new String[] {username, password});
        while (cursor.moveToNext())
        {
            id = cursor.getInt(0);
        }
        cursor.close();
        db.close();

        return  id;
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
