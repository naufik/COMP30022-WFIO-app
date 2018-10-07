package com.navigation.wfio_dlyw.twilio;

import android.app.NotificationManager;
import android.content.Context;
import android.widget.Toast;

import com.navigation.wfio_dlyw.navigation.CallActivity;
import com.twilio.voice.Call;
import com.twilio.voice.CallException;
import com.twilio.voice.CallInvite;

import java.util.function.UnaryOperator;

public class TwilioUtils {

    private static TwilioUtils instance;
    private NotificationManager notificationManager;

    private CallInvite activeCallInvite;
    private Call activeCall;
    private int activeCallNotificationId;

    public static synchronized TwilioUtils getInstance() {
        if (instance == null) {
            instance = new TwilioUtils();
        }
        return instance;
    }

    public void receiveCall(int notificationId, CallInvite invite) {
        this.activeCallInvite = invite;
        this.activeCallNotificationId = notificationId;
    }

    private void setCall(Call call) {
        this.activeCall = call;
    }

    public void declineCall(Context ctx) {
        if (this.activeCallInvite != null) {
            this.activeCallInvite.reject(ctx);
            this.activeCallInvite = null;
        }
    }

    public CallInvite getActiveCallInvite() {
        return activeCallInvite;
    }

    public Call getCall() {
        return activeCall;
    }

    public static Call.Listener buildListener(Context ctx,
                                              UnaryOperator<Call> onConnect,
                                              UnaryOperator<Call> onError,
                                              UnaryOperator<Call> onDisconnect) {
        return new Call.Listener() {
            @Override
            public void onConnectFailure(Call call, CallException e) {
                if (onError != null) {
                    onError.apply( call );
                }
                if (e != null) {
                    Toast.makeText(ctx, e.getMessage(), Toast.LENGTH_LONG)
                            .show();
                    e.printStackTrace();
                }
            }

            @Override
            public void onConnected(Call call) {
                if (onConnect != null) {
                    onConnect.apply( call );
                }
                TwilioUtils.getInstance().activeCall = call;
            }

            @Override
            public void onDisconnected(Call call, CallException e) {
                if (onDisconnect != null) {
                    onDisconnect.apply(call);
                }
                if (e != null) {
                    Toast.makeText(ctx, e.getMessage(), Toast.LENGTH_LONG)
                            .show();
                    e.printStackTrace();
                }
            }
        };
    }
}
