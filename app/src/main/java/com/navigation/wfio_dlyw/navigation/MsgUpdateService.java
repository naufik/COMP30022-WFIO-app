package com.navigation.wfio_dlyw.navigation;

import android.app.IntentService;
import android.app.Service;
import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.widget.Toast;

import com.navigation.wfio_dlyw.comms.Credentials;
import com.navigation.wfio_dlyw.comms.Requester;
import com.navigation.wfio_dlyw.comms.ServerAction;
import com.navigation.wfio_dlyw.comms.Token;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Array;
import java.util.HashMap;
import java.util.Timer;
import java.util.TimerTask;

public class MsgUpdateService extends IntentService {
    private static Timer timer;

    private Handler h = new Handler();

    public MsgUpdateService() {
        super("MsgUpdateService");
    }

    @Override
    protected void onHandleIntent(@Nullable Intent intent) {
        String action = intent.getAction();
        if (action.equals("poll")) {

            this.startPolling();
        } else if (action.equals("stop")) {
            this.stopPolling();
        } else {
          Toast.makeText(this.getApplicationContext(), "Invalid intent request",
                  Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public void onCreate() {
        if (timer == null) {
            timer = new Timer();
        }
        super.onCreate();
    }

    private void startPolling() {
        Requester req = Requester.getInstance(this);
        Token token = Token.getInstance();
        TimerTask task = new TimerTask() {

            @Override
            public void run() {
                h.post(() -> {
                    req.requestAction(ServerAction.MESSAGE_PULL, null, t->{
                        try {
                            for (int i=0; i<t.getJSONObject("result").getJSONArray("messages").length(); i++){
                                token.getMessages().put(t.getJSONObject("result").getJSONArray("messages").getJSONObject(i));
                            }
                        }catch(JSONException e) {}
                    }, new Credentials(token.getEmail(), token.getValue()));
                    Toast.makeText(getApplicationContext(), "Requested!", Toast.LENGTH_LONG).show();
                });
            }
        };
        timer.schedule(task, 0,1000);
    }

    private void stopPolling() {
        if (timer != null) {
            timer.cancel();
        }
    }

}
