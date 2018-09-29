package com.navigation.wfio_dlyw.comms;

import android.app.IntentService;
import android.app.Notification;
import android.app.job.JobParameters;
import android.app.job.JobService;
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

import com.navigation.wfio_dlyw.navigation.AnswerHelp;
import com.navigation.wfio_dlyw.navigation.NotificationReceiver;
import com.navigation.wfio_dlyw.navigation.R;

import org.json.JSONObject;

public class NotificationService extends JobIntentService {
    public NotificationService() {
        super();
    }

    @Override
    protected void onHandleWork(@NonNull Intent intent) {

    }

    @Override
    public int onStartCommand(@Nullable Intent intent, int flags, int startId){
        Token t = Token.getInstance();
        Requester.getInstance(this).requestAction(ServerAction.NOTIFICATION_POLL, null, res -> {
            displayNotification();
        },new Credentials(t.getEmail(), t.getValue()));
        return super.onStartCommand(intent, flags, startId);
    }



    public void displayNotification() {
        String title = "New Notif";
        String message = "yeet";

        //start an activity, then choose intent
        Intent activityIntent = new Intent(this, AnswerHelp.class);
        PendingIntent contentIntent = PendingIntent.getActivity(this,
                0, activityIntent, 0);

        //instant intent
        Intent broadcastIntent = new Intent(this, NotificationReceiver.class);
        broadcastIntent.putExtra("toastMessage", message);
        PendingIntent actionIntent = PendingIntent.getBroadcast(this, 0,
                broadcastIntent, PendingIntent.FLAG_UPDATE_CURRENT);

        Notification notification = new NotificationCompat.Builder(this, "channel 1")
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
}
