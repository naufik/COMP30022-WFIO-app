package com.navigation.wfio_dlyw.navigation;

import android.support.annotation.Nullable;

/***
 * A message object, that would displayed by custom Message adapter
 */
public class Message {
    private String text; // message body
    private boolean belongsToCurrentUser; // is this message sent by us?
    private String name;

    /***
     * Create a new message object
     * @param text String that the message have
     * @param name Name of the sender
     * @param belongsToCurrentUser Check whether the message is from someone else/belongs to the user
     */
    public Message(String text, @Nullable String name, boolean belongsToCurrentUser) {
        this.text = text;
        this.belongsToCurrentUser = belongsToCurrentUser;
        if (name != null) {
            this.name = name;
        }
    }

    /***
     * @return get name of the message sender
     */
    public String getUsername() {return name;}

    /***
     * @return get the String the message contains
     */
    public String getText() {
        return text;
    }

    /***
     * @return return true if message belongs to current user, false otherwise
     */
    public boolean isBelongsToCurrentUser() {
        return belongsToCurrentUser;
    }
}