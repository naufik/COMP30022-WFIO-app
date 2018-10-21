package com.navigation.wfio_dlyw.utility;

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

/***
 * Utility class that can is used for reading a string with default android text to speech function
 */
public class Text2Speech {
    private Context application;

    public Text2Speech(Context app){
        this.application = app;
    }

    private android.speech.tts.TextToSpeech t1;

    /***
     * Delegates a button object as a trigger for the text to speech to read a string
     * @param b1 Button as a trigger when clicked
     * @param text String to be read
     */
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

    /***
     * Delegates a Text View object as a trigger for the text to speech to read a string
     * @param tv1 Text view object as a trigger when clicked
     * @param text String to be read
     */
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

    /***
     * Text2Speech object will read out a String object
     * @param text String to be read
     */
    public void read(String text){
        Intent checkIntent = new Intent();
        checkIntent.setAction(android.speech.tts.TextToSpeech.Engine.ACTION_CHECK_TTS_DATA);

        t1=new android.speech.tts.TextToSpeech(application, new OnInitListener() {
            @Override
            public void onInit(int status) {
                if(status != android.speech.tts.TextToSpeech.ERROR) {
                    t1.setLanguage(Locale.UK);
                    t1.speak(text, android.speech.tts.TextToSpeech.QUEUE_FLUSH, null, null);
                }
            }
        });
    }

}
