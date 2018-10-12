package com.navigation.wfio_dlyw.navigation;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.VoidDDQ.Cam.UnityPlayerActivity;
import com.navigation.wfio_dlyw.comms.Credentials;
import com.navigation.wfio_dlyw.comms.NotificationService;
import com.navigation.wfio_dlyw.comms.Requester;
import com.navigation.wfio_dlyw.comms.ServerAction;
import com.navigation.wfio_dlyw.comms.Token;

import org.json.JSONException;
import org.json.JSONObject;


public class ElderHome extends AppCompatActivity {

    private Intent favouriteIntent;
    private static final int PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_elder_home);
        Token token = Token.getInstance(this);
        Token.getInstance( this ).setUsername( Token.getInstance( this ).getUsername());
        Toolbar myToolbar = findViewById(R.id.toolbarEH);
        setSupportActionBar(myToolbar);

        Intent notifier = new Intent(this, NotificationService.class);
        notifier.setAction("poll");
        startService(notifier);

        Button favouriteButton = findViewById(R.id.favoritesBtn);
        favouriteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                favouriteIntent = new Intent(getApplicationContext(), Favourites.class);
                startActivity(favouriteIntent);

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

    public void getLocationPermission(View view) {
        if (ContextCompat.checkSelfPermission(this.getApplicationContext(),
                android.Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            onPermissionGranted();
        } else {
            ActivityCompat.requestPermissions(this,
                    new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION},
                    PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String permissions[],
                                           @NonNull int[] grantResults) {
        switch (requestCode) {
            case PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    onPermissionGranted();
                } else {
                    // Do nothing, try to notify user to accept else can't do shit
                }
            }
        }
    }

    private void onPermissionGranted() {
        Intent intent = new Intent(this, UnityPlayerActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
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


    public static void Call(Activity activity){
        Intent intent = new Intent(activity, ElderMaps.class);
        activity.startActivity(intent);
    }

    public static void CallAgain(Activity activity){
        Intent intent = new Intent(activity, ElderNavigation.class);
        activity.startActivity(intent);
    }
}
