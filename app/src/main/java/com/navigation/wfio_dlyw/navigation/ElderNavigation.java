package com.navigation.wfio_dlyw.navigation;


import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.support.v4.app.NotificationCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.concurrent.locks.ReadWriteLock;

public class ElderNavigation extends AppCompatActivity {

    public static final String channel_1_ID = "channel 1";
    public static final String channel_2_ID = "channel 2";
    private static final int uniqueID = 45612;

    private void createNotificationChannels(){
        if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.O){
            NotificationChannel channel1= new NotificationChannel(
                    channel_1_ID,
                    "channel 1",
                    NotificationManager.IMPORTANCE_HIGH
            );
            channel1.setDescription("This is Channel 1");
            NotificationChannel channel2= new NotificationChannel(
                    channel_2_ID,
                    "channel 1",
                    NotificationManager.IMPORTANCE_LOW
            );
            channel2.setDescription("This is Channel 2");

            NotificationManager manager = getSystemService(NotificationManager.class);
            manager.createNotificationChannel(channel1);
            manager.createNotificationChannel(channel2);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_elder_navigation);

//        Button notif = (Button) findViewById(R.id.notif);
//        notif.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                ButtonClicked(view);
//            }
//        });

        Toolbar myToolbar = (Toolbar) findViewById(R.id.toolbarEN);
        setSupportActionBar(myToolbar);

//        Button favoritesButton = (Button) findViewById(R.id.favoritesButton);
//        favoritesButton.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                exampleRequest();
//            }
//        });

    }

//
//    private void exampleRequest(){
//        Requester rs = Requester.getInstance(this.getApplicationContext());
//
//        rs.GETRequest(new Response.Listener<JSONObject>() {
//            @Override
//            public void onResponse(JSONObject response) {
//                try {
//                    makeToast(response.getString("message"));
//                } catch (JSONException e) {
//                    makeToast(e.getMessage());
//                }
//            }
//
//        });
////    }
//
//    private void makeToast(String msg) {
//        Toast.makeText(this, msg, Toast.LENGTH_LONG).show();
//    }

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

}
