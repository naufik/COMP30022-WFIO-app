package com.navigation.wfio_dlyw.twilio;

import android.app.NotificationManager;
import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import com.navigation.wfio_dlyw.navigation.CallActivity;
import com.twilio.voice.Call;
import com.twilio.voice.CallException;
import com.twilio.voice.CallInvite;

import java.util.function.UnaryOperator;

public class TwilioUtils {

    public interface TwilioCallListener {
        public void onConnected(Call call);

        public void onDisconnected(Call call);

        public void onFailure(Call call);
    }

    private static TwilioUtils instance;
    private static Context pastContext;

    private CallInvite activeCallInvite;
    private Call activeCall;
    private int activeCallNotificationId;

    public static synchronized TwilioUtils getInstance(Context context) {
        if (instance == null) {
            instance = new TwilioUtils();
            if (context != null) {
                instance.pastContext = context;
            }
        }

        return instance;
    }

    public void receiveCall(int notificationId, CallInvite invite) {
        if (this.activeCall != null || this.activeCallInvite != null) {
            invite.reject(this.pastContext);
            return;
        }
        this.activeCallInvite = invite;
        this.activeCallNotificationId = notificationId;
    }

    public void acceptCall(TwilioCallListener listener) {
        if (this.activeCallInvite != null) {
            activeCallInvite.accept(pastContext,
                    TwilioUtils.buildListener(pastContext, listener));
        }
        this.activeCallInvite = null;
    }

    public void declineCall() {
        if (this.activeCallInvite != null) {
            this.activeCallInvite.reject(this.pastContext);
        }
        this.activeCallInvite = null;
    }

    public CallInvite getActiveCallInvite() {
        return activeCallInvite;
    }

    public Call getCall() {
        return activeCall;
    }

    public static Call.Listener buildListener(Context ctx,
                                              TwilioCallListener listener) {
        return new Call.Listener() {
            @Override
            public void onConnectFailure(Call call, CallException e) {
                if (listener != null) {
                    listener.onFailure( call );
                }
                if (e != null) {
                    Toast.makeText(ctx, e.getMessage(), Toast.LENGTH_LONG)
                            .show();
                    e.printStackTrace();
                }
                instance.activeCall = null;
                instance.activeCallInvite = null;
            }

            @Override
            public void onConnected(Call call) {
                if (listener != null) {
                    listener.onConnected( call );
                }
                instance.activeCall = call;
            }

            @Override
            public void onDisconnected(Call call, CallException e) {
                if (listener != null) {
                    listener.onDisconnected(call);
                }
                if (e != null) {
                    Toast.makeText(ctx, e.getMessage(), Toast.LENGTH_LONG)
                            .show();
                    e.printStackTrace();
                }
                instance.activeCall = null;
                instance.activeCallInvite = null;
            }
        };
    }

    public int getCurrentCallNotificationId() {
        return this.activeCallNotificationId;
    }
}
