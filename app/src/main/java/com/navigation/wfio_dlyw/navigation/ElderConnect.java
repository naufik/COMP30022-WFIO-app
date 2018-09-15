package com.navigation.wfio_dlyw.navigation;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.Button;

import com.navigation.wfio_dlyw.comms.Requester;
import com.navigation.wfio_dlyw.comms.ServerAction;
import com.navigation.wfio_dlyw.comms.Token;

import org.json.JSONException;
import org.json.JSONObject;

public class ElderConnect extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_elder_connect);
        Requester req = Requester.getInstance(this);
        Token token = Token.getInstance();

        Toolbar myToolbar = (Toolbar) findViewById(R.id.toolbarEC);
        setSupportActionBar(myToolbar);

        Button getCode = (Button) findViewById(R.id.newCodeBtn);
        getCode.setOnClickListener(view -> {
            JSONObject codeRequest = new JSONObject();
            try {
                codeRequest.put("elderId", token.getValue()).put("code", "42069322");
                req.requestAction(ServerAction.ELDER_REQUEST_CODE, codeRequest,
                        t -> {}, token.getValue());
            } catch (JSONException e) {}
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
