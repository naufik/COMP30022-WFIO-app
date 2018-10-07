package com.navigation.wfio_dlyw.navigation;

import android.content.Intent;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.ListView;
import android.widget.Toast;

import com.navigation.wfio_dlyw.comms.Token;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class MessageListElder extends AppCompatActivity {
    private boolean update;
    private Intent serviceIntent;

    Handler handler = new Handler();
    Runnable runner = new Runnable() {
        @Override
        public void run() {
            if (!update){
                return;
            }
            populateUsersList();
            handler.postDelayed(this,700);
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_message_list_elder);
        update = true;
        this.serviceIntent = new Intent(this, MsgUpdateService.class);
        this.serviceIntent.setAction("poll");
        startService(serviceIntent);
        handler.post(runner);
    }

    private void populateUsersList() {
        Token token = Token.getInstance();
        // Construct the data source
        while (token.getServerMessages().length() > 0) {
            try {
                JSONObject curMessage = token.getServerMessages().getJSONObject(0);
                if (curMessage.getInt("from") == token.getCurrentConnection().getInt("id")){
                    token.getSessionMessages().add(new Message(curMessage.getString("content"),token.getCurrentConnection().getString("fullname"),false));
                }
                token.getServerMessages().remove(0);
            } catch (JSONException e) {}
        }
        // Create the adapter to convert the array to views
        CustomMessageAdapter adapter = new CustomMessageAdapter(this, token.getSessionMessages());
        // Attach the adapter to a ListView
        ListView listView = (ListView) findViewById(R.id.messages_view);
        listView.setAdapter(adapter);
    }

    @Override
    protected void onResume() {
        super.onResume();
        update = true;
        this.serviceIntent = new Intent(this, MsgUpdateService.class);
        this.serviceIntent.setAction("poll");
        startService(serviceIntent);
        // start first run by hand
        handler.post(runner);
    }

    @Override
    protected void onPause() {
        super.onPause();
        update= false;
        this.serviceIntent = new Intent(this, MsgUpdateService.class);
        this.serviceIntent.setAction("stop");
        startService(serviceIntent);
    }

    @Override
    protected void onStop() {
        super.onStop();
        update= false;
        this.serviceIntent = new Intent(this, MsgUpdateService.class);
        this.serviceIntent.setAction("stop");
        startService(serviceIntent);
    }
}

