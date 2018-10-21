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

import com.navigation.wfio_dlyw.navigation.Message;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

/***
 * A singleton class that acts as a session manager to store all information needed
 * for current session.
 */
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
    private JSONArray serverMessages;
    private ArrayList<Message> sessionMessages;
    private JSONObject currentConnection;
    private String username;
    private String twilioVoiceToken;
    private JSONArray favorites;

    /***
     * @return return username of current user
     */
    public String getUsername() {
        return username;
    }

    private void setVoiceToken(String v) {
        this.twilioVoiceToken = v;
    }

    /***
     * @return return voice token of current user
     */
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
    }

    public void setValue(String data){
        this.value = data;
    }

    /***
     * @return token value of current user
     */
    public String getValue(){
        return this.value;
    }

    public void setType(String type) {
        this.type = type;
    }

    /***
     * @return return current user account type
     */
    public String getType() {
        return type;
    }

    public void setId(int id) {
        this.id = id;
    }

    /***
     * @return return current user ID
     */
    public int getId(){
        return id;
    }

    /***
     * @return return current user email
     */
    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    /***
     * @return return all users currently connected to user
     */
    public JSONArray getConnections() {
        return connections;
    }

    public void setConnections(JSONArray connections) {
        this.connections = connections;
    }

    /***
     * @return return current user full name
     */
    public String getFullName() {
        return fullname;
    }

    public void setFullname(String fullname) {
        this.fullname = fullname;
    }

    /***
     * Return the current instance of token, unless token have not been
     * initialized for current session, in which it creates new token object
     * @param ctx activity context
     * @return current token instance
     */
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
        instance = null;
        return instance;
    }

    /***
     * @return return a user JSONObject which is the person currently helping/being helped with/by the user
     */
    public synchronized JSONObject getCurrentConnection() {
        return currentConnection;
    }

    public void setCurrentConnection(JSONObject currentConnection) {
        this.currentConnection = currentConnection;
    }

    /***
     * @return get all message from server that are currently addressed to the user
     */
    public synchronized JSONArray getServerMessages() {
        return serverMessages;
    }

    /***
     * @return get all message that have been displayed in current session
     */
    public synchronized ArrayList<Message> getSessionMessages() {
        return sessionMessages;
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

    /***
     * @return return current user favorites places
     */
    public JSONArray getFavorites() {
        return favorites;
    }

    public void setFavorites(JSONArray favorites) {
        this.favorites = favorites;
    }

    public void startVoice() {
        this.loadTwilioToken();
    }

    /***
     * Initialize Session Messages
     */
    public synchronized void createSessionMessages () {
        this.sessionMessages = new ArrayList<>();
        this.serverMessages = new JSONArray();
    }
}
