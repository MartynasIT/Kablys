package com.example.kablys;



import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class LoginActivicty extends AppCompatActivity {
    EditText TextUsername;
    EditText TextPasswd;
    Button ButtonLogin;
    TextView TextViewRegister;
    DatabaseAPI db;
    SessionManager Session;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        db = new DatabaseAPI(this);
        TextUsername = findViewById(R.id.edit_username);
        TextPasswd = findViewById(R.id.edit_passwd);
        ButtonLogin = findViewById(R.id.btn_login);
        TextViewRegister = findViewById(R.id.text_register);

        Session = new SessionManager(this);

        if (Session.is_logged_in())
        {
           Intent LoginIntent = new Intent(LoginActivicty.this, DrawerActivity.class);
           startActivity(LoginIntent);
           finish();
        }

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
               String res = db.CheckUser(user,makeMD5(passwd));
               if(!res.isEmpty()){
                   Session.set_logged_in(true);
                   Session.set_username(user);
                   Session.set_email(res);
                   Toast.makeText(LoginActivicty.this, "Prisijungta!",
                           Toast.LENGTH_SHORT).show();
                   Intent SuccessIntent = new Intent(LoginActivicty.this, DrawerActivity.class);
                   startActivity(SuccessIntent);
                   finish();
               }

               else {
                   Toast.makeText(LoginActivicty.this, "Neteisingi duomenys!",
                           Toast.LENGTH_SHORT).show();
               }
            }

        });

    }
    public String makeMD5(String passwd) {
        try {
            MessageDigest md5 = MessageDigest.getInstance("MD5");
            byte[] Digest = md5.digest(passwd.getBytes());
            BigInteger number = new BigInteger(1, Digest);
            String hash = number.toString(16);

            while (hash.length() < 32)
                hash = "0" + hash;

            return hash;
        } catch (NoSuchAlgorithmException e) {
            return null;
        }
    }
}
