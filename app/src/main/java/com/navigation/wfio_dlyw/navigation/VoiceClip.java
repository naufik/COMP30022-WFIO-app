package com.navigation.wfio_dlyw.navigation;

import android.widget.Button;

public class VoiceClip extends Message {

    private Button clip;

    public VoiceClip(String text, String name, boolean belongsToCurrentUser){
        super(text,name,belongsToCurrentUser);
    }

    public void setClip(Button clip) {
        this.clip = clip;
    }

    public Button getClip() {
        return clip;
    }
}
