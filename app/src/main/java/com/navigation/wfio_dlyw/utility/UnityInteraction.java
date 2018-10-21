package com.navigation.wfio_dlyw.utility;

import android.app.Activity;
import android.content.Intent;
import android.widget.Toast;

import com.navigation.wfio_dlyw.comms.Token;
import com.navigation.wfio_dlyw.navigation.ElderHome;
import com.navigation.wfio_dlyw.navigation.ElderMaps;
import com.navigation.wfio_dlyw.navigation.MessageListElder;

public class UnityInteraction {

    /**
     * This takes back to the previous activity without actually destroying the AR. So the user
     * will not see the logo screen again when going to AR.
     * @param activity where it is the UnityPlayerActivity
     */
    public static void goBack(Activity activity){
        Intent intent = new Intent(activity, ElderHome.class);
        activity.startActivity(intent);
    }

    /**
     * It will be called if the phone's orientation is close to flat, it will switch to maps
     * @param activity where it is the UnityPlayerActivity
     */
    public static void openMap(Activity activity){
        Intent intent = new Intent(activity, ElderMaps.class);
        activity.startActivity(intent);
    }

    /**
     * It is for a button in AR where it will go to the text chat activity if and only if
     * there exists a carer that is connected to this elder. Otherwise it will give a toast
     * prompt
     * @param activity where it is the UnityPlayerActivity
     */
    public static void goChat(Activity activity){
        if (Token.getInstance(activity).getCurrentConnection() != null) {
            Intent smsintent = new Intent(activity, MessageListElder.class);
            activity.startActivity(smsintent);
        }else{
            Toast.makeText(activity, "Please connect to a Carer to enable messaging", Toast.LENGTH_LONG).show();
        }
    }
}
