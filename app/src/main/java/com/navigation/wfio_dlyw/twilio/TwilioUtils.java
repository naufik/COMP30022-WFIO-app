package com.navigation.wfio_dlyw.twilio;

import android.app.NotificationManager;
import android.content.Context;

import com.navigation.wfio_dlyw.navigation.CallActivity;
import com.twilio.voice.Call;
import com.twilio.voice.CallInvite;

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

    public void acceptCall(Call call) {
        this.activeCall = call;
    }

    public void declineCall(Context ctx) {
        if (this.activeCallInvite != null) {
            this.activeCallInvite.reject(ctx);
        }
    }

    public CallInvite getActiveCallInvite() {
        return activeCallInvite;
    }

    public Call getCall() {
        return activeCall;
    }
}
