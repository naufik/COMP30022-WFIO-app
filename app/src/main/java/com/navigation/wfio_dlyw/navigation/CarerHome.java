package com.navigation.wfio_dlyw.navigation;

import android.app.IntentService;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.support.v7.widget.Toolbar;

import com.navigation.wfio_dlyw.comms.NotificationService;

public class CarerHome extends AppCompatActivity {
    Intent serviceIntent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_carer_home);

        Toolbar myToolbar = findViewById(R.id.toolbarCH);
        setSupportActionBar(myToolbar);

        Button connectToElderBtn = (Button)findViewById(R.id.connectToElderBtn);
        connectToElderBtn.setOnClickListener(view -> {
                Intent startIntent = new Intent(getApplicationContext(), CarerConnect.class);
                startActivity(startIntent);
            }
        );

        Button myElders = (Button)findViewById(R.id.myElders);
        myElders.setOnClickListener(view -> {
            Intent startIntent = new Intent(getApplicationContext(), MyElders.class);
            startActivity(startIntent);
        });

        this.serviceIntent = new Intent(this, NotificationService.class);
        serviceIntent.setAction("poll");
        startService(serviceIntent);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.settings,menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.settingsTB:
                Intent startIntent = new Intent(getApplicationContext(), CarerSettings.class);
                startActivity(startIntent);
                return true;

            default:
                // If we got here, the user's action was not recognized.
                // Invoke the superclass to handle it.
                return super.onOptionsItemSelected(item);

        }
    }
}
