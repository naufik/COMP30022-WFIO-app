package com.navigation.wfio_dlyw.navigation;

import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Button;

import com.navigation.wfio_dlyw.twilio.CallService;
import com.navigation.wfio_dlyw.twilio.TwilioUtils;
import com.twilio.voice.*;


/**
 * This is an intermediary activity that is used to direct notifications to accept calls as that
 * notifications are not able to be redirected to services alone but only activities.
 *
 * Passing an intent with a CallInvite object to this activity will answer the call that is wrapped
 * within the invite.
 *
 * @author Naufal Fikri (http://github.com/naufik.net)
 */
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
        this.finish();
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

            Intent answeringIntent = new Intent(this, CallService.class);
            answeringIntent.setAction("call.answer");
            answeringIntent.putExtra("invite", inv);
            startService(answeringIntent);

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
    }
}
