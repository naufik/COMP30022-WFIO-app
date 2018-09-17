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
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Toast;

import java.io.File;
import java.io.IOException;
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
    private MediaPlayer mPlayer = null;
    private Button mRecord;
    private Button playButton;

    private File mFile;
    private boolean isPlaying = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_message_list);
        // This is where we write the mesage
        editText = (EditText) findViewById(R.id.messageInput);

        messageAdapter = new MessageAdapter(this);
        messagesView = (ListView) findViewById(R.id.messages_view);
        messagesView.setAdapter(messageAdapter);

        // Record to the external cache directory for visibility
        mFileName = getExternalCacheDir().getAbsolutePath();
        mFileName += "/audiorecordtest.3gp";

        mFile = new File(mFileName);

        ActivityCompat.requestPermissions(this, permissions, REQUEST_AUDIO_RECORD);
        mRecord = findViewById(R.id.recordButton);

        mRecord.setOnTouchListener(new View.OnTouchListener(){

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if(event.getAction() == MotionEvent.ACTION_DOWN){
                    Log.d("Interactions","Recording");
                    startRecording();
                } else if(event.getAction() == MotionEvent.ACTION_UP){
                    stopRecording();
                }
                return false;
            }
        });


    }

    private void startPlaying() {
        mPlayer = new MediaPlayer();
        mPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                isPlaying = false;
                playButton.setText("Play");
            }
        });
        try {
            mPlayer.setDataSource(mFileName);
            mPlayer.prepare();
            mPlayer.start();
            isPlaying = true;
            playButton.setText("Stop");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void stopPlaying() {
        mPlayer.release();
        mPlayer = null;
        isPlaying = false;
        playButton.setText("Play");
    }

    private void startRecording() {
        mRecorder = new MediaRecorder();
        mRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
        mRecorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
        mRecorder.setOutputFile(mFile);
        mRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);

        try {
            mRecorder.prepare();
        } catch (IOException e) {
            e.printStackTrace();
        }

        mRecorder.start();
    }

    private void stopRecording() {
        boolean success = false;
        try{
            mRecorder.stop();
            success = true;
        } catch (Exception e){
            mFile.delete();
        } finally {
            mRecorder.release();
            mRecorder = null;
        }
        if(success){

            playButton = new Button(this);
            playButton.setText("Play");

            LinearLayout ll = (LinearLayout) findViewById(R.id.textLayout);
            LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            ll.addView(playButton, lp);

            playButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(!isPlaying)
                        startPlaying();
                    else
                        stopPlaying();
                }
            });
        }
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
        Toast.makeText(this, "hey", Toast.LENGTH_LONG).show();
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