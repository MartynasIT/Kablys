package com.example.kablys;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.StrictMode;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class RegisterActivity extends AppCompatActivity {
    EditText TextUsername;
    EditText TextPasswd;
    EditText TextEmail;
    EditText ComfTextPasswd;
    Button ButtonRegister;
    TextView TextViewlogin;
    DatabaseAPI db;
    EmailSender gmail;
    boolean connection;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        db = new DatabaseAPI(this);
        gmail = new EmailSender();

        TextUsername = findViewById(R.id.edit_username);
        TextPasswd = findViewById(R.id.edit_passwd);
        TextEmail = findViewById(R.id.edit_email);
        ComfTextPasswd = findViewById(R.id.edit_comf_passwd);
        ButtonRegister = findViewById(R.id.btn_register);
        TextViewlogin = findViewById(R.id.text_register);
        // patikriname ar yra interneto rysis, kadangi bus isiustas email su duomenimis
        ConnectivityManager connectivityManager = (ConnectivityManager)getSystemService(Context.CONNECTIVITY_SERVICE);
        if(connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE).getState() == NetworkInfo.State.CONNECTED ||
                connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI).getState() == NetworkInfo.State.CONNECTED) {
            connection = true;
        }
        else
            connection = false;


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
                String email = TextEmail.getText().toString().trim();
                Boolean is_email = android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches();
                StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
                StrictMode.setThreadPolicy(policy); // kad leistu is vienu threado siusti mail

                int k = 0;

                if (username.isEmpty() || password.isEmpty() || conf_passwd.isEmpty() || email.isEmpty()) {

                    Toast.makeText(RegisterActivity.this, "Įveskite visus duomenis!",
                            Toast.LENGTH_SHORT).show();
                    k = 1;
                }

                else if (!password.equals(conf_passwd)) {
                    Toast.makeText(RegisterActivity.this, "Slaptažodžiai neatitinka!",
                            Toast.LENGTH_SHORT).show();
                    k = 1;
                }

                else if (db.UserExists(username)) {
                    Toast.makeText(RegisterActivity.this, "Toksai vartotojas egzistuoja!",
                            Toast.LENGTH_SHORT).show();
                    k = 1;
                }

                else if (password.length() <= 6)
                {
                    Toast.makeText(RegisterActivity.this, "Slaptažodis turi būti ilgesnis nei 6 simboliai!",
                            Toast.LENGTH_SHORT).show();
                    k = 1;
                }

                else if (!is_email)
                {
                    Toast.makeText(RegisterActivity.this, "Blogas emailas!",
                            Toast.LENGTH_SHORT).show();
                    k = 1;
                }

                else if (db.EmailExists(email))
                {
                    Toast.makeText(RegisterActivity.this, "Toks emailas jau egzistuoja!",
                            Toast.LENGTH_SHORT).show();
                    k = 1;
                }
                else if (!connection)
                {
                    Toast.makeText(RegisterActivity.this, "Nėra interneto ryšio!",
                            Toast.LENGTH_LONG).show();
                    k = 1;
                }

                if ( k!=1) {
                    Toast.makeText(RegisterActivity.this, "Prisisegistruota!",
                            Toast.LENGTH_SHORT).show();
                    long val = db.addUser(username, makeMD5(password), email);
                    if (val > 0) {
                        gmail.sendMail(email,password,username);
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
