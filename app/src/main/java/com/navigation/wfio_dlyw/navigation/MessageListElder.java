package com.navigation.wfio_dlyw.navigation;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ListView;

import com.navigation.wfio_dlyw.comms.Token;

import org.json.JSONException;

import java.util.Timer;
import java.util.TimerTask;

public class MessageListElder extends AppCompatActivity {

    private MessageAdapter messageAdapter;
    private ListView messagesView;
    Intent serviceIntent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_message_list_elder);
        Token token = Token.getInstance();
        Timer timer = new Timer();

        messageAdapter = new MessageAdapter(this);
        messagesView = findViewById(R.id.messages_view);
        messagesView.setAdapter(messageAdapter);
        this.serviceIntent = new Intent(this, MsgUpdateService.class);
        startService(serviceIntent);

        TimerTask task = new TimerTask() {
            @Override
            public void run() {
                runOnUiThread(() -> {
                    try {
                        while (token.getMessages().length() > 0) {
                            int senderID = token.getMessages().getJSONObject(0).getInt("from");
                            String sender = "error";
                            for (int i =0; i < token.getConnections().length(); i++) {
                                if (token.getConnections().getJSONObject(i).getInt("id") == senderID){
                                    sender = token.getConnections().getJSONObject(i).getString("fullname");
                                }
                            }
                            token.getConnections().getJSONObject(0).getInt("id");
                            onMessage(token.getMessages().getJSONObject(0).getString("content"), sender);
                            token.getMessages().remove(0);
                        }
                    } catch (JSONException e) {}
                });
            }

        };
        timer.schedule(task, 0,1000);
    }
    public void onMessage(String message, String sender) {
        //if message sent by self, belongsToCurrentUser is True and dialog pops up on right
        //if false, dialog pops on the left, set name to the carer's/elder's username
        Message message1 = new Message(message, sender, false);
        messageAdapter.add(message1);
        // scroll the ListView to the last added element
        messagesView.setSelection(messagesView.getCount() - 1);
    }

    @Override
    public void onPause(){
        super.onPause();
        stopService(serviceIntent);
    }

    @Override
    public void onStop(){
        super.onStop();
        stopService(serviceIntent);
    }
}
