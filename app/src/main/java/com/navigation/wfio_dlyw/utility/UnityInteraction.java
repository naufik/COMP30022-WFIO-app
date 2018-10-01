package com.navigation.wfio_dlyw.utility;

import android.app.Activity;
import android.content.Intent;

import com.navigation.wfio_dlyw.navigation.ElderMaps;
import com.navigation.wfio_dlyw.navigation.ElderNavigation;

public class UnityInteraction {

    public static void goBack(Activity activity){
        Intent intent = new Intent(activity, ElderNavigation.class);
        activity.startActivity(intent);
    }

    public static void openMap(Activity activity){
        Intent intent = new Intent(activity, ElderMaps.class);
        activity.startActivity(intent);
    }

    public static void sendHelp(Activity activity){

    }
}
