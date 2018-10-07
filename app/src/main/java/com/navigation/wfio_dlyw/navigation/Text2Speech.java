package com.navigation.wfio_dlyw.navigation;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.speech.tts.TextToSpeech;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import android.speech.tts.TextToSpeech;
import android.speech.tts.TextToSpeech.OnInitListener;

import java.util.Locale;

public class Text2Speech {
    private Context application;

    public Text2Speech(Context app){
        this.application = app;
    }

    private android.speech.tts.TextToSpeech t1;

    public void buttonTalk(Button b1, String text){

        Intent checkIntent = new Intent();
        checkIntent.setAction(android.speech.tts.TextToSpeech.Engine.ACTION_CHECK_TTS_DATA);

        t1=new android.speech.tts.TextToSpeech(application, new OnInitListener() {
            @Override
            public void onInit(int status) {
                if(status != android.speech.tts.TextToSpeech.ERROR) {
                    t1.setLanguage(Locale.UK);
                }
            }
        });

        b1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(application, text,Toast.LENGTH_SHORT).show();
                t1.speak(text, android.speech.tts.TextToSpeech.QUEUE_FLUSH, null, null);
            }
        });
    }

    public void textViewTalk(TextView tv1, String text){
        Intent checkIntent = new Intent();
        checkIntent.setAction(android.speech.tts.TextToSpeech.Engine.ACTION_CHECK_TTS_DATA);

        t1=new android.speech.tts.TextToSpeech(application, new OnInitListener() {
            @Override
            public void onInit(int status) {
                if(status != android.speech.tts.TextToSpeech.ERROR) {
                    t1.setLanguage(Locale.UK);
                }
            }
        });

        tv1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(application, text,Toast.LENGTH_SHORT).show();
                t1.speak(text, android.speech.tts.TextToSpeech.QUEUE_FLUSH, null, null);
            }
        });
    }

    public void read(String text){
        Intent checkIntent = new Intent();
        checkIntent.setAction(android.speech.tts.TextToSpeech.Engine.ACTION_CHECK_TTS_DATA);
        Log.d("T2T", "Read being launch");

        t1=new android.speech.tts.TextToSpeech(application, new OnInitListener() {
            @Override
            public void onInit(int status) {
                if(status != android.speech.tts.TextToSpeech.ERROR) {
                    t1.setLanguage(Locale.UK);
                    Log.d("T2T", "ON init success");
                    t1.speak(text, android.speech.tts.TextToSpeech.QUEUE_FLUSH, null, null);
                }
            }
        });
    }

}
