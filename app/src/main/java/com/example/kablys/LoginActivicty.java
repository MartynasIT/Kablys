package com.example.kablys;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class LoginActivicty extends AppCompatActivity {
    EditText TextUsername;
    EditText TextPasswd;
    Button ButtonLogin;
    TextView TextViewRegister;
    DatabaseAPI db;
    long id;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        db = new DatabaseAPI(this);
        TextUsername = findViewById(R.id.edit_username);
        TextPasswd = findViewById(R.id.edit_passwd);
        ButtonLogin = findViewById(R.id.btn_login);
        TextViewRegister = findViewById(R.id.text_register);

        TextViewRegister.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                Intent registerIntent = new Intent(LoginActivicty.this, RegisterActivity.class);
                startActivity(registerIntent);
            }


        });

        ButtonLogin.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
               String user = TextUsername.getText().toString().trim();
               String passwd = TextPasswd.getText().toString().trim();
               long res = db.CheckUser(user,passwd);
               if(res > 0){
                   id = res;
                   Toast.makeText(LoginActivicty.this, "Prisijungta!",
                           Toast.LENGTH_SHORT).show();
               }

               else {
                   Toast.makeText(LoginActivicty.this, "Neteisingi duomenys!",
                           Toast.LENGTH_SHORT).show();
               }
            }


        });

    }
}
