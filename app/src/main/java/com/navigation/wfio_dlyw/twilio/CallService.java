package com.navigation.wfio_dlyw.twilio;

import android.app.IntentService;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;
import android.os.IInterface;
import android.os.Parcel;
import android.os.RemoteException;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.content.LocalBroadcastManager;

import com.twilio.voice.Call;

import java.io.FileDescriptor;

public class CallService extends Service {
    public static String ON_DISCONNECT = "call.ondisconnect";
    public static String ON_CONNECT = "call.onconnected";
    public static String ON_FAILURE = "call.onfailure";

    public CallService() {
        super();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        handleIntent(intent);
        return super.onStartCommand( intent, flags, startId );
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

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



    private void handleIntent(@Nullable Intent intent) {
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
    }

    private void acceptCall(Intent intent) {
        twilio.acceptCall(new TwilioUtils.TwilioCallListener() {
            @Override
            public void onConnected(Call call) {
                Intent i = new Intent();
                i.setAction("call.onconnected");
                sendBroadcast(i);
            }

            @Override
            public void onDisconnected(Call call) {
                Intent i = new Intent();
                i.setAction("call.ondisconnect");
                sendBroadcast(i);
                stopSelf();
            }

            @Override
            public void onFailure(Call call) {
                Intent i = new Intent();
                i.setAction("call.onfailure");
                sendBroadcast(i);
                stopSelf();
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
                Intent i = new Intent();
                i.setAction("call.onconnected");
                sendBroadcast(i);
            }

            @Override
            public void onDisconnected(Call call) {
                Intent i = new Intent();
                i.setAction("call.ondisconnect");
                sendBroadcast(i);
                stopSelf();
            }

            @Override
            public void onFailure(Call call) {
                Intent i = new Intent();
                i.setAction( "call.onfailure" );
                sendBroadcast( i );
                stopSelf();
            }
        } );
    }
}
