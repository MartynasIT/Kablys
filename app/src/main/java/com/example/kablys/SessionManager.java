package com.example.kablys;

import android.content.Context;
import android.content.SharedPreferences;

public class SessionManager {
    SharedPreferences preferences;
    SharedPreferences.Editor editor;
    Context context;

    public SessionManager(Context context){
        this.context = context;
        preferences = context.getSharedPreferences("myapp", Context.MODE_PRIVATE);
        editor = preferences.edit();
    }

    public void set_logged_in(boolean logggedIn){
        editor.putBoolean("State",logggedIn);
        editor.commit();
    }

    public void set_username(String username){
        editor.putString("Username", username);
        editor.commit();
    }

    public void set_email(String email){
        editor.putString("Email", email);
        editor.commit();
    }

    public boolean is_logged_in() {
        return preferences.getBoolean("State", false);
    }

    public Object get_username() {
        return preferences.getString("Username", null);
    }

    public String get_email() {
        return preferences.getString("Email", null);
    }

}