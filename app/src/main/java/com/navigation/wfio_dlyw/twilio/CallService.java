package com.navigation.wfio_dlyw.twilio;

import android.app.IntentService;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.support.annotation.Nullable;

import com.twilio.voice.Call;

public class CallService extends IntentService {

    private TwilioUtils twilio;

    public static abstract class CallServiceReceiver extends BroadcastReceiver {
        public abstract void onDisconnect();

        public abstract void onConnected();

        public abstract void onCallFailure();

        @Override
        public void onReceive(Context context, Intent intent) {
            switch (intent.getAction()) {
                case "call.ondisconnect":
                    onDisconnect();
                    break;
                case "call.onconnected":
                    onConnected();
                    break;
                case "call.onfailure":
                    onCallFailure();
                    break;
                default:
                    break;
            }
        }
    }

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
            case "call.answer":
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
        sendBroadcast(intent);
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
