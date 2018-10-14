package com.navigation.wfio_dlyw.navigation;

import android.support.annotation.Nullable;
import android.util.Log;

// Message.java
public class Message {
    private String text; // message body
    private boolean belongsToCurrentUser; // is this message sent by us?
    private String name;

    public Message(String text, @Nullable String name, boolean belongsToCurrentUser) {
        this.text = text;
        this.belongsToCurrentUser = belongsToCurrentUser;
        if (name != null) {
            this.name = name;
        }
    }

    public String getUsername() {return name;}

    public String getText() {
        return text;
    }

    public boolean isBelongsToCurrentUser() {
        return belongsToCurrentUser;
    }
}