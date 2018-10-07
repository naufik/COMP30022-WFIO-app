package com.navigation.wfio_dlyw.twilio;

import android.app.IntentService;
import android.content.Intent;
import android.support.annotation.Nullable;

import com.twilio.voice.Call;

public class CallService extends IntentService {

    private TwilioUtils twilio;

    public CallService() {
        super("CallService");
    }

    @Override
    protected void onHandleIntent(@Nullable Intent intent) {
        twilio = TwilioUtils.getInstance(this);
        String action = intent.getAction();
        switch (action) {
            case "call.start":
                startCall(intent);
                break;
            case "call.accept":
                acceptCall(intent);
                break;

            case "call.stop":
                disconnectCall(intent);
                break;

            case "call.decline":
                declineCall(intent);
                break;

            default:
                break;
        }
    }

    private void acceptCall(Intent intent) {
        twilio.acceptCall( new TwilioUtils.TwilioCallListener() {
            @Override
            public void onConnected(Call call) {

            }

            @Override
            public void onDisconnected(Call call) {

            }

            @Override
            public void onFailure(Call call) {

            }
        } );
    }

    private void disconnectCall(Intent intent) {
        twilio.getCall().disconnect();
    }

    private void declineCall(Intent intent) {
        twilio.declineCall();
    }



    private void startCall(Intent intent) {
        twilio.startCall(intent.getStringExtra( "to" ), new TwilioUtils.TwilioCallListener() {
            @Override
            public void onConnected(Call call) {

            }

            @Override
            public void onDisconnected(Call call) {

            }

            @Override
            public void onFailure(Call call) {

            }
        } );
    }
}
