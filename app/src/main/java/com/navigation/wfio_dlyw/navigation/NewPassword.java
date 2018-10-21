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

        Button newPasswordBtn = findViewById(R.id.newPasswordBtn);
        newPasswordBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                EditText newPassword = findViewById(R.id.newPassword);
                EditText retypeNewPassword = findViewById(R.id.retypeNewPassword);

                String newPasswordS = newPassword.getText().toString();
                String retypeNewPasswordS = retypeNewPassword.getText().toString();

                //checks if new password is empty
                if(newPasswordS.isEmpty()) {
                    Toast.makeText(getApplicationContext(), "Please enter a new password", Toast.LENGTH_LONG).show();
                //checks if retyped password is empty
                }else if(retypeNewPasswordS.isEmpty()){
                    Toast.makeText(getApplicationContext(), "Please Re-Type your new Password", Toast.LENGTH_LONG).show();
                //checks if passwords are the same
                }else if(!newPasswordS.equals(retypeNewPasswordS)){
                    Toast.makeText(getApplicationContext(), "Passwords do not match", Toast.LENGTH_LONG).show();
                }else{

                    //if all initial checks pass send the password to the database
                    JSONObject params = new JSONObject();
                    try {
                        params.put("password", newPasswordS);
                    } catch (JSONException e) {}
                    req.requestAction(ServerAction.USER_MODIFY_RECORD, params, t->{
                    }, new Credentials(token.getEmail(), token.getValue()));
                    Toast.makeText(NewPassword.this,"Password changed successfully",Toast.LENGTH_SHORT).show();

                    //if an elder accessed this activity send the elder back to eldersettings
                    if (token.getType().equals("ELDER")) {
                        Intent startIntent = new Intent(getApplicationContext(), ElderSettings.class);
                        startActivity(startIntent);
                        finish();
                    }
                    //if carer accessed this activity send the carer back to carersettings
                    else {
                        Intent startIntent = new Intent(getApplicationContext(), CarerSettings.class);
                        startActivity(startIntent);
                        finish();
                    }
                }
            }
        });

        //handles the what happens on click of the device's done/enter button
        EditText retypeNewPasswordEnter = findViewById(R.id.retypeNewPassword);
        retypeNewPasswordEnter.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if ((event != null && (event.getKeyCode() == KeyEvent.KEYCODE_ENTER)) || (actionId == EditorInfo.IME_ACTION_DONE)) {
                    //does the same thing as pressing the in-built submit button
                    newPasswordBtn.performClick();
                }
                return false;
            }
        });
    }
}
