package com.example.kablys;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

public class RegisterActivity extends AppCompatActivity {
    EditText TextUsername;
    EditText TextPasswd;
    EditText ComfTextPasswd;
    Button ButtonLogin;
    TextView TextViewRegister;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        TextUsername = (EditText) findViewById(R.id.edit_username);
        TextPasswd = (EditText) findViewById(R.id.edit_passwd);
        ComfTextPasswd = (EditText) findViewById(R.id.edit_conf_passwd);
        ButtonLogin = (Button) findViewById(R.id.btn_login);
        TextViewRegister = (TextView) findViewById(R.id.text_login);
    }
}
