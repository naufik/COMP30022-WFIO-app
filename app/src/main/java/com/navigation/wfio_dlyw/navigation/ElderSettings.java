package com.navigation.wfio_dlyw.navigation;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
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
import org.w3c.dom.Text;

public class ElderSettings extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_elder_settings);
        Requester req = Requester.getInstance(this);
        Token token = Token.getInstance();

        //instead of doing this, setHint "onCreate" by grabbing the user's current data

        EditText fullname = (EditText) findViewById(R.id.fullNameES);
        TextView email = (TextView) findViewById(R.id.emailES);
        TextView username = (TextView) findViewById(R.id.usernameES);

        //get Information from server :)
        req.requestAction(ServerAction.USER_GET_INFO, null,
                t-> {
                    try {
                        fullname.setHint(t.getJSONObject("result").getString("fullname"));
                        email.setText(t.getJSONObject("result").getString("email"));
                        username.setText(t.getJSONObject("result").getString("username"));

                    } catch (JSONException e) {
                    }
                }, new Credentials(token.getEmail(), token.getValue()));
        String fullnameS = fullname.getText().toString();

        Button applyChangesES = (Button) findViewById(R.id.applyChangesES);
        applyChangesES.setOnClickListener(view -> {
            try {
                JSONObject params = new JSONObject();
                if (!fullnameS.isEmpty()) {
                    params.put("fullname", fullnameS);

                    req.requestAction(ServerAction.USER_MODIFY_RECORD, params, t -> {
                        try {
                            if (t.getBoolean("ok")) {
                                Toast.makeText(this, "Full name changed successfully", Toast.LENGTH_LONG).show();
                                fullname.setHint(fullnameS);
                            }
                        } catch (JSONException e) {
                        }
                    }, new Credentials(token.getEmail(), token.getValue()));
                }
                else {
                    Toast.makeText(this, "Please insert valid full name", Toast.LENGTH_LONG).show();
                }
            } catch (JSONException e) {}
        });


        Toolbar myToolbar = (Toolbar) findViewById(R.id.toolbarES);
        setSupportActionBar(myToolbar);

        Button elderLogOutBtn = (Button)findViewById(R.id.elderLogOutBtn);
        elderLogOutBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Token.reset();
                Intent startIntent = new Intent(getApplicationContext(), LogIn.class);
                startActivity(startIntent);
            }
        });

        Button changePassword = (Button)findViewById(R.id.changePasswordES);
        changePassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent startIntent = new Intent(getApplicationContext(), EnterCurrentPassword.class);
                startActivity(startIntent);
            }
        });

        Button applicationAppearrance = (Button) findViewById(R.id.changeAppearrance);
        applicationAppearrance.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent startIntent = new Intent(getApplicationContext(), ApplicationAppearrance.class);
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
