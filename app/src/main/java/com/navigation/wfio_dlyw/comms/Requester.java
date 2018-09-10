package com.navigation.wfio_dlyw.comms;

import java.util.Map;
import java.util.HashMap;

import android.content.Context;
import android.support.annotation.Nullable;
import android.widget.Toast;

import com.android.volley.*;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.function.Function;

public class Requester {

    public static String SERVER_URL = "http://rawon.naufik.net:3000/";

    private static Requester instance;
    private Context context;
    private RequestQueue requestQueue;


    private Requester(Context context) {
        this.context = context;
        this.requestQueue = Volley.newRequestQueue(context.getApplicationContext());
    }

    public static synchronized Requester getInstance(Context context) {
        if (Requester.instance == null) {
            Requester.instance = new Requester(context);
        }

        return Requester.instance;
    }

    public <T> void addRequest(Request<T> req) {
        this.requestQueue.add(req);
    }

    private void post(String endpoint, JSONObject body,
                      Response.Listener<JSONObject> onResponse, String auth) {
        JsonObjectRequest req = new JsonObjectRequest(Request.Method.POST,
                SERVER_URL + endpoint,
                body,
                onResponse,
                err -> {
                        Toast.makeText(context, err.getMessage(),
                                Toast.LENGTH_LONG).show();
        }) { //no semicolon or coma
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> params = new HashMap<String, String>();
                params.put("Content-Type", "application/json");
                if (auth != null) {
                    params.put("WFIO-AUTH", auth);
                }
                return params;
            }
        };
        this.addRequest(req);
    }

    private void get(String endpoint, Response.Listener<JSONObject> onResponse, String auth) {
        JsonObjectRequest req = new JsonObjectRequest(Request.Method.GET,
                SERVER_URL + endpoint,
                null,
                onResponse,
                err -> {
                        Toast.makeText(context, err.getMessage(),
                                Toast.LENGTH_LONG).show();
        }){
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> params = new HashMap<String, String>();
                params.put("Content-Type", "application/json");
                if (auth != null) {
                    params.put("WFIO-AUTH", auth);
                }
                return params;
            }
        };
        this.addRequest(req);
    }

    public void getServerStatus(Function<Boolean, Void> onStatusReceived) {
        this.get("", response -> {
                try {
                    onStatusReceived.apply((Boolean) response.get("online"));
                } catch (Exception e) {
                    Toast.makeText(context, e.getMessage(),
                            Toast.LENGTH_LONG).show();
                }
            }, null);
    }

    public void requestAction(ServerAction action, JSONObject params,
                              Response.Listener<JSONObject> onFinish, String token) {
        JSONObject jsonObj = new JSONObject();
        try {
            jsonObj.put("action", action.getPrompt());
            jsonObj.put("params", params);
            if (token != null) {
                jsonObj.put("auth", token);
            }
        } catch (JSONException e) {
            Toast.makeText(this.context, "Invalid Request Sent -- Contact Developer!",
                    Toast.LENGTH_LONG).show();
        }
        this.post(action.mapEndpoint(), jsonObj, onFinish, token);
    }

    public void requestAction(ServerAction action, JSONObject params,
                              Response.Listener<JSONObject> onFinish) {
        this.requestAction(action, params, onFinish, null);

    }
}
