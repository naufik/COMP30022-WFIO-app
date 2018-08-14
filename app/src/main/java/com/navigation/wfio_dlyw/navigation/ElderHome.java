package com.navigation.wfio_dlyw.navigation;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;


public class ElderHome extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_elder_home);
        Button navigationButton = (Button)findViewById(R.id.navigateButton);
        navigationButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){
                Intent startIntent = new Intent(getApplicationContext(), ElderNavigation.class);
                startIntent.putExtra("test", "Hello World?");
                startActivity(startIntent);
            }
        });

        Button settingsBtn = (Button) findViewById(R.id.settingsBtn);
        settingsBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent startIntent = new Intent (getApplicationContext(), ElderSettings.class);
                startActivity(startIntent);
            }
        });

    }

}
