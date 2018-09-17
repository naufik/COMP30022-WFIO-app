package com.navigation.wfio_dlyw.navigation;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ListView;

import com.navigation.wfio_dlyw.comms.Credentials;
import com.navigation.wfio_dlyw.comms.Requester;
import com.navigation.wfio_dlyw.comms.ServerAction;
import com.navigation.wfio_dlyw.comms.Token;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class MessageListElder extends AppCompatActivity {

    private MessageAdapter messageAdapter;
    private ListView messagesView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_message_list_elder);

        Token token = Token.getInstance();
        messageAdapter = new MessageAdapter(this);
        messagesView = findViewById(R.id.messages_view);
        messagesView.setAdapter(messageAdapter);
        Requester req = Requester.getInstance(this);
        Button refresh = (Button) findViewById(R.id.refresh);
        refresh.setOnClickListener(view -> {
            req.requestAction(ServerAction.MESSAGE_PULL, null, t->{
                try {
                    JSONArray messages = t.getJSONObject("result").getJSONArray("messages");
                    for (int i = 0; i < messages.length(); ++i) {
                        JSONObject currentMessage =  (JSONObject) messages.get(i);
                        onMessage(currentMessage.getString("content"));
                    }
                } catch (JSONException e) {}
            }, new Credentials(token.getEmail(), token.getValue()));
        });
    }

    public void onMessage(String message) {
        //if message sent by self, belongsToCurrentUser is True and dialog pops up on right
        //if false, dialog pops on the left, set name to the carer's/elder's username
        Message message1 = new Message(message, "astuti", false);
        messageAdapter.add(message1);
        // scroll the ListView to the last added element
        messagesView.setSelection(messagesView.getCount() - 1);
    }
}
