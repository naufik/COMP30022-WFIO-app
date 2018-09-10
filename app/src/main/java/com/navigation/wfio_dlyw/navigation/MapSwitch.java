package com.navigation.wfio_dlyw.navigation;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;

public class MapSwitch extends AppCompatActivity {

    public void mapSwitch(){
        Intent startIntent = new Intent(getApplicationContext(), MapActivity.class);
        startActivity(startIntent);
    }
}
