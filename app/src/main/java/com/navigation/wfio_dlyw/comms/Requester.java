package com.navigation.wfio_dlyw.comms;

import android.content.Context;
import android.support.annotation.Nullable;

import com.android.volley.*;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.JsonRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

public class Requester {

    public static String SERVER_URL = "http://rawon.naufik.net:3000";

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

    public void POSTRequest(@Nullable JSONObject body, Response.Listener<JSONObject> onResponse) {
        JsonObjectRequest req = new JsonObjectRequest(Request.Method.POST, SERVER_URL,
                body, onResponse, new Response.ErrorListener(){
                    @Override
                    public void onErrorResponse(VolleyError err) {

                    }
                });
        this.requestQueue.add(req);
    }

    public void GETRequest(Response.Listener<JSONObject> onResponse) {
        JsonObjectRequest req = new JsonObjectRequest(Request.Method.GET, SERVER_URL,
                null, onResponse, new Response.ErrorListener(){
                @Override
                public void onErrorResponse(VolleyError err) {

                }
        });
        this.requestQueue.add(req);
    }

}
