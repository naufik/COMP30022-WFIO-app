package com.navigation.wfio_dlyw.navigation;

// Message.java
public class Message {
    private String text; // message body
    private boolean belongsToCurrentUser; // is this message sent by us?
    private String name;

    public Message(String text, String name, boolean belongsToCurrentUser) {
        this.text = text;
        this.belongsToCurrentUser = belongsToCurrentUser;
        this.name = name;
    }

    public String getUsername() {return name;}

    public String getText() {
        return text;
    }

    public boolean isBelongsToCurrentUser() {
        return belongsToCurrentUser;
    }
}