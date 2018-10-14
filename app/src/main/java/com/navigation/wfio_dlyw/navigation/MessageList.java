package com.navigation.wfio_dlyw.navigation;

import android.Manifest;
import android.app.IntentService;
import android.content.BroadcastReceiver;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.media.MediaRecorder;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.widget.Button;
import android.widget.EditText;
import android.view.View;
import android.widget.ListView;
import android.widget.Toast;

import com.navigation.wfio_dlyw.comms.*;
import com.navigation.wfio_dlyw.twilio.CallService;
import com.navigation.wfio_dlyw.twilio.TwilioUtils;
import com.twilio.voice.*;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.function.Function;


public class MessageList extends AppCompatActivity{

    private TwilioUtils twilio;
    private BroadcastReceiver callEventsHandler;

    private String toName;
    private String toUserName;
    private boolean onCall = false;

    private EditText editText;
    private MessageAdapter messageAdapter;
    private ListView messagesView;

    // Requesting permission to RECORD_AUDIO
    private boolean permissionToRecordAccepted = false;
    private String [] permissions = {Manifest.permission.RECORD_AUDIO};

    private static final int REQUEST_AUDIO_RECORD = 200;
    private static String mFileName = null;

    private MediaRecorder mRecorder = null;
    private Button mRecord;

    private ArrayList<String> fileNames = new ArrayList<>();
    private Button viewClips;
    private int fileCount = 0;

    private int recipientID;

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        handleCallIntent(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_message_list);

        twilio = TwilioUtils.getInstance(this);
        // This is where we write the message
        editText = findViewById(R.id.messageInput);

        Intent intent = getIntent();
        /*
        ElderItem elderItem = intent.getParcelableExtra("Example Item");
        String name = elderItem.getmText1();
        recipientID = elderItem.getmId(); */
        try {
            recipientID = Token.getInstance().getCurrentConnection().getInt("id");
            Toolbar myToolbar = findViewById(R.id.toolbarML);
            myToolbar.setTitle("");
            setSupportActionBar(myToolbar);
            this.toName = Token.getInstance().getCurrentConnection().getString("fullname");
            this.toUserName = Token.getInstance().getCurrentConnection().getString("username");
            myToolbar.setTitle(this.toName);
        } catch (JSONException e) {}


        // Record to the external cache directory for visibility
        mFileName = getExternalCacheDir().getAbsolutePath();
        mFileName += "/audiorecordtest" + fileCount + ".3gp";

        messageAdapter = new MessageAdapter(this);
        messagesView = findViewById(R.id.messages_view);
        messagesView.setAdapter(messageAdapter);

        ActivityCompat.requestPermissions(this, permissions, REQUEST_AUDIO_RECORD);
        mRecord = findViewById(R.id.recordButton);

        mRecord.setOnTouchListener(new View.OnTouchListener(){

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if(event.getAction() == MotionEvent.ACTION_DOWN){
                    startRecording();
                } else if(event.getAction() == MotionEvent.ACTION_UP){
                    stopRecording();
                }
                return false;
            }
        });

    }


    private void startRecording() {
        mRecorder = new MediaRecorder();
        mRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
        mRecorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
        mRecorder.setOutputFile(mFileName);
        mRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);

        try {
            mRecorder.prepare();
            mRecorder.start();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    private void stopRecording() {

        try{
            mRecorder.stop();
            fileNames.add(mFileName);

            // Record to the external cache directory for visibility
            mFileName = getExternalCacheDir().getAbsolutePath();
            mFileName += "/audiorecordtest" + (++fileCount) + ".3gp";

        } catch (Exception e){
            mFileName = "";
        } finally {
            mRecorder.release();
            mRecorder = null;
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);

        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.messaging, menu);
        callEventsHandler = new CallService.CallServiceReceiver() {
            private MenuItem item =
                    menu.getItem(0);

            @Override
            public void onDisconnect() {
                item.setIcon(R.drawable.ic_call);
                item.setEnabled(true);
            }

            @Override
            public void onConnected() {
                Toolbar toolbar = findViewById(R.id.toolbarML);
                toolbar.setTitle("ON CALL: ");

                item.setIcon(R.drawable.ic_hangup);
                item.setEnabled(true);
            }

            @Override
            public void onCallFailure() {
                item.setIcon(R.drawable.ic_call);
                item.setEnabled(true);
            }
        };

        IntentFilter filter = new IntentFilter();
        filter.addAction("call.ondisconnect");
        filter.addAction("call.onconnected");
        filter.addAction("call.onfailure");
        filter.addAction("call.onfailure");


        registerReceiver(callEventsHandler, filter);
        return true;
    }


    @Override
    public void onDestroy() {
        unregisterReceiver(callEventsHandler);
        this.callEventsHandler = null;
        super.onDestroy();
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

        if (message.equals("")){
            return;
        }

        Token token = Token.getInstance();
        Requester req = Requester.getInstance(this);
        try {
            JSONObject param = new JSONObject();
            //param.put("recipient",token.getCurrentConnection().getInt("id")).put("content", message);
            param.put("recipient",recipientID).put("content", message);
            req.requestAction(ServerAction.MESSAGE_SEND, param, t -> {}, new Credentials(token.getEmail(), token.getValue()));
        } catch (JSONException e) {}
        Toast.makeText(this, message, Toast.LENGTH_LONG).show();
        if (message.length() > 0) {
            onMessage(message);
            editText.getText().clear();
        }
    }

    public void onMessage(String message) {
        //if message sent by self, belongsToCurrentUser is True and dialog pops up on right
        //if false, dialog pops on the left, set name to the carer's/elder's username
        Message message1 = new Message(message, "You", true);
        messageAdapter.add(message1);
        // scroll the ListView to the last added element
        messagesView.setSelection(messagesView.getCount() - 1);
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.call_button:
                if (twilio.getCall() == null) {
                    makeCall();
                    item.setIcon(R.drawable.ic_hangup);
                } else {
                    stopCall();
                }

                findViewById(R.id.call_button).setEnabled(false);
                return true;
            case R.id.clips_button:
                Intent intent = new Intent(getApplicationContext(), StoreClips.class);
                intent.putExtra("fileNames", fileNames);
                startActivity(intent);

            default:
                // If we got here, the user's action was not recognized.
                // Invoke the superclass to handle it.
                return super.onOptionsItemSelected(item);
        }
    }

    private void makeCall() {
        try {
            Intent callIntent = new Intent(this, CallService.class);
            callIntent.setAction("call.start");
            callIntent.putExtra("to", Token.getInstance(this).getCurrentConnection()
                .getString("username"));
            startService(callIntent);
        } catch (JSONException e) {
            Toast.makeText( this, "currently not being connected to anyone" ,
                    Toast.LENGTH_LONG).show();
        }
    }

    private void stopCall() {
        Intent stopCallIntent = new Intent(this, CallService.class);
        stopCallIntent.setAction("call.stop");
        startService(stopCallIntent);
    }

    private void handleCallIntent(Intent intent) {
        if (intent.getAction().equals("call.answer")) {
            CallInvite invite = intent.getParcelableExtra("invite");
            twilio.receiveCall(intent.getIntExtra("notificationId", 0),
                    invite);
            Intent answerIntent = new Intent(this, CallService.class);
            answerIntent.setAction("call.answer");
            answerIntent.putExtra("invite", invite);
            startService(answerIntent);
        }
    }
}