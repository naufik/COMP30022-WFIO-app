package com.navigation.wfio_dlyw.comms;

import android.app.Application;

public class Client extends Application {
    private Token token = null;

    public Token getToken() {
        return token;
    }

    public void setToken(Token token) {
        this.token = token;
    }

    public void setTokenValues(String token) {
        this.token.setValue(token);
    }
}
