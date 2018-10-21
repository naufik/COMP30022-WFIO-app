package com.navigation.wfio_dlyw.navigation;

import android.app.IntentService;
import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.support.annotation.Nullable;

/***
 * Start up routine for the application
 */
public class InitializeService extends IntentService {

    /***
     * start the routine
     */
    public InitializeService() {
        super( "InitializeService" );
    }

    @Override
    protected void onHandleIntent(@Nullable Intent intent) {
        AudioManager audioMgr = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
        audioMgr.setSpeakerphoneOn(true);

    }
}
