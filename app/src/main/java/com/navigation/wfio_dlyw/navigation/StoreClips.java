package com.navigation.wfio_dlyw.navigation;

import android.media.MediaPlayer;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;

import java.io.IOException;
import java.util.ArrayList;

/**
 * Display a list of voice clips that were recorded previously
 */
public class StoreClips extends AppCompatActivity implements View.OnClickListener{

    private LinearLayout parent;
    private Button playButton;
    // Used to play the voice clips
    private MediaPlayer mPlayer = null;
    // Number of voice clips
    private int counter = 0;

    /**
     * Initialize the list of all voice clips, for every voice clip it will create a play button
     * @param savedInstanceState
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // List of all the voice clips
        ArrayList<String> fileNames =  getIntent().getStringArrayListExtra("listName");

        setContentView(R.layout.activity_store_clips);

        parent = findViewById(R.id.list);

        for(String i : fileNames){
            playButton = new Button(this);
            playButton.setId(counter);
            playButton.setText("Play");
            playButton.setTag(i);
            counter++;
            parent.addView(playButton);
            playButton.setOnClickListener(StoreClips.this);
        }
    }

    /**
     * Once the user presses the play button, it will stop any clips that were playing,
     * afterwards MediaPlayer will first prepare the voice clip before playing
     * @param file which is the directory to where the voice clip is stored
     */
    private void startPlaying(Object file) {

        if(mPlayer != null && mPlayer.isPlaying())
            stopPlaying();

        mPlayer = new MediaPlayer();
        try {
            mPlayer.setDataSource(file.toString());
            mPlayer.prepare();
            mPlayer.start();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Immediately stop the currently played voice clip
     */
    private void stopPlaying() {
        mPlayer.release();
        mPlayer = null;
    }

    /**
     * Pressing any voice clip buttons will play that recorded voice clip
     * @param v which is view of the Activity
     */
    @Override
    public void onClick(View v) {

        startPlaying(v.getTag());
    }
}
