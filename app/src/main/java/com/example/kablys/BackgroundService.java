package com.example.kablys;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.app.Service;
import android.content.*;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.*;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;
import android.widget.Toast;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Iterator;
import java.util.Locale;

public class BackgroundService extends Service {
    DatabaseAPI db;
    SessionManager Session;
    private ArrayList<String[]> permits = new ArrayList<String[]>();
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
        Session = new SessionManager(this);
        Toast.makeText(this, "Service created!", Toast.LENGTH_LONG).show();

        handler = new Handler();
        runnable = new Runnable() {
            public void run() {
              // Toast.makeText(context, "Service is still running", Toast.LENGTH_LONG).show();
                try {
                    checkPermits();
                    permits.clear();
                } catch (ParseException e) {
                    e.printStackTrace();
                }

                handler.postDelayed(runnable, 5000); // kas kiek laiko kartos run funkcija
            }
        };

        handler.postDelayed(runnable, 5000); // po kiek laiko bus paleistas run
    }

    @Override
    public void onDestroy() {
        /* IF YOU WANT THIS SERVICE KILLED WITH THE APP THEN UNCOMMENT THE FOLLOWING LINE */
        //handler.removeCallbacks(runnable);
        Toast.makeText(this, "Service stopped", Toast.LENGTH_LONG).show();
        Intent intent = new Intent("com.android.BackgroundService");
        sendBroadcast(intent);
    }

    private  void checkPermits() throws ParseException {
        permits = db.getPermits(Session.get_username());
        Calendar c1 =Calendar.getInstance();
        Calendar c2 =Calendar.getInstance();
        c1.add(Calendar.DATE,1); // priminimas 1d pries

        Iterator<String[]> itr = permits.iterator();
        while (itr.hasNext()) {
            String[] array = itr.next();
            SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyy HH:mm", Locale.UK);
            Date date = dateFormat.parse(array[1]);
            if((c1.getTime().compareTo(date) > 0) && array[3].equals("0") && !date.before(c2.getTime())){

               db.UpdatePermit(Session.get_username(), array[1]);
               createNotification("Už vienos dienos pasibaigs leidimas kuris galioja iki: " + array[1]);

            }
           if (date.before(c2.getTime()))
            {

                db.removePermits(array[1], Session.get_username());
                Toast.makeText(context, c2.getTime().toString() + " VS " + date , Toast.LENGTH_LONG).show();

            }


        }
    }

}