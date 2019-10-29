package com.example.kablys;

import android.app.ActivityManager;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Random;


public class DrawerActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {
    private DrawerLayout drawerLayout;
    SessionManager Session;
    DatabaseAPI db;
    private String CHANNEL_ID = "kablys";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_drawer);
        Session = new SessionManager(this);
        db = new DatabaseAPI(this);


        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        drawerLayout = findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar,
                R.string.open_navigation, R.string.close_navigation);
        getSupportActionBar().setTitle("Žemėlapis");

        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();

        // jeigu backgroundo procesas yra isjungtas ji paleidziame
        if (!isMyServiceRunning(BackgroundService.class)) {
            Intent BackgroundService = new Intent(this, BackgroundService.class);
            ContextWrapper cont = new ContextWrapper(getBaseContext());
            cont.startService(BackgroundService);
        }

        if (Session.get_status()) // jeigu vartotojas turi enablines challengus
        giveChallenge();

        NavigationView navView = findViewById(R.id.navigation_view);
        navView.setNavigationItemSelectedListener(this);
        View headerView = navView.getHeaderView(0);
        TextView user = headerView.findViewById(R.id.drawer_username);
        TextView email = headerView.findViewById(R.id.drawer_email);
        user.setText((String) Session.get_username());
        email.setText(Session.get_email());
        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                new MapFragment()).commit();
    }

    private boolean isMyServiceRunning(Class<?> serviceClass) {
        ActivityManager manager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if (serviceClass.getName().equals(service.service.getClassName())) {
                return true;
            }
        }
        return false;
    }


    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
        switch (menuItem.getItemId()){
           case R.id.nav_account:
               getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                       new AccountFragment()).commit();
               getSupportActionBar().setTitle("Paskyra");
                break;

            case R.id.nav_logout:
                Session = new SessionManager(this);
                Session.set_logged_in(false);
                Intent LogOutIntent = new Intent(DrawerActivity.this, LoginActivicty.class);
                startActivity(LogOutIntent);
                finish();
                break;

            case R.id.nav_maps:
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                        new MapFragment()).commit();
                break;

            case  R.id.share:
                Intent share = new Intent(Intent.ACTION_SEND);
                share.setType("image/jpeg/text");
                share.putExtra(Intent.EXTRA_TEXT, "");
                startActivity(Intent.createChooser(share, "share"));
                break;

            case  R.id.nav_permit:
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                        new PermitFragment()).commit();
                getSupportActionBar().setTitle("Leidimai");
                break;

            case  R.id.nav_fishes:
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                        new FishVikiFragment()).commit();
                getSupportActionBar().setTitle("Žuvys");
                break;

            case  R.id.nav_calendar:
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                        new CalendarFragment()).commit();
                getSupportActionBar().setTitle("Kalendorius");
                break;

            case  R.id.nav_weather:
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                        new WeatherFragment()).commit();
                getSupportActionBar().setTitle("Orai");
                break;
        }
        drawerLayout.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    public  void onBackPressed()
    {
        if(drawerLayout.isDrawerOpen(GravityCompat.START))
        {
            drawerLayout.closeDrawer(GravityCompat.START);
        }
        else {
            super.onBackPressed();
        }
    }

    public void giveChallenge()
    {
        Date date = Calendar.getInstance().getTime();
        DateFormat dateFormat = new SimpleDateFormat("yyyy-mm-dd hh:mm:ss");
        String currentDate = dateFormat.format(date);
        Calendar c1 = Calendar.getInstance();
        Date lastDate = null;
        try {
            lastDate = dateFormat.parse(Session.get_ChallengeDate());
        } catch (ParseException| NullPointerException e) {

        }

        if (lastDate != null) {

            if (lastDate.after(c1.getTime())) { // mes norime isuki pushinti tik 1 karta i diena

                PostChallenge(currentDate);
            }
        }

        else
        {
            PostChallenge(currentDate);
        }


    }

    private  void PostChallenge(String currentDate )
    {
        ArrayList challenges = new ArrayList();
        challenges = db.getChallenges();
        Random rn = new Random();
        int max = 10;
        int min = 0;
        int pos = rn.nextInt(max - min + 1) + min;
        createNotification((String) challenges.get(pos));
        Session.set_ChallengeDate(currentDate);
    }

    public void createNotification(String message) {

        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_android)
                .setContentTitle("Šios dienos išukis")
                .setStyle(new NotificationCompat.BigTextStyle()
                        .bigText(message))
                .setPriority(NotificationCompat.PRIORITY_DEFAULT);

        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(this);

        notificationManager.notify(4, builder.build());
    }



}
