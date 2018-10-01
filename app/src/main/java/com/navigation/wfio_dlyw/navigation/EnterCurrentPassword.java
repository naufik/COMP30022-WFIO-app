package com.navigation.wfio_dlyw.navigation;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.navigation.wfio_dlyw.comms.Credentials;
import com.navigation.wfio_dlyw.comms.Requester;
import com.navigation.wfio_dlyw.comms.ServerAction;
import com.navigation.wfio_dlyw.comms.Token;

import org.json.JSONException;
import org.json.JSONObject;


public class EnterCurrentPassword extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_enter_current_password);
        Token token = Token.getInstance();
        Requester req = Requester.getInstance(this);

        Button currentPasswordBtn = (Button)findViewById(R.id.currentPasswordBtn);
        currentPasswordBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d("KYA", "KYAAAAAAA");
                EditText currentPassword = (EditText)findViewById(R.id.currentPassword);
                String currentPasswordS = currentPassword.getText().toString();
                if(currentPasswordS.isEmpty()){
                    Log.d("KYA", "BBBBBBBB");
                    Toast.makeText(getApplicationContext(), "Please enter your current " +
                            "password", Toast.LENGTH_LONG).show();
                }
                else{
                    Log.d("KYA", "CCCCCCCCCC");
                    JSONObject params = new JSONObject();
                    try {
                        Log.d("KYA", "DDDDDDDDDDDDD");
                        params.put("username", token.getUsername()).put("password", currentPasswordS);
                        req.requestAction(ServerAction.USER_LOGIN, params, t->{
                            try {
                                if (t.getBoolean("ok")){
                                    Log.d("KYA", "EEEEEEEEEEE");
                                    if (t.getJSONObject("result").getJSONObject("user").getString("email").equals(token.getEmail())){
                                        Log.d("KYA", "FFFFFFFFFF");
                                        Toast.makeText(EnterCurrentPassword.this, "something is right?", Toast.LENGTH_SHORT).show();
                                        Intent startIntent = new Intent(getApplicationContext(), NewPassword.class);
                                        startActivity(startIntent);
                                    }
                                    else {
                                        Log.d("KYA", "GGGGGGGGGGGG");
                                        Toast.makeText(EnterCurrentPassword.this,"something is wrong?",Toast.LENGTH_SHORT).show();
                                    }
                                }
                                Log.d("KYA", "HHHHHHHHHHHHHH");
                            } catch (JSONException e) {}
                        }, new Credentials(token.getEmail(), token.getValue()));
                    } catch (JSONException e) {}

                }
            }
        });

        //for use outside of onclicklistener scope
        EditText currentPasswordEnter = (EditText)findViewById(R.id.currentPassword);
        currentPasswordEnter.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if ((event != null && (event.getKeyCode() == KeyEvent.KEYCODE_ENTER)) || (actionId == EditorInfo.IME_ACTION_DONE)) {
                    //do what you want on the press of 'done'
                    currentPasswordBtn.performClick();
                }
                return false;
            }
        });
    }
}
