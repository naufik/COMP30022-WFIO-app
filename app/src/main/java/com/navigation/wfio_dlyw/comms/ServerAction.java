package com.navigation.wfio_dlyw.comms;

public enum ServerAction {
    USER_LOGIN("user.login"),
    USER_SIGN_UP("user.signup"),
    USER_GET_INFO("user.details"),
    USER_GET_FAVORITES("user.favs");

    private String actionString;

    ServerAction(String s) {
        this.actionString = s;
    }

    public String getPrompt() {
        return this.actionString;
    };

    public String mapEndpoint() {
        return this.actionString.split(".")[0];
    }
}
