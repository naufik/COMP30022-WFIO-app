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
import android.widget.Toast;

import com.navigation.wfio_dlyw.comms.Credentials;
import com.navigation.wfio_dlyw.comms.Requester;
import com.navigation.wfio_dlyw.comms.ServerAction;
import com.navigation.wfio_dlyw.comms.Token;

import org.json.JSONException;

public class CarerSettings extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_carer_settings);
        Requester req = Requester.getInstance(this);
        Token token = Token.getInstance();

        EditText fullname = (EditText) findViewById(R.id.fullNameCS);
        EditText username = (EditText) findViewById(R.id.usernameCS);
        EditText email = (EditText) findViewById(R.id.emailCS);

        //TO-DO set hints so that it shows the user's current details
        req.requestAction(ServerAction.USER_GET_INFO, null,
                t-> {
                    try {
                        fullname.setHint(t.getJSONObject("result").getJSONObject("user").getString("fullname"));
                        email.setText(t.getJSONObject("result").getJSONObject("user").getString("email"));
                        username.setText(t.getJSONObject("result").getJSONObject("user").getString("username"));
                        Toast.makeText(this, "hehehe", Toast.LENGTH_SHORT).show();
                    } catch (JSONException e) {
                    }
                }, new Credentials(token.getEmail(), token.getValue()));

        Toolbar myToolbar = findViewById(R.id.toolbarCS);
        setSupportActionBar(myToolbar);

        final Button carerLogOutBtn = findViewById(R.id.carerLogOutBtn);
        carerLogOutBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent startIntent = new Intent(getApplicationContext(), LogIn.class);
                startActivity(startIntent);
            }
        });

        Button changePassword = findViewById(R.id.changePasswordCS);
        changePassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent startIntent = new Intent(getApplicationContext(), EnterCurrentPassword.class);
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
                Intent startIntent = new Intent(getApplicationContext(), CarerHome.class);
                startActivity(startIntent);
                return true;

            default:
                // If we got here, the user's action was not recognized.
                // Invoke the superclass to handle it.
                return super.onOptionsItemSelected(item);
        }
    }
}
