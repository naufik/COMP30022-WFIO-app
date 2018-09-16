package com.navigation.wfio_dlyw.navigation;

import android.Manifest;
import android.content.pm.PackageManager;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;

import java.io.File;
import java.io.IOException;

public class RecordVoice extends AppCompatActivity {

    private static final int REQUEST_AUDIO_RECORD = 200;
    private static String mFileName = null;
    private boolean onPlay = false;

    private MediaRecorder mRecorder = null;
    private MediaPlayer   mPlayer = null;

    private Button mRecord;
    private Button mPlay;

    private File mFile;

    // Requesting permission to RECORD_AUDIO
    private boolean permissionToRecordAccepted = false;
    private String [] permissions = {Manifest.permission.RECORD_AUDIO};

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

    private void playButton(boolean isPlaying){
        if(isPlaying)
            stopPlaying();
        else
            startPlaying();
    }

    private void startPlaying() {
        mPlayer = new MediaPlayer();
        try {
            mPlayer.setDataSource(mFileName);
            mPlayer.prepare();
            mPlayer.start();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void stopPlaying() {
        mPlayer.release();
        mPlayer = null;
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
        try{
            mRecorder.stop();
        } catch (Exception e){
            mFile.delete();
        } finally {
            mRecorder.release();
            mRecorder = null;
        }
    }

    @Override
    public void onCreate(Bundle icicle) {
        super.onCreate(icicle);

        // Record to the external cache directory for visibility
        mFileName = getExternalCacheDir().getAbsolutePath();
        mFileName += "/audiorecordtest.3gp";

        mFile = new File(mFileName);

        ActivityCompat.requestPermissions(this, permissions, REQUEST_AUDIO_RECORD);
        setContentView(R.layout.activity_record_voice);
        mRecord = findViewById(R.id.recordVoice);
        mPlay = findViewById(R.id.play);

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

        mPlay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                playButton(onPlay);
                onPlay = !onPlay;
            }
        });
    }

    @Override
    public void onStop() {
        super.onStop();
        if (mRecorder != null) {
            mRecorder.release();
            mRecorder = null;

        }

        if (mPlayer != null) {
            mPlayer.release();
            mPlayer = null;
        }
    }
}
