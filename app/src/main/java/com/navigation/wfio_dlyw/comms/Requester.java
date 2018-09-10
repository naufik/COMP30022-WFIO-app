package com.navigation.wfio_dlyw.comms;

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

    public void POSTRequest(String endpoint, @Nullable JSONObject body, Response.Listener<JSONObject> onResponse) {
        JsonObjectRequest req = new JsonObjectRequest(Request.Method.POST,
                SERVER_URL + endpoint,
                body,
                onResponse,
                new Response.ErrorListener(){
                    @Override
                    public void onErrorResponse(VolleyError err) {
                        Toast.makeText(context, err.getMessage(),
                                Toast.LENGTH_LONG);
                    }
                });
        this.addRequest(req);
    }

    public void GETRequest(String endpoint, Response.Listener<JSONObject> onResponse) {
        JsonObjectRequest req = new JsonObjectRequest(Request.Method.GET,
                SERVER_URL + endpoint,
                null,
                onResponse,
                new Response.ErrorListener(){
                    @Override
                    public void onErrorResponse(VolleyError err) {
                        Toast.makeText(context, err.getMessage(),
                                Toast.LENGTH_LONG);
                    }
        });
        this.addRequest(req);
    }

    public void getServerStatus(Function<Boolean, Void> onStatusReceived) {
        JsonObjectRequest req = new JsonObjectRequest(Request.Method.GET,
                SERVER_URL,
                null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            onStatusReceived.apply((Boolean) response.get("status"));
                        } catch (JSONException e) {

                        }
                    }
                },
                new Response.ErrorListener(){
                    @Override
                    public void onErrorResponse(VolleyError e){

                    }
                });
    }
}
