package com.navigation.wfio_dlyw.comms;

import android.app.IntentService;
import android.content.Intent;
import android.support.annotation.Nullable;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * A notification handler used to notify the other party in a connection that the other has agreed
 * to help them or if they have requested for help.
 *
 * @author Naufal Fikri (http://github.com/naufik)
 */
public class NotifyService extends IntentService {
    public NotifyService() {
        super("NotifyService");
    }

    @Override
    protected void onHandleIntent(@Nullable Intent intent) {
        try {
            Requester.getInstance( this ).requestAction( ServerAction.CARER_ACCEPT,
                    new JSONObject().put( "elderEmail", intent.getStringExtra( "to" ) ),
                    t -> {
                        // to be implemented later.
                    },
                    new Credentials( Token.getInstance().getEmail(), Token.getInstance().getValue() )
            );
        } catch (JSONException e) {}
    }
}
