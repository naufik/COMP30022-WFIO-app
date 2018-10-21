package com.navigation.wfio_dlyw.twilio;

import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.Toast;

import com.navigation.wfio_dlyw.comms.Token;
import com.navigation.wfio_dlyw.navigation.CallActivity;
import com.twilio.voice.Call;
import com.twilio.voice.CallException;
import com.twilio.voice.CallInvite;
import com.twilio.voice.Voice;

import java.util.HashMap;
import java.util.function.UnaryOperator;

/**
 * Singleton utility to deal with Twilio interactions.
 */
public class TwilioUtils {

    /**
     * A callback interface that defines actions that happen after an event has happened
     * to a call.
     */
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

    /**
     * Obtains an instance of the Twilio utilities.
     * @param context THe context that requests the instance.
     * @return The twilio manager instance.
     */
    public static synchronized TwilioUtils getInstance(Context context) {
        if (instance == null) {
            instance = new TwilioUtils();
            if (context != null) {
                instance.pastContext = context;
            }
        }

        return instance;
    }

    /**
     * Registers a call invite and displays it to the user.
     * @param notificationId The ID of the notification that contains the call. This notification
     *                       will be dismissed.
     * @param invite The Twilio call invite object that packs an incoming call.
     */
    public void receiveCall(int notificationId, CallInvite invite) {
        if (this.activeCall != null || this.activeCallInvite != null) {
            invite.reject(this.pastContext);
            return;
        }
        this.activeCallInvite = invite;
        this.activeCallNotificationId = notificationId;
    }

    /**
     * Accepts a call.
     *
     * @param listener The series of callbacks or Twilio event listeners that will be called when
     *                 events happen on the answered call.
     */
    public void acceptCall(TwilioCallListener listener) {
        if (this.activeCallInvite != null) {
            activeCallInvite.accept(pastContext,
                    TwilioUtils.buildListener(pastContext, listener));
        }
        this.activeCallInvite = null;
    }

    /**
     * Declines the currently registered call invite.
     */
    public void declineCall() {
        if (this.activeCallInvite != null) {
            this.activeCallInvite.reject(this.pastContext);
        }
        this.activeCallInvite = null;
    }

    /**
     * @return obtains the currently registered call invite.
     */
    public CallInvite getActiveCallInvite() {
        return activeCallInvite;
    }

    /**
     * @return obtains instance of currently running call. null if there is no call happening
     * in the background.
     */
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

    /**
     * @return the currently registered call notification ID
     */
    public int getCurrentCallNotificationId() {
        return this.activeCallNotificationId;
    }

    /**
     * Starts a call to a recipient.
     * @param userName The username of the person that we want to call.
     * @param listener A series of callback methods that will be called when certain events happen
     *                 to the started up call.
     */
    public void startCall(String userName, TwilioCallListener listener) {
        HashMap<String, String> twiMlParams = new HashMap<>();
        twiMlParams.put("to", userName);

        instance.activeCall = Voice.call(
          this.pastContext, Token.getInstance(this.pastContext).getVoiceToken(), twiMlParams,
                TwilioUtils.buildListener(this.pastContext, listener));
    }
}
