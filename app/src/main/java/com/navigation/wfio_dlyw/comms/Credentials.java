package com.navigation.wfio_dlyw.comms;

import org.json.JSONException;
import org.json.JSONObject;

public class Credentials {
    private String email;
    private String tokenAuth;

    public Credentials(String email, String token) throws NullPointerException {
        if (email == null || token == null) {
            //throw new NullPointerException();
        } else{
            this.email = email;
            this.tokenAuth = token;
        }
    }

    public String getEmail() {
        return this.email;
    }

    public String getPrivateToken() {
        return this.tokenAuth;
    }

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
