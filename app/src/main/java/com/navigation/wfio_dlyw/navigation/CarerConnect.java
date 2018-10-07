package com.navigation.wfio_dlyw.navigation;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.navigation.wfio_dlyw.comms.Requester;
import com.navigation.wfio_dlyw.comms.ServerAction;
import com.navigation.wfio_dlyw.comms.Token;
import com.navigation.wfio_dlyw.comms.Credentials;

import org.json.JSONException;
import org.json.JSONObject;


public class CarerConnect extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_carer_connect);

        Toolbar myToolbar = findViewById(R.id.toolbarCC);
        setSupportActionBar(myToolbar);
        Requester req = Requester.getInstance(this);
        Token token = Token.getInstance();

        Toast.makeText(this , token.getType(), Toast.LENGTH_LONG).show();

        EditText input = findViewById(R.id.verificationCodeCC);
        Button link = findViewById(R.id.link);

        link.setOnClickListener(view -> {
            String code = input.getText().toString();
            try {
                JSONObject linkRequest = new JSONObject();
                linkRequest.put("code", code);

                req.requestAction(ServerAction.CARER_LINK, linkRequest,
                        t-> {
                            try {
                                String s = t.getJSONObject("result").getString("elderId");
                                Toast.makeText(this , s, Toast.LENGTH_LONG).show();
                            } catch (JSONException e) {}
                        }, new Credentials(token.getEmail(), token.getValue()));
            } catch (JSONException e) {}

            req.requestAction(ServerAction.USER_GET_INFO, null, t2 -> {
                try {
                    token.setConnections(t2.getJSONObject("result").getJSONObject("user").getJSONArray("eldersList"));
                } catch (JSONException e) {}
            }, new Credentials(token.getEmail(), token.getValue()));
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
