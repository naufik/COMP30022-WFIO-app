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
import java.util.Timer;
import java.util.TimerTask;

public class MsgUpdateService extends IntentService {
    Timer timer;
    public MsgUpdateService() {
        super("MsgUpdateService");
    }
    public Handler h = new Handler();

    @Override
    public int onStartCommand(@Nullable Intent intent, int flags, int startId) {
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
                });

            }
        };
        timer.schedule(task, 0,1000);
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    protected void onHandleIntent(@Nullable Intent intent) {
    }

    @Override
    public void onCreate() {
        super.onCreate();
        timer = new Timer();
    }
}
