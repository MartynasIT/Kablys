package com.example.kablys;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class RegisterActivity extends AppCompatActivity {
    EditText TextUsername;
    EditText TextPasswd;
    EditText ComfTextPasswd;
    Button ButtonRegister;
    TextView TextViewlogin;
    DatabaseAPI db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        db = new DatabaseAPI(this);
        TextUsername = findViewById(R.id.edit_username);
        TextPasswd = findViewById(R.id.edit_passwd);
        ComfTextPasswd = findViewById(R.id.edit_comf_passwd);
        ButtonRegister = findViewById(R.id.btn_register);
        TextViewlogin = findViewById(R.id.text_login);

        TextViewlogin.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                Intent LoginIntent = new Intent(RegisterActivity.this, LoginActivicty.class);
                startActivity(LoginIntent);
            }


        });

        ButtonRegister.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view) {
                String username = TextUsername.getText().toString().trim();
                String password = TextPasswd.getText().toString().trim();
                String conf_passwd = ComfTextPasswd.getText().toString().trim();

                if (!password.equals(conf_passwd)) {
                    Toast.makeText(RegisterActivity.this, "Slaptažodžiai neatitinka!",
                            Toast.LENGTH_SHORT).show();
                }

                else if (db.UserExists(username)) {
                    Toast.makeText(RegisterActivity.this, "Toksai vartotojas egzistuoja!",
                            Toast.LENGTH_SHORT).show();
                }

                if (password.equals(conf_passwd) && !db.UserExists(username)) {
                    Toast.makeText(RegisterActivity.this, "Prisisegistruota!",
                            Toast.LENGTH_SHORT).show();
                    long val = db.addUser(username, password);
                    if (val > 0) {
                        Intent Login = new Intent(RegisterActivity.this, LoginActivicty.class);
                        startActivity(Login);
                    } else {
                        Toast.makeText(RegisterActivity.this, "Registracijos klaida!",
                                Toast.LENGTH_SHORT).show();
                    }
                }

            }

        });
    }
}
