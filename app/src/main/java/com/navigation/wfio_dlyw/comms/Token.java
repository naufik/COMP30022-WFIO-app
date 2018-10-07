package com.navigation.wfio_dlyw.comms;

import com.navigation.wfio_dlyw.navigation.Message;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

public class Token{
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

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    // Restrict the constructor from being instantiated
    private Token(){
        this.serverMessages = new JSONArray();
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

    public JSONArray getServerMessages() {
        return serverMessages;
    }

    public ArrayList<Message> getSessionMessages() {
        return sessionMessages;
    }

    public void createSessionMessages () {
        this.sessionMessages = new ArrayList<>();
    }
}
