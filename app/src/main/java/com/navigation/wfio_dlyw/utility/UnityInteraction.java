package com.navigation.wfio_dlyw.utility;

import android.app.Activity;
import android.content.Intent;
import android.widget.Toast;

import com.navigation.wfio_dlyw.comms.Token;
import com.navigation.wfio_dlyw.navigation.ElderHome;
import com.navigation.wfio_dlyw.navigation.ElderMaps;
import com.navigation.wfio_dlyw.navigation.MessageListElder;
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

    public static void goChat(Activity activity){
        if (Token.getInstance(activity).getCurrentConnection() != null) {
            Intent smsintent = new Intent(activity, MessageListElder.class);
            activity.startActivity(smsintent);
        }else{
            Toast.makeText(activity, "Please connect to a Carer to enable messaging", Toast.LENGTH_LONG).show();
        }
    }

    public static void callPhone(Activity activity){
        if (Token.getInstance(activity).getCurrentConnection() != null) {
            Intent callintent = new Intent(activity, MessageListElder.class);
            activity.startActivity(callintent);
        }else{
            Toast.makeText(activity, "Please connect to a Carer to enable voice call", Toast.LENGTH_LONG).show();
        }
    }

    public static void sendHelp(Activity activity){

    }
}
