package com.navigation.wfio_dlyw.navigation;

import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.koushikdutta.async.future.FutureCallback;
import com.koushikdutta.ion.Ion;
import com.navigation.wfio_dlyw.comms.Token;
import com.navigation.wfio_dlyw.twilio.TwilioUtils;
import com.twilio.voice.*;

public class CallActivity extends AppCompatActivity {

    public static final String TOKEN_ENDPOINT_URL = "https://rawon.naufik.net/voice/accessToken";

    private NotificationManager notificationManager;
    private TwilioUtils twilio = TwilioUtils.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate( savedInstanceState );
        setContentView( R.layout.activity_call );
        this.notificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
    }

    @Override
    public void onNewIntent(Intent intent) {
        if (intent.getAction().equals("call.answer")) {
            CallInvite inv = intent.getParcelableExtra( "invite");
            twilio.receiveCall( intent.getIntExtra("notificationId", 0),
                    inv);

            // placeholder events, this declines all calls as soon as they are received.
            twilio.declineCall(this);
        }
    }
}
