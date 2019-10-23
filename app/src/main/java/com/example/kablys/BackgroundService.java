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
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.Iterator;
import java.util.Locale;

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
            CharSequence name = "KablysCHannel";
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
        runnable = new Runnable() {
            public void run() {
                try {
                    checkPermits();
                    permits.clear();
                } catch (ParseException e) {
                    e.printStackTrace();
                }

                checkDistance(); // tikriname ar zvejys nera arti vietos kur uzdrausta zvejoti

                handler.postDelayed(runnable, 600000); // kas kiek laiko kartos run funkcija (10min)
            }
        };

        handler.postDelayed(runnable, 5000); // po kiek laiko bus paleistas run nuo programos paleidimo
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return START_STICKY; // turetu padeti neuzdaryti proceso
    }

    @Override
    public void onDestroy() {
       // Intent broadcastIntent = new Intent();
       // broadcastIntent.setAction("restartservice");
      //  broadcastIntent.setClass(this, Restarter.class);
       // this.sendBroadcast(broadcastIntent);
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


}