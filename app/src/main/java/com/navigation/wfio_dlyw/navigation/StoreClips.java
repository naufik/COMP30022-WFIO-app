package com.navigation.wfio_dlyw.navigation;

import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;

import java.io.IOException;
import java.util.ArrayList;

public class StoreClips extends AppCompatActivity implements View.OnClickListener{

    private LinearLayout parent;
    private Button playButton;
    private MediaPlayer mPlayer = null;
    private int counter = 0;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        ArrayList<String> fileNames =  (ArrayList<String>) getIntent().getStringArrayListExtra("listName");

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

    private void stopPlaying() {
        mPlayer.release();
        mPlayer = null;
    }

    @Override
    public void onClick(View v) {

        startPlaying(v.getTag());
    }
}
