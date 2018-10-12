package com.navigation.wfio_dlyw.utility;

import android.app.Activity;
import android.content.Intent;

import com.navigation.wfio_dlyw.comms.Credentials;
import com.navigation.wfio_dlyw.comms.Requester;
import com.navigation.wfio_dlyw.comms.ServerAction;
import com.navigation.wfio_dlyw.comms.Token;
import com.navigation.wfio_dlyw.navigation.ElderHome;
import com.navigation.wfio_dlyw.navigation.ElderMaps;
import com.navigation.wfio_dlyw.navigation.ElderNavigation;
import com.unity3d.player.UnityPlayer;

public class UnityInteraction {

    public static void requestService(){
        
        UnityPlayer.UnitySendMessage("Camera","ReceiveMessage", "You got message");
    }

    public static void goBack(Activity activity){
        Intent intent = new Intent(activity, ElderHome.class);
        activity.startActivity(intent);
    }

    public static void openMap(Activity activity){
        Intent intent = new Intent(activity, ElderMaps.class);
        activity.startActivity(intent);
    }

    public static void sendHelp(Activity activity){
        Requester minta = Requester.getInstance(activity.getApplicationContext());
        Token var = Token.getInstance();
        minta.requestAction(ServerAction.CARER_SIGNAL, null, response -> {}, new Credentials(var.getEmail(), var.getValue()));
    }
}
