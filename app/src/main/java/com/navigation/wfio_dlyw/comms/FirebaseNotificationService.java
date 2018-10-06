package com.navigation.wfio_dlyw.comms;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.PendingIntent;
import android.content.Intent;
import android.os.Bundle;
import com.navigation.wfio_dlyw.navigation.R;
import android.app.NotificationManager;
import android.content.Context;
import android.util.Log;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import com.navigation.wfio_dlyw.navigation.CallActivity;
import com.twilio.voice.CallInvite;
import com.twilio.voice.MessageException;
import com.twilio.voice.MessageListener;
import com.twilio.voice.Voice;

import java.util.Map;

public class FirebaseNotificationService extends FirebaseMessagingService {
    private NotificationManager notificationManager;



    @Override
    public void onCreate() {
        super.onCreate();
        this.notificationManager = (NotificationManager)
                getSystemService(Context.NOTIFICATION_SERVICE );
    }

    @Override
    public void onMessageReceived(RemoteMessage message) {
        Log.d("NOTIF", "kalo yang ini pasti nongol anjing");
        if (message.getData().size() > 0) {
            Log.d("NOTIF", "nongol gan");
            Map<String, String> content = message.getData();
            final int notificationId = (int) System.currentTimeMillis();

            Voice.handleMessage(this, content, new MessageListener() {
                @Override
                public void onCallInvite(CallInvite callInvite) {
                    if (callInvite.getState() == CallInvite.State.PENDING) {
                        Intent intent2 = new Intent(FirebaseNotificationService.this,
                                CallActivity.class);
                        intent2.setAction("call.answer");

                        Bundle extras = new Bundle();
                        extras.putString("fromUsername", callInvite.getFrom());
                        extras.putString("callSid", callInvite.getCallSid());
                        extras.putInt("notificationId", notificationId);

                        PendingIntent pendingIntent = PendingIntent.getActivity(
                                FirebaseNotificationService.this, notificationId,
                                intent2, PendingIntent.FLAG_ONE_SHOT);

                        Notification n = new Notification
                                .Builder(getApplicationContext(), "wfio_channel1")
                                .setSmallIcon(R.drawable.ic_call)
                                .setContentTitle(callInvite.getFrom() + " called you.")
                                .setContentText("Here to offer a better help")
                                .setContentIntent(pendingIntent)
                                .setAutoCancel(true)
                                .build();

                        FirebaseNotificationService.this.notificationManager
                                .notify(notificationId, n);
                    }
                }

                @Override
                public void onError(MessageException e) {

                }
            });

        }
    }
}
