package com.navigation.wfio_dlyw.navigation;


import android.app.Activity;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.VoidDDQ.Cam.UnityPlayerActivity;
import com.navigation.wfio_dlyw.comms.Token;

public class ElderNavigation extends AppCompatActivity {
    private NotificationManagerCompat notificationManager;
    public static final String EXTRA_DESTINATION = "com.navigation.wfio_dlyw.navigation.DESTINATION";
    public static final String channel_1_ID = "channel 1";

   private final Context context = this;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Token token = Token.getInstance();
        Toast.makeText(this , token.getValue(), Toast.LENGTH_LONG).show();
        setContentView(R.layout.activity_elder_navigation);
        createNotificationChannels();
        notificationManager = NotificationManagerCompat.from(this);
        Toolbar myToolbar = (Toolbar) findViewById(R.id.toolbarEN);
        setSupportActionBar(myToolbar);

        Button elderMessage = (Button) findViewById(R.id.eldermsg);
        elderMessage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent startIntent = new Intent(getApplicationContext(), MessageListElder.class);
                startActivity(startIntent);
            }
        });

        Button arButton = (Button) findViewById(R.id.AR);
        arButton.setOnClickListener(view -> {
            Intent startIntent = new Intent(getApplicationContext(), UnityPlayerActivity.class);
            startActivity(startIntent);
        });


    }

    public void sendDestination(View view) {
        Intent intent = new Intent(this, ElderMaps.class);
        EditText editText = (EditText) findViewById(R.id.navigationSearchField);
        String destination = editText.getText().toString();
        intent.putExtra(EXTRA_DESTINATION, destination);
        startActivity(intent);
    }

    public void sendOnChannel(View v){
        String title = "New Notif";
        String message = "yeet";

        //start an activity, then choose intent
        Intent activityIntent = new Intent(this,     AnswerHelp.class);
        PendingIntent contentIntent = PendingIntent.getActivity(this,
                0, activityIntent, 0);

        //instant intent
        Intent broadcastIntent = new Intent(this, NotificationReceiver.class);
        broadcastIntent.putExtra("toastMessage", message);
        PendingIntent actionIntent = PendingIntent.getBroadcast(this, 0,
                broadcastIntent, PendingIntent.FLAG_UPDATE_CURRENT);

        Notification notification = new NotificationCompat.Builder(this, channel_1_ID)
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
                .addAction(R.mipmap.ic_launcher, "Decline",actionIntent)
                .build();
        //need to give different id's if you want to give multiple notifications instanteneously
        notificationManager.notify(1, notification);
    }


    public void createNotificationChannels(){
        if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.O){
            NotificationChannel channel1= new NotificationChannel(
                    channel_1_ID,
                    "channel 1",
                    NotificationManager.IMPORTANCE_HIGH
            );
            channel1.setDescription("This is Channel 1");
            channel1.enableVibration(true);
            channel1.enableLights(true);

            NotificationManager manager = getSystemService(NotificationManager.class);
            manager.createNotificationChannel(channel1);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.back,menu);
        return true;
    }
    

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.back_button:
                Intent startIntent = new Intent(getApplicationContext(), ElderHome.class);
                Toast.makeText(this, "get back on it, come on come on", Toast.LENGTH_LONG).show();
                startActivity(startIntent);
                return true;

            default:
                // If we got here, the user's action was not recognized.
                // Invoke the superclass to handle it.
                return super.onOptionsItemSelected(item);
        }
    }

    public static void Call(Activity activity){
        Intent intent = new Intent(activity, ElderMaps.class);
        activity.startActivity(intent);
    }

    public static void CallAgain(Activity activity){
        Intent intent = new Intent(activity, ElderNavigation.class);
        activity.startActivity(intent);
    }
}
