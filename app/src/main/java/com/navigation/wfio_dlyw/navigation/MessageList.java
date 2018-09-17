package com.navigation.wfio_dlyw.navigation;

import android.Manifest;
import android.content.pm.PackageManager;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.view.View;
import android.widget.ListView;
import java.util.Random;


public class MessageList extends AppCompatActivity {

    private EditText editText;
    private MessageAdapter messageAdapter;
    private ListView messagesView;

    // Requesting permission to RECORD_AUDIO
    private boolean permissionToRecordAccepted = false;
    private String [] permissions = {Manifest.permission.RECORD_AUDIO};
    private static final int REQUEST_AUDIO_RECORD = 200;
    private static String mFileName = null;

    private MediaRecorder mRecorder = null;
    private MediaPlayer   mPlayer = null;
    private Button mRecord;
    private File mFile;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_message_list);
        // This is where we write the mesage
        editText = (EditText) findViewById(R.id.messageInput);

        messageAdapter = new MessageAdapter(this);
        messagesView = (ListView) findViewById(R.id.messages_view);
        messagesView.setAdapter(messageAdapter);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode){
            case REQUEST_AUDIO_RECORD:
                permissionToRecordAccepted  = grantResults[0] == PackageManager.PERMISSION_GRANTED;
                break;
        }
        if (!permissionToRecordAccepted ) finish();

    }

    public void sendMessage(View view) {
        String message = editText.getText().toString();
        if (message.length() > 0) {
            onMessage(message);
            editText.getText().clear();
        }
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