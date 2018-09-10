package com.navigation.wfio_dlyw.navigation;

import com.android.volley.Response;
import com.navigation.wfio_dlyw.comms.*;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
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

    NotificationCompat.Builder notification;
    private static final int uniqueID = 45612;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_elder_navigation);

        notification  = new NotificationCompat.Builder(ElderNavigation.this, "45612");
        notification.setAutoCancel(true);

        Button notif = (Button) findViewById(R.id.notif);
        notif.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ButtonClicked(view);
            }
        });

        Toolbar myToolbar = (Toolbar) findViewById(R.id.toolbarEN);
        setSupportActionBar(myToolbar);

        Button favoritesButton = (Button) findViewById(R.id.favoritesButton);
        favoritesButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                exampleRequest();
            }
        });

    }

    public void ButtonClicked(View view) {
        //Build the notification
        notification.setSmallIcon(R.drawable.ic_launcher_background);
        notification.setTicker("This is the ticker");
        notification.setWhen(System.currentTimeMillis());
        notification.setContentTitle("Here is the title");
        notification.setContentText("I am body test of your notification");

        Intent intent = new Intent(this, ElderNavigation.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        notification.setContentIntent(pendingIntent);

        //Build Notification and issues it
        NotificationManager manager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        manager.notify(uniqueID, notification.build());
    }

    private void exampleRequest(){
        Requester rs = Requester.getInstance(this.getApplicationContext());

        rs.GETRequest(new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    makeToast(response.getString("message"));
                } catch (JSONException e) {
                    makeToast(e.getMessage());
                }
            }

        });
    }

    private void makeToast(String msg) {
        Toast.makeText(this, msg, Toast.LENGTH_LONG).show();
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

}
