package com.example.kablys;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

public class LoginActivicty extends AppCompatActivity {
    EditText TextUsername;
    EditText TextPasswd;
    Button ButtonLogin;
    TextView TextViewRegister;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        TextUsername = (EditText) findViewById(R.id.edit_username);
        TextPasswd = (EditText) findViewById(R.id.edit_passwd);
        ButtonLogin = (Button) findViewById(R.id.btn_login);
        TextViewRegister = (TextView) findViewById(R.id.text_login);

        TextViewRegister.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                Intent registerIntent = new Intent(LoginActivicty.this, RegisterActivity.class);
                startActivity(registerIntent);
            }


        });


    }
}
