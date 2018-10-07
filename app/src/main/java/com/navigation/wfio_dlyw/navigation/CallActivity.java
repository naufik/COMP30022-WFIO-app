package com.navigation.wfio_dlyw.navigation;

import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.net.rtp.AudioStream;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.Toast;

import com.koushikdutta.async.future.FutureCallback;
import com.koushikdutta.ion.Ion;
import com.navigation.wfio_dlyw.comms.Token;
import com.navigation.wfio_dlyw.twilio.TwilioUtils;
import com.twilio.voice.*;

import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.Callable;

public class CallActivity extends AppCompatActivity {

    public static final String TOKEN_ENDPOINT_URL = "https://rawon.naufik.net/voice/accessToken";

    private NotificationManager notificationManager;
    private TwilioUtils twilio = TwilioUtils.getInstance(this);

    Button endCallButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate( savedInstanceState );
        setContentView( R.layout.activity_call );

        endCallButton = findViewById(R.id.endCall);
        endCallButton.setOnClickListener(v->{
            twilio.getCall().disconnect();
        });

        this.notificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        handleCallIntent(this.getIntent());

        setVolumeControlStream(AudioManager.STREAM_VOICE_CALL);

        ensureMicrophoneAccess();
    }

    private void ensureMicrophoneAccess() {

    }


    @Override
    public void onNewIntent(Intent intent) {
        handleCallIntent(intent);
    }

    private void handleCallIntent(Intent intent) {
        if (intent.getAction().equals("call.answer")) {
            CallInvite inv = intent.getParcelableExtra( "invite");
            twilio.receiveCall(intent.getIntExtra("notificationId", 0),
                    inv);

            twilio.acceptCall(new TwilioUtils.TwilioCallListener() {
                @Override
                public void onConnected(Call call) {

                }

                @Override
                public void onDisconnected(Call call) {
                    CallActivity.this.finish();
                }

                @Override
                public void onFailure(Call call) {
                    CallActivity.this.finish();
                }
            } );

            this.notificationManager.cancel(twilio.getCurrentCallNotificationId());
        } else if (intent.getAction().equals("call.start")) {
            String recipient = intent.getStringExtra("to");

            twilio.getInstance(this).startCall( recipient, new TwilioUtils.TwilioCallListener() {
                @Override
                public void onConnected(Call call) {

                }

                @Override
                public void onDisconnected(Call call) {
                    CallActivity.this.finish();
                }

                @Override
                public void onFailure(Call call) {
                    CallActivity.this.finish();
                }
            } );
        }
    }

    @Override
    public void onStop() {
        super.onStop();
        twilio.getCall().disconnect();
    }
}
