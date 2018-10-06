package com.navigation.wfio_dlyw.comms;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.Date;

public class Token{
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
        this.username = username;
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

    public int getId() {
        return id;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public JSONArray getConnections() { return connections; }

    public void setConnections(JSONArray connections) {
        this.connections = connections;
    }

    public String getFullname() {
        return fullname;
    }

    public void setFullname(String fullname) {
        this.fullname = fullname;
    }

    public static synchronized Token getInstance(){
        if(instance==null){
            instance=new Token();
        }
        return instance;
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
}
