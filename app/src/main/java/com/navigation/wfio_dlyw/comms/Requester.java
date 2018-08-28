package com.navigation.wfio_dlyw.comms;

import android.content.Context;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;

public class Requester {

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

}
