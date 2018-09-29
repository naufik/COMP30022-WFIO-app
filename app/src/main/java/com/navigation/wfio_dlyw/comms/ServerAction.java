package com.navigation.wfio_dlyw.comms;

import com.android.volley.Request;

public enum ServerAction {
    SERVER_TEST_ROOT("root.test", Request.Method.GET),
    USER_LOGIN("user.login", Request.Method.POST),
    USER_SIGN_UP("user.signup", Request.Method.POST),
    USER_GET_INFO("user.details", Request.Method.GET),
    USER_MODIFY_RECORD("user.modify", Request.Method.POST),
    ELDER_REQUEST_LINK("user.genlink", Request.Method.POST),
    CARER_LINK("user.link", Request.Method.POST),
    MESSAGE_PULL("msg.pull", Request.Method.GET),
    MESSAGE_SEND("msg.send", Request.Method.POST),
    CARER_SIGNAL("msg.sos", Request.Method.POST);

    private String actionString;
    private int method;

    ServerAction(String actionPrompt, int httpRequestMethod) {
        this.actionString = actionPrompt;
        this.method = httpRequestMethod;
    }

    public String getPrompt() {
        return this.actionString;
    }

    public int getRequestMethod() {
        return this.method;
    }

    public String mapEndpoint() {
        String t = this.actionString.split("\\.")[0];
        return t.equalsIgnoreCase("root") ? "" : t;
    }
}
