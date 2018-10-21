package com.navigation.wfio_dlyw.comms;

import com.android.volley.Request;

/**
 * This defines the possible actions to be requested on the server.
 *
 * @author Naufal Fikri (http://github.com/naufik).
 */
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
    CARER_SIGNAL("msg.sos", Request.Method.POST),
    CARER_ACCEPT("msg.sosaccept", Request.Method.POST),
    DELETE_LINK("user.delete", Request.Method.POST),
    NOTIFICATION_POLL("notif.poll", Request.Method.GET);

    private String actionString;
    private int method;

    ServerAction(String actionPrompt, int httpRequestMethod) {
        this.actionString = actionPrompt;
        this.method = httpRequestMethod;
    }

    /**
     * Obtains the 'prompt / action' of a certain server action.
     * @return prompt / action string of a certain server action
     */
    public String getPrompt() {
        return this.actionString;
    }

    /**
     * Obtains the request method of a certain server action.
     * @return the HTTP request method of a certain server action.
     */
    public int getRequestMethod() {
        return this.method;
    }

    /**
     * Gets the endpoint where a specific action can be sent to.
     * @return The endpoint of the server where the action can be sent to. The empty string
     * corresponds to the root endpoint of the server, i.e. http://example.com/ <- without anything
     * after the final slash.
     */
    public String mapEndpoint() {
        String t = this.actionString.split("\\.")[0];
        return t.equalsIgnoreCase("root") ? "" : t;
    }
}
