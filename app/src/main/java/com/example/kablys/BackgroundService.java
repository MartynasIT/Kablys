package com.example.kablys;

import android.Manifest;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.app.Service;
import android.content.*;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.*;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;
import android.widget.Toast;

import java.lang.reflect.Array;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.Iterator;
import java.util.Locale;
import java.util.Random;

public class BackgroundService extends Service {
    DatabaseAPI db;
    SessionManager Session;
    private ArrayList<String[]> permits = new ArrayList<String[]>();
    private ArrayList<String[]> ForbiddenLocations = new ArrayList<String[]>();
    public Context context = this;
    public Handler handler = null;
    public static Runnable runnable = null;
    private String CHANNEL_ID = "kablys";

    private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = "Notificationai";
            String description = "Reminder";
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, name, importance);
            channel.setDescription(description);
            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }

    public void createNotification(String message) {

        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_android)
                .setContentTitle("Priminimas")
                .setStyle(new NotificationCompat.BigTextStyle()
                        .bigText(message))
                .setPriority(NotificationCompat.PRIORITY_DEFAULT);

        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(this);

        notificationManager.notify(1, builder.build());
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        createNotificationChannel();
        db = new DatabaseAPI(this);
        ForbiddenLocations = db.getForbiddenLocations();
        Session = new SessionManager(this);
        handler = new Handler();

        if (Session.is_logged_in() != false)
        {
            runnable = new Runnable() {
                public void run() {
                    try {
                        checkPermits();
                        permits.clear();
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }

                    checkDistance(); // tikriname ar zvejys nera arti vietos kur uzdrausta zvejoti
                    if (Session.get_status()) // jeigu vartotojas turi enablines challengus
                        giveChallenge(); // duodame dienos isuki

                    handler.postDelayed(runnable, 10000); // kas kiek laiko kartos run funkcija (1min)
                }
            };

            handler.postDelayed(runnable, 3000); // po kiek laiko bus paleistas run nuo programos paleidimo
        }

    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return START_STICKY; // turetu padeti neuzdaryti proceso
    }

    @Override
    public void onDestroy() {

    }

    private void checkPermits() throws ParseException {
        permits = db.getPermits(Session.get_username());
        Calendar c1 = Calendar.getInstance();
        Calendar c2 = Calendar.getInstance();
        c1.add(Calendar.DATE, 1); // priminimas 1d pries

        Iterator<String[]> itr = permits.iterator();
        while (itr.hasNext()) {
            String[] array = itr.next();
            SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyy HH:mm", Locale.UK);
            Date date = dateFormat.parse(array[1]);
            if ((c1.getTime().compareTo(date) > 0) && array[3].equals("0") && !date.before(c2.getTime())) {

                db.UpdatePermit(Session.get_username(), array[1]);
                createNotification("Už vienos dienos pasibaigs leidimas kuris galioja iki: " + array[1]);

            }
            if (date.before(c2.getTime())) {

                db.removePermits(array[1], Session.get_username());

            }


        }
    }

    private void checkDistance() {

        LocationManager lm = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        else {
            assert lm != null;
            Location location = lm.getLastKnownLocation(LocationManager.GPS_PROVIDER);
            assert location != null;
            double latitude;
            double longitude;
            try
            {
               longitude = location.getLongitude();
               latitude = location.getLatitude();
                Location loc1 = new Location("");
                loc1.setLatitude(latitude);
                loc1.setLongitude(longitude);

                Iterator<String[]> itr = ForbiddenLocations.iterator();
                while (itr.hasNext()) {
                    String[] array = itr.next();

                    Location loc2 = new Location("");
                    loc2.setLatitude(Double.parseDouble(array[0]));
                    loc2.setLongitude(Double.parseDouble(array[1]));

                    float distance = loc1.distanceTo(loc2) / 1000;

                    if (distance < 5.0) //mazesnis nei 5km atstumas
                    {
                        WarnUser("Artėjate (atstumas mažesnis nei 5km) prie vietos kurioje yra uždrausta žvejoti. Vietos pavadinimas: " + array[2]);

                    }

                }
            }
            catch (NullPointerException e)
            {

            }


        }
    }


    public void WarnUser(String message) {

        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_android)
                .setContentTitle("Ispėjimas")
                .setStyle(new NotificationCompat.BigTextStyle()
                        .bigText(message))
                .setPriority(NotificationCompat.PRIORITY_DEFAULT);

        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(this);

        notificationManager.notify(2, builder.build());
    }

    public void giveChallenge()
    {
        Date date = Calendar.getInstance().getTime();
        DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm");
        String currentDate = dateFormat.format(date);
        Calendar c1 = Calendar.getInstance();
        Date lastDate = null;
        try {
            lastDate = dateFormat.parse(Session.get_ChallengeDate());
            Calendar c = Calendar.getInstance();
            c.setTime(lastDate);
            c.add(Calendar.DATE, 1);
            lastDate = c.getTime();
        } catch (ParseException| NullPointerException e) {

        }


        if (lastDate != null) {

            if (System.currentTimeMillis() > lastDate.getTime()) { // mes norime isuki pushinti tik 1 karta i diena

                Toast.makeText(this, Session.get_ChallengeDate(),
                        Toast.LENGTH_SHORT).show();
                PostChallenge(currentDate);
            }
        }

        else
        {

            PostChallenge(currentDate);
        }


    }

    private  void PostChallenge(String currentDate)
    {
        ArrayList challenges = new ArrayList();
        challenges = db.getChallenges();
        Random rn = new Random();
        int max = 9;
        int min = 0;
        int pos = rn.nextInt(max - min + 1) + min;
        createNotification((String) challenges.get(pos), currentDate);
    }

    public void createNotification(String message, String currentDate) {
        Session.set_ChallengeDate(currentDate);

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