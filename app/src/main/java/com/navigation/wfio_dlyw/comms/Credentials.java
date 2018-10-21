package com.navigation.wfio_dlyw.comms;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Represents the credentials of a user which contains the user's identifying feature,
 * which is a username and a token string.
 *
 * @author Team We'll Figure It Out
 * @author Naufal Fikri (http://github.com/naufik)
 * @author Surviantoro Ilham Yudanardi (http://github.com/syudanardi)
 */
public class Credentials {
    private String email;
    private String tokenAuth;

    /**
     * Creates a new representation of Credentials for a single user.
     * @param email The email of the user.
     * @param token The hex authentication token string of the user.
     * @throws NullPointerException
     */
    public Credentials(String email, String token) throws NullPointerException {
        if (email == null || token == null) {
            //throw new NullPointerException();
        } else{
            this.email = email;
            this.tokenAuth = token;
        }
    }

    /**
     * Obtains the user's email.
     * @return email of the user.
     */
    public String getEmail() {
        return this.email;
    }

    /**
     * Obtains the user's private token
     * @return private hex token of the user.
     */
    public String getPrivateToken() {
        return this.tokenAuth;
    }

    /**
     * Obtains a JSON Object representation of the token to be sent to the server.
     * @return A JSON Object that matches the server's interface to pass on credentials.
     */
    public JSONObject toJSONObject() {
        JSONObject obj = new JSONObject();
        try {
            obj.put("email", this.email);
            obj.put("token", this.tokenAuth);
        } catch (JSONException e) {
            return null;
        }
        return obj;
    }

}
