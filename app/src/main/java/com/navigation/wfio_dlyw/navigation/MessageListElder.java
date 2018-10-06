package com.navigation.wfio_dlyw.navigation;

import android.content.Intent;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.navigation.wfio_dlyw.comms.Token;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

public class MessageListElder extends AppCompatActivity {
    private boolean update;
    private Intent serviceIntent;
    private ArrayList<Message> arrayOfMessages = new ArrayList<>();

    Handler handler = new Handler();
    Runnable runner = new Runnable() {
        @Override
        public void run() {
            if (!update){
                return;
            }
            populateUsersList();
            handler.postDelayed(this,1500);
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Toast.makeText(this, "New Activity Created", Toast.LENGTH_SHORT).show();
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
        Log.d("MLE", "populateUsersList Called");
        while (token.getMessages().length() > 0) {
            try {
                Log.d("MLE", "Message exist in Token");
                JSONObject curMessage = token.getMessages().getJSONObject(0);
                if (curMessage.getInt("from") == token.getCurrentConnection().getInt("id")){
                    arrayOfMessages.add(new Message(curMessage.getString("content"),token.getCurrentConnection().getString("fullname"),false));
                }
                token.getMessages().remove(0);
            } catch (JSONException e) {}
        }
        // Create the adapter to convert the array to views
        CustomMessageAdapter adapter = new CustomMessageAdapter(this, arrayOfMessages);
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
}

