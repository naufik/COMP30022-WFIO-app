package com.navigation.wfio_dlyw.comms;

import android.app.IntentService;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.job.JobParameters;
import android.app.job.JobService;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v4.app.JobIntentService;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.graphics.Color;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;
import android.view.View;
import android.widget.Toast;

import com.navigation.wfio_dlyw.navigation.AnswerHelp;
import com.navigation.wfio_dlyw.navigation.NotificationReceiver;
import com.navigation.wfio_dlyw.navigation.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Timer;
import java.util.TimerTask;

public class NotificationService extends IntentService {
    private static Timer timer;
    private static boolean channelsCreated = false;

    private Handler h = new Handler();

    public NotificationService() {
        super("NotificationServices");
    }


    @Override
    public void onCreate() {
        if (timer == null) {
            timer = new Timer();
        }
        ;
        if (!channelsCreated) {
            createNotificationChannels();
        }
        super.onCreate();
    }

    @Override
    protected void onHandleIntent(@Nullable Intent intent) {
        if (intent.getAction().equals("poll")) {
            this.startPolling();
        } else if (intent.getAction().equals("stop")) {
            this.stopPolling();
        }
    }

    private void stopPolling() {
        timer.cancel();
    }


    public void displayNotification(@NonNull String title, @NonNull String message) {

        //start an activity, then choose intent
        Intent activityIntent = new Intent(this, AnswerHelp.class);
        PendingIntent contentIntent = PendingIntent.getActivity(this,
                0, activityIntent, 0);

        //instant intent
        Intent broadcastIntent = new Intent(this, NotificationReceiver.class);
        broadcastIntent.putExtra("toastMessage", message);
        PendingIntent actionIntent = PendingIntent.getBroadcast(this, 0,
                broadcastIntent, PendingIntent.FLAG_UPDATE_CURRENT);

        Notification notification = new NotificationCompat.Builder(this, "wfio_channel1")
                .setSmallIcon(R.drawable.ic_child)
                .setContentTitle(title)
                .setContentText(message)
                .setColor(Color.BLUE)
                //click this shows the new activity
                .setContentIntent(contentIntent)
                .setAutoCancel(true)
                .setOnlyAlertOnce(true)
                //clicks Toast and creates new Intent use this to decline and answer help request immediately
                .addAction(R.mipmap.ic_launcher, "Accept", actionIntent)
                .addAction(R.mipmap.ic_launcher, "Decline", actionIntent)
                .build();
        //need to give different id's if you want to give multiple notifications instanteneously
        NotificationManagerCompat.from(this).notify(1, notification);
    }

    private void createNotificationChannels() {
        NotificationChannel channel1= new NotificationChannel(
                "wfio_channel1",
                "some notification channel",
                NotificationManager.IMPORTANCE_HIGH
        );
        channel1.setDescription("This is Channel 1");
        channel1.enableVibration(true);
        channel1.enableLights(true);

        NotificationManager manager = getSystemService(NotificationManager.class);
        manager.createNotificationChannel(channel1);
    }

    public void startPolling() {
        Token t = Token.getInstance();
        Requester req = Requester.getInstance(this);

        TimerTask task = new TimerTask() {
            @Override
            public void run() {
                h.post(() -> {
                    req.requestAction(ServerAction.NOTIFICATION_POLL, null, res -> {
                        try {
                            JSONArray notifs = res.getJSONObject("result")
                                    .getJSONArray("notifications");

                            for (int i = 0; i < notifs.length(); ++i) {
                                String title = "Elder needs assistance!";
                                String subtitle = notifs.getJSONObject(0)
                                        .getJSONObject("content").getJSONObject("from")
                                        .getString("fullname");
                                subtitle += " needs help navigating!!";
                                displayNotification(title, subtitle);
                            }
                        } catch (JSONException e) {

                        }
                    }, new Credentials(t.getEmail(), t.getValue()));
                });
            }
        };

        timer.schedule(task, 0, 1000);
    }
}
