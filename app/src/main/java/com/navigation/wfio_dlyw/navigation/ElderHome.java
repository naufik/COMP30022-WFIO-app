package com.navigation.wfio_dlyw.navigation;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.navigation.wfio_dlyw.comms.Token;


public class ElderHome extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_elder_home);
        Token token = Token.getInstance();
        Toast.makeText(this , token.getValue(), Toast.LENGTH_LONG).show();

        Toolbar myToolbar = (Toolbar) findViewById(R.id.toolbarEH);
        setSupportActionBar(myToolbar);

        Button navigationButton = (Button)findViewById(R.id.navigateButton);
        navigationButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){
                Intent startIntent = new Intent(getApplicationContext(), ElderNavigation.class);
                startActivity(startIntent);
            }
        });

        Button connectBtn = (Button)findViewById(R.id.rncButton);
        connectBtn.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){
                Intent startIntent = new Intent(getApplicationContext(), ElderConnect.class);
                startActivity(startIntent);
            }
        });
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
                Intent startIntent = new Intent(getApplicationContext(), ElderSettings.class);
                startActivity(startIntent);
                return true;

            default:
                // If we got here, the user's action was not recognized.
                // Invoke the superclass to handle it.
                return super.onOptionsItemSelected(item);
        }
    }
}
