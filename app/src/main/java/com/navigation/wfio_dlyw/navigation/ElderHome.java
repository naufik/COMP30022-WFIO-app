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

import com.navigation.wfio_dlyw.comms.Credentials;
import com.navigation.wfio_dlyw.comms.Requester;
import com.navigation.wfio_dlyw.comms.ServerAction;
import com.navigation.wfio_dlyw.comms.Token;

import org.json.JSONException;


public class ElderHome extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_elder_home);
        Token token = Token.getInstance();
        Toolbar myToolbar = findViewById(R.id.toolbarEH);
        setSupportActionBar(myToolbar);

        Button navigationButton = findViewById(R.id.navigateButton);
        navigationButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){
                Intent startIntent = new Intent(getApplicationContext(), ElderNavigation.class);
                startActivity(startIntent);
            }
        });

        Button connectBtn = findViewById(R.id.rncButton);
        connectBtn.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){
                Intent startIntent = new Intent(getApplicationContext(), ElderConnect.class);
                startActivity(startIntent);
            }
        });

        Requester req = Requester.getInstance(this);
        req.requestAction(ServerAction.USER_GET_INFO, null, t2 -> {
            try {
                token.setConnections(t2.getJSONObject("result").getJSONObject("user").getJSONArray("carersList"));
            } catch (JSONException e) {}
        }, new Credentials(token.getEmail(), token.getValue()));
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

    @Override
    public void onBackPressed() {
        System.exit(0);
    }
}
