package com.navigation.wfio_dlyw.comms;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.widget.Toast;

import com.google.firebase.iid.FirebaseInstanceId;
import com.koushikdutta.async.future.FutureCallback;
import com.koushikdutta.ion.Ion;
import com.navigation.wfio_dlyw.navigation.R;
import com.twilio.voice.RegistrationException;
import com.twilio.voice.RegistrationListener;
import com.twilio.voice.Voice;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.Date;

public class Token{
    private static final String TOKEN_ENDPOINT_URL = "https://rawon.naufik.net/voice/accessToken";

    private static Context context;
    private static Token instance;

    // Global variable
    private String value = null;
    private String type;
    private int id;
    private String email;
    private String fullname;
    private JSONArray connections;
    private JSONArray messages;
    private JSONObject currentConnection;
    private String username;
    private String twilioVoiceToken;

    public String getUsername() {
        return username;
    }

    public void setVoiceToken(String v) {
        this.twilioVoiceToken = v;
    }

    public String getVoiceToken() {
        return this.twilioVoiceToken;
    }

    public void setUsername(String username) {
        if (username == null) {
            this.setVoiceToken(null);
            return;
        }
        this.username = username;
        this.loadTwilioToken();
    }

    // Restrict the constructor from being instantiated
    private Token(){
        this.messages = new JSONArray();
    }

    public void setValue(String data){
        this.value = data;
    }

    public String getValue(){
        return this.value;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getType() {
        return type;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getId(){
        return id;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public JSONArray getConnections() {
        return connections;
    }

    public void setConnections(JSONArray connections) {
        this.connections = connections;
    }

    public String getFullName() {
        return fullname;
    }

    public void setFullname(String fullname) {
        this.fullname = fullname;
    }

    public static synchronized Token getInstance(Context ctx) {
        if(instance == null){
            instance = new Token();
        }
        if (ctx != null) {
            instance.context = ctx;
        }
        return instance;
    }

    @Deprecated
    public static Token getInstance() {
        return getInstance(null);
    }

    public static synchronized Token reset() {
        instance = new Token();
        return instance;
    }

    public JSONObject getCurrentConnection() {
        return currentConnection;
    }

    public void setCurrentConnection(JSONObject currentConnection) {
        this.currentConnection = currentConnection;
    }

    public JSONArray getMessages() {
        return messages;
    }

    public void setMessages(JSONArray messages) {
        this.messages = messages;
    }

    private void loadTwilioToken() {
        int resultMic = ContextCompat.checkSelfPermission(context, Manifest.permission.RECORD_AUDIO);
        Log.d("dadada", Boolean.toString(resultMic == PackageManager.PERMISSION_GRANTED));
            Ion.with( context ).load(TOKEN_ENDPOINT_URL + "?identity=" + this.getUsername())
                    .asString()
                    .setCallback((e, s) -> {
                        if (e != null) {
                            Toast.makeText(context, "error jancuk", Toast.LENGTH_LONG).show();
                            return;
                        }
                        this.setVoiceToken(s);
                        Log.d("LOAD", "tok: " + this.twilioVoiceToken);
                        final String fcmToken = FirebaseInstanceId.getInstance().getToken();
                        Log.d("LOAD", "fcm: " + FirebaseInstanceId.getInstance()
                                .getToken());
                        if (fcmToken != null) {
                            Voice.register(context,
                                    this.getVoiceToken(),
                                    Voice.RegistrationChannel.FCM,
                                    fcmToken, new RegistrationListener() {
                                        @Override
                                        public void onRegistered(String s, String s1) {
                                            Log.d("TAG", "registered");
                                        }

                                        @Override
                                        public void onError(RegistrationException e, String s, String s1) {
                                            Log.d("TAG", e.getMessage().toString());
                                            e.printStackTrace();
                                        }
                                    });
                        }
            });
    }

    public void startVoice() {
        this.loadTwilioToken();
    }
}
