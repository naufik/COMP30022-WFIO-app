package com.navigation.wfio_dlyw.navigation;

import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.koushikdutta.async.future.FutureCallback;
import com.koushikdutta.ion.Ion;
import com.navigation.wfio_dlyw.comms.Token;
import com.twilio.voice.*;

public class CallActivity extends AppCompatActivity {

    public static final String TOKEN_ENDPOINT_URL = "https://rawon.naufik.net/voice/accessToken";

    private Call activeCall;
    private String callTo;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate( savedInstanceState );
        setContentView( R.layout.activity_call );
    }

    private void obtainToken() {
        String identity = Token.getInstance().getUsername();
        Ion.with(this).load(TOKEN_ENDPOINT_URL + "?identity=" + identity).asString().setCallback(new FutureCallback<String>() {
            @Override
            public void onCompleted(Exception e, String accessToken) {
                if (e == null) {
                    Token.getInstance().setVoiceToken(accessToken);
                } else {
                    Toast.makeText(CallActivity.this, "error jancuk",
                            Toast.LENGTH_LONG).show();
                }
            }
        });
    }
}
