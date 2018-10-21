package com.navigation.wfio_dlyw.navigation;

import android.app.Activity;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.navigation.wfio_dlyw.comms.Requester;
import com.navigation.wfio_dlyw.comms.ServerAction;
import com.navigation.wfio_dlyw.comms.Token;
import com.navigation.wfio_dlyw.comms.Credentials;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.concurrent.Executor;

/**
* Class that handles carer connect activity so that the user (a carer) can connect with new elder
*/
public class CarerConnect extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_carer_connect);

        Toolbar myToolbar = findViewById(R.id.toolbarCC);
        setSupportActionBar(myToolbar);
        Requester req = Requester.getInstance(this);
        Token token = Token.getInstance();

        EditText input = findViewById(R.id.verificationCodeCC);
        Button link = findViewById(R.id.link);

        // make a request to the server to connect carer with the elder which code is inputted
        link.setOnClickListener(view -> {
            String code = input.getText().toString();
            try {
                JSONObject linkRequest = new JSONObject();
                linkRequest.put("code", code);

                req.requestAction(ServerAction.CARER_LINK, linkRequest,
                        t-> {}, new Credentials(token.getEmail(), token.getValue()));
            } catch (JSONException e) {}

            // update the carer connection list
            req.requestAction(ServerAction.USER_GET_INFO, null, t2 -> {
                try {
                    try {
                        Thread.sleep(500);
                    } catch (Exception e) {
                        Log.d("CC", "Problem");
                    }
                    JSONArray newConnection = t2.getJSONObject("result").getJSONObject("user").getJSONArray("eldersList");
                    token.setConnections(newConnection);
                    Intent startIntent = new Intent(getApplicationContext(), MyElders.class);
                    startIntent.setAction("feedback");
                    startIntent.putExtra("name", newConnection.getJSONObject(newConnection.length()-1).getString("fullname"));
                    Log.d("CC",""+newConnection.length());
                    Log.d("CC",""+token.getConnections().length());
                    startActivity(startIntent);
                } catch (JSONException e) {
                    Log.d("this", "doesnt work");
                }
            }, new Credentials(token.getEmail(), token.getValue()));
        });


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
                Intent startIntent = new Intent(getApplicationContext(), CarerHome.class);
                startActivity(startIntent);
                return true;

            default:
                // If we got here, the user's action was not recognized.
                // Invoke the superclass to handle it.
                return super.onOptionsItemSelected(item);
        }
    }

}
