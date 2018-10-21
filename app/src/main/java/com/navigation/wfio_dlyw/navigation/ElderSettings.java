package com.navigation.wfio_dlyw.navigation;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.VoidDDQ.Cam.GeoStatService;
import com.VoidDDQ.Cam.UnityPlayerActivity;
import com.navigation.wfio_dlyw.comms.Credentials;
import com.navigation.wfio_dlyw.comms.Requester;
import com.navigation.wfio_dlyw.comms.ServerAction;
import com.navigation.wfio_dlyw.comms.Token;
import com.navigation.wfio_dlyw.utility.FileIO;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ElderSettings extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_elder_settings);
        Requester req = Requester.getInstance(this);
        Token token = Token.getInstance();

        //instead of doing this, setHint "onCreate" by grabbing the user's current data

        EditText fullname = findViewById(R.id.fullNameES);
        TextView email = findViewById(R.id.emailES);
        TextView username = findViewById(R.id.usernameES);

        //get Information from server :)
        req.requestAction(ServerAction.USER_GET_INFO, null,
                t-> {
                    try {
                        fullname.setHint(t.getJSONObject("result").getJSONObject("user").getString("fullname"));
                        email.setText(t.getJSONObject("result").getJSONObject("user").getString("email"));
                        username.setText(t.getJSONObject("result").getJSONObject("user").getString("username"));
                    } catch (JSONException e) {
                    }
                }, new Credentials(token.getEmail(), token.getValue()));

        Button applyChangesES = (Button) findViewById(R.id.applyChangesES);
        applyChangesES.setOnClickListener(view -> {
            String fullnameS = fullname.getText().toString();
            try {
                Pattern p = Pattern.compile("^[ A-Za-z]+$");
                JSONObject params = new JSONObject();
                if (!fullnameS.isEmpty()) {
                    if (p.matcher(fullnameS).matches()) {
                        params.put("fullName", fullnameS);
                        req.requestAction(ServerAction.USER_MODIFY_RECORD, params, t -> {
                                try {
                                    if (t.getBoolean("ok")) {
                                        Toast.makeText(ElderSettings.this, "Full name changed successfully", Toast.LENGTH_LONG).show();
                                        fullname.setHint(fullnameS);
                                        token.setFullname(fullnameS);
                                    }
                                } catch (JSONException e) {}
                        }, new Credentials(token.getEmail(), token.getValue()));
                    }
                    else {
                        Toast.makeText(this, "Please only usse alphabets and spaces", Toast.LENGTH_LONG).show();
                    }
                }
                else {
                    Toast.makeText(this, "Please insert a valid full name", Toast.LENGTH_LONG).show();
                }
            } catch (JSONException e) {}
        });


        Toolbar myToolbar = findViewById(R.id.toolbarES);
        setSupportActionBar(myToolbar);

        Button elderLogOutBtn = findViewById(R.id.elderLogOutBtn);
        elderLogOutBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Token.reset();
                if (FileIO.deleteCredentials(getApplicationContext())){
                    Log.d("ElderLogOut", "Credentials deleted");
                }
                else {
                    Log.d("ElderLogOut", "Credentials NOT deleted");
                }

                Intent intent = new Intent(getApplicationContext(), LogIn.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
            }
        });

        Button changePassword = findViewById(R.id.changePasswordES);
        changePassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent startIntent = new Intent(getApplicationContext(), EnterCurrentPassword.class);
                startActivity(startIntent);
            }
        });

        Button connectBtn = findViewById(R.id.rncButton);
        connectBtn.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){
                Intent startIntent = new Intent(getApplicationContext(), ElderConnect.class);
                startActivity(startIntent);
            }
        });


    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.back,menu);
        return true;
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.back_button:
                Intent startIntent = new Intent(getApplicationContext(), ElderHome.class);
                startActivity(startIntent);
                return true;

            default:
                // If we got here, the user's action was not recognized.
                // Invoke the superclass to handle it.
                return super.onOptionsItemSelected(item);
        }
    }
}
