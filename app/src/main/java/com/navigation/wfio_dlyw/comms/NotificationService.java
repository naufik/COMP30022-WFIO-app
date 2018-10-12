package com.navigation.wfio_dlyw.comms;

import android.app.IntentService;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.job.JobParameters;
import android.app.job.JobService;
import android.os.Bundle;
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
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.navigation.wfio_dlyw.navigation.AnswerHelp;
import com.navigation.wfio_dlyw.navigation.CarerMaps;
import com.navigation.wfio_dlyw.navigation.ElderHome;
import com.navigation.wfio_dlyw.navigation.ElderMaps;
import com.navigation.wfio_dlyw.navigation.ElderNavigation;
import com.navigation.wfio_dlyw.navigation.NotificationReceiver;
import com.navigation.wfio_dlyw.navigation.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;
import java.util.function.Function;

/**
 * This service polls notification from the notification server and creates a notification
 * if such is needed.
 */
public class NotificationService extends IntentService {
    private static Timer timer;
    private static boolean channelsCreated = false;
    private static int currentId = Integer.MIN_VALUE;

    private Handler h = new Handler();
    private boolean isElder;

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


    public void displayNotification(@NonNull String title, @NonNull String message,
                                    @Nullable Intent contentIntent,
                                    @Nullable HashMap<String, Intent> buttonIntents) {

        if (buttonIntents == null) {
            buttonIntents = new HashMap<>();
        }

        //start an activity, then choose intent

        //instant intent
        NotificationCompat.Builder newNotification =
                new NotificationCompat.Builder(this, "wfio_channel1")
                .setSmallIcon(R.drawable.ic_child)
                .setContentTitle(title)
                .setContentText(message)
                .setColor(Color.BLUE)
                .setOnlyAlertOnce(true);

        if (contentIntent != null) {
            PendingIntent pendingContent = PendingIntent.getActivity(this,
                    (int)System.currentTimeMillis(), contentIntent, 0);
            newNotification.setContentIntent(pendingContent);
        }

        for (Map.Entry<String, Intent> i : buttonIntents.entrySet()) {
            newNotification.addAction(R.mipmap.ic_launcher, i.getKey(), PendingIntent.getActivity(this,
                    (int)System.currentTimeMillis(), i.getValue(), 0));
        }

        Log.d("HELLO", "mantap anjing");
        NotificationManagerCompat.from(this).notify((int) System.currentTimeMillis(), newNotification.setAutoCancel(true).build());
    }

    private void createNotificationChannels() {
        NotificationChannel channel1 = new NotificationChannel(
                "wfio_channel1",
                "some notification channel",
                NotificationManager.IMPORTANCE_HIGH
        );
        channel1.setDescription("This is Channel 1");
        channel1.enableVibration(true);

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
                            //Log.d("asu", notifs.toString());

                            for (int i = 0; i < notifs.length(); ++i) {
                                JSONObject currentMessage = notifs.getJSONObject(i)
                                        .getJSONObject("display");

                                String title = currentMessage.getString("title");
                                String subtitle = currentMessage.getString("subtitle");


                                Intent contentIntent = generateIntent(notifs.getJSONObject(i));

                                // this is pretty hardcoded for a while
                                HashMap<String, Intent> buttons = new HashMap<>();
                                notifs.getJSONObject(i).put("redirect", "sos.autoaccept");
                                Intent testIntent = generateIntent(notifs.getJSONObject(i));

                                buttons.put("Accept", testIntent);

                                Log.d("HELLO", "Displaying notif...");
                                displayNotification(title, subtitle, contentIntent, buttons);
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }, new Credentials(t.getEmail(), t.getValue()));
                });
            }
        };

        timer.schedule(task, 0, 1000);
    }

    private Intent generateIntent(JSONObject thing) {
        try {
            String action = thing.getString("redirect");
            JSONObject content = thing.getJSONObject("content");
            Intent x = null;


            switch (action) {
                case "sos.respond": {
                    x = new Intent(this, AnswerHelp.class);
                    x.setAction("help-accept");
                    x.putExtra("from", content.getJSONObject("from").getString("email"));
                    x.putExtra("fromName", content.getJSONObject("from").getString("fullname"));
                    Bundle b = new Bundle();
                    b.putString("route", content.getJSONArray("route").toString());
                    x.putExtras(b);
                    break;
                }
                case "sos.autoaccept": {
                    x = new Intent(this, Token.getInstance().getType().equals("CARER") ?
                            CarerMaps.class : ElderMaps.class);
                    x.setAction("i-can-help");
                    x.putExtra("from", content.getJSONObject("from").getString("email"));
                    x.putExtra("fromName", content.getJSONObject("from").getString("fullname"));
                    if (Token.getInstance().getType().equals("CARER")) {
                        Bundle b = new Bundle();
                        b.putString("route", content.getJSONArray("route").toString());
                        x.putExtras(b);
                    }
                    break;
                }
                default:
                    // pass;
            }

            return x;
        } catch (JSONException e) {
            return null;
        }
    }

}
