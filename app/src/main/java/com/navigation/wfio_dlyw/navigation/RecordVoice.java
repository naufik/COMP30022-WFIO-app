package com.navigation.wfio_dlyw.navigation;

import android.app.Activity;
import android.media.MediaRecorder;
import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;

import java.io.IOException;

public class RecordVoice extends Activity {

    private Button recordButton;
    private MediaRecorder mRecorder;
    private String mFileName = null;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_record_voice);
        recordButton = findViewById(R.id.recordVoice);
        mFileName = Environment.getExternalStorageDirectory().getAbsolutePath();
        mFileName += "/voiceMessages.3gp";

        mRecorder = new MediaRecorder();

        recordButton.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {

                if(event.getAction() == MotionEvent.ACTION_DOWN){
                    Log.d("Interactions", "Recording");
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
            Log.d("Interactions","Recording3");
        } catch (IOException e) {
            Log.d("Interactions", "Error");
            e.printStackTrace();
        }
        mRecorder.start();
        Log.d("Interactions", "Recording4");
    }

    private void stopRecording() {
        mRecorder.stop();
        mRecorder.release();
        mRecorder = null;
    }

}
