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

/**
 * A background task manager to send HTTP requests to the WFIO Don't Lose Your Way server.
 *
 * @author Naufal Fikri (http://github.com/naufik)
 */
public class Requester {

    private static final String SERVER_URL = "https://rawon.naufik.net/";

    private static Requester instance;
    private Context context;
    private RequestQueue requestQueue;


    private Requester(Context context) {
        this.context = context;
        this.requestQueue = Volley.newRequestQueue(context.getApplicationContext());
    }

    /**
     * Obtains the current instance of the requester object.
     * @param context the android context.
     * @return the current requester instance.
     */
    public static synchronized Requester getInstance(Context context) {
        if (Requester.instance == null) {
            Requester.instance = new Requester(context);
        }

        return Requester.instance;
    }

    /**
     * Adds an arbitrary HTTP request.
     * @param req The request object.
     * @param <T> The expected type of the response.
     */
    public <T> void addRequest(Request<T> req) {
        this.requestQueue.add(req);
    }

    /**
     * Creates a HTTP Post request to the server.
     * @param endpoint specific endpoint of the server.
     * @param body the JSON Object body of the server.
     * @param onResponse A callback method that is executed once a response have been received.
     * @param auth Credentials of the user to be put in.
     */
    public void post(String endpoint, JSONObject body,
                     Response.Listener<JSONObject> onResponse,
                     @Nullable Credentials auth) {
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
                    params.put("XWfio-Identity", auth.getEmail());
                    params.put("XWfio-Secret", auth.getPrivateToken());
                }
                return params;
            }
        };
        this.addRequest(req);
    }

    /**
     * Creates a HTTP Post request to the server.
     * @param endpoint specific endpoint of the server.
     * @param onResponse A callback method that is executed once a response have been received.
     * @param auth Credentials of the user to be put in.
     */
    public void get(String endpoint, Response.Listener<JSONObject> onResponse, @Nullable Credentials auth) {
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
                    params.put("XWfio-Identity", auth.getEmail());
                    params.put("XWfio-Secret", auth.getPrivateToken());
                }
                return params;
            }
        };
        this.addRequest(req);
    }

    /**
     * Obtains the status of the server.
     * @param onStatusReceived A callback method after the response have been received, takes a
     *                         boolean that will be true if the server is active, or false
     *                         otherwise.
     */
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

    /**
     * Creates a specific request to the server based on the predefined server actions in the
     * ServerAction class.
     * @param action action to be taken.
     * @param params parameters of the action in JSON form
     * @param onFinish callback method that will be called once a response have been received from
     *                 the server.
     * @param auth user credentials.
     */
    public void requestAction(ServerAction action, @Nullable JSONObject params,
                              Response.Listener<JSONObject> onFinish,
                              @Nullable Credentials auth) {
        JSONObject jsonObj = new JSONObject();
        try {
            jsonObj.put("action", action.getPrompt());
            if (params != null) {
                jsonObj.put("params", params);
            } else {
                jsonObj.put("params", new JSONObject());
            }
            if (auth != null) {
                jsonObj.put("identity", auth.toJSONObject());
            }
        } catch (JSONException e) {
            Toast.makeText(this.context, "Invalid Request Sent -- Contact Developer!",
                    Toast.LENGTH_LONG).show();
        }

        if (action.getRequestMethod() == Request.Method.POST) {
            this.post(action.mapEndpoint(), jsonObj, onFinish, auth);
        } else if (action.getRequestMethod() == Request.Method.GET) {
            this.get(action.mapEndpoint(),onFinish, auth);
        }

    }

    /**
     * Creates a specific request to the server based on the predefined server actions in the
     * ServerAction class.
     * @param action action to be taken.
     * @param params parameters of the action in JSON form
     * @param onFinish callback method that will be called once a response have been received from
     *                 the server.
     */
    public void requestAction(ServerAction action, JSONObject params,
                              Response.Listener<JSONObject> onFinish) {
        this.requestAction(action, params, onFinish, null);

    }
}
