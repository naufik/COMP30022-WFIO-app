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

public class NewPassword extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_password);
        Token token = Token.getInstance();
        Requester req = Requester.getInstance(this);

        Button newPasswordBtn = (Button)findViewById(R.id.newPasswordBtn);
        newPasswordBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                EditText newPassword = (EditText) findViewById(R.id.newPassword);
                EditText retypeNewPassword = (EditText) findViewById(R.id.retypeNewPassword);

                String newPasswordS = newPassword.getText().toString();
                String retypeNewPasswordS = retypeNewPassword.getText().toString();

                //as of now it goes back to elder settings even if your originally login as a carer
                if(newPasswordS.isEmpty()) {
                    Toast.makeText(getApplicationContext(), "Please enter a new password", Toast.LENGTH_LONG).show();
                }else if(retypeNewPasswordS.isEmpty()){
                    Toast.makeText(getApplicationContext(), "Please Re-Type your new Password", Toast.LENGTH_LONG).show();
                }else if(!newPasswordS.equals(retypeNewPasswordS)){
                    Toast.makeText(getApplicationContext(), "Passwords do not match", Toast.LENGTH_LONG).show();
                }else{
                    JSONObject params = new JSONObject();
                    try {
                        params.put("password", newPasswordS);
                    } catch (JSONException e) {}
                    req.requestAction(ServerAction.USER_MODIFY_RECORD, params, t->{
                        try {
                            if (t.getBoolean("ok")){
                                Log.d("KYA", newPasswordS);
                            }
                        } catch (JSONException e) {}
                    }, new Credentials(token.getEmail(), token.getValue()));
                    Intent startIntent = new Intent(getApplicationContext(), ElderSettings.class);
                    startActivity(startIntent);
                }
            }
        });

        //for use outside of onclicklistener scope
        EditText retypeNewPasswordEnter = (EditText) findViewById(R.id.retypeNewPassword);
        retypeNewPasswordEnter.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if ((event != null && (event.getKeyCode() == KeyEvent.KEYCODE_ENTER)) || (actionId == EditorInfo.IME_ACTION_DONE)) {
                    //do what you want on the press of 'done'
                    newPasswordBtn.performClick();
                }
                return false;
            }
        });
    }
}
