package com.navigation.wfio_dlyw.navigation;


import android.app.Activity;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
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
import com.miguelcatalan.materialsearchview.MaterialSearchView;
import com.navigation.wfio_dlyw.comms.Credentials;
import com.navigation.wfio_dlyw.comms.Requester;
import com.navigation.wfio_dlyw.comms.ServerAction;
import com.navigation.wfio_dlyw.comms.Token;

import java.util.ArrayList;
import java.util.List;

public class ElderNavigation extends AppCompatActivity {
    public static final String EXTRA_DESTINATION = "com.navigation.wfio_dlyw.navigation.DESTINATION";
    private static final int MAX_SUGGESTIONS = 100;
    private Intent favouriteIntent;
    private MaterialSearchView searchView;
    private List[] stuff;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Token token = Token.getInstance();
        Toast.makeText(this , token.getValue(), Toast.LENGTH_LONG).show();
        setContentView(R.layout.activity_elder_navigation);

        Toolbar myToolbar = (Toolbar) findViewById(R.id.toolbarEN);
        setSupportActionBar(myToolbar);

        Button elderMessage = findViewById(R.id.eldermsg);
        elderMessage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent startIntent = new Intent(getApplicationContext(), MessageListElder.class);
                startActivity(startIntent);
            }
        });
        Button notifyAll = findViewById(R.id.nofifyAll);
        notifyAll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Requester minta = Requester.getInstance(getApplicationContext());
                Token var = Token.getInstance();
                minta.requestAction(ServerAction.CARER_SIGNAL, null, response -> {}, new Credentials(var.getEmail(), var.getValue()));
            }
        });
        Button arButton = findViewById(R.id.AR);
        arButton.setOnClickListener(view -> {
            Intent startIntent = new Intent(getApplicationContext(), UnityPlayerActivity.class);
            startIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(startIntent);
        });
        Button favouriteButton = findViewById(R.id.favoritesButton);
        favouriteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                favouriteIntent = new Intent(getApplicationContext(), Favourites.class);
                startActivity(favouriteIntent);

            }
        });
        String[] list = new String[] { "Barney" , "is", "a", "dinosaur", "of", "our", "imagination"};

        //searchview stuff
        MaterialSearchView searchView = (MaterialSearchView) findViewById(R.id.search_view);
        this.searchView = searchView;
        searchView.setSuggestions(list);

        searchView.setOnQueryTextListener(new MaterialSearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                //Do some magic
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                //Do some magic
                return false;
            }
        });
        searchView.setOnSearchViewListener(new MaterialSearchView.SearchViewListener() {
            @Override
            public void onSearchViewShown() {
                //Do some magic
            }

            @Override
            public void onSearchViewClosed() {
                //Do some magic
            }
        });

    }

    public void sendDestination(View view) {
        Intent intent = new Intent(this, ElderMaps.class);
        EditText editText = findViewById(R.id.navigationSearchField);
        String destination = editText.getText().toString();
        intent.putExtra(EXTRA_DESTINATION, destination);
        startActivity(intent);
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(getApplicationContext(), ElderHome.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
    }



    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.search, menu);

        MenuItem item = menu.findItem(R.id.action_search);
        searchView.setMenuItem(item);

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
