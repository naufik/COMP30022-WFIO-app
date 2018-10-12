package com.navigation.wfio_dlyw.navigation;

import android.app.IntentService;
import android.content.Intent;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.util.Log;
import android.widget.Toast;

import com.navigation.wfio_dlyw.comms.Credentials;
import com.navigation.wfio_dlyw.comms.Requester;
import com.navigation.wfio_dlyw.comms.ServerAction;
import com.navigation.wfio_dlyw.comms.Token;

import org.json.JSONException;
import org.json.JSONObject;

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
        this.stopPolling();
        Requester req = Requester.getInstance(this);
        TimerTask task = new TimerTask() {

            @Override
            public void run() {
                h.post(() -> {
                    Token token = Token.getInstance();
                    req.requestAction(ServerAction.MESSAGE_PULL, null, t->{
                        try {
                            for (int i=0; i<t.getJSONObject("result").getJSONArray("messages").length(); i++){
                                JSONObject message = t.getJSONObject("result").getJSONArray("messages").getJSONObject(i);
                                token.getServerMessages().put(message);
                            }
                        }catch(JSONException e) {}
                    }, new Credentials(token.getEmail(), token.getValue()));
                });
            }
        };
        timer = new Timer();
        timer.schedule(task, 0,500);
    }

    private void stopPolling() {
        if (timer != null) {
            timer.cancel();
        }
    }


}