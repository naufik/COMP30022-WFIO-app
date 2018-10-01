package com.navigation.wfio_dlyw.navigation;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import android.view.inputmethod.EditorInfo;
import android.widget.TextView;
import android.view.KeyEvent;

import com.navigation.wfio_dlyw.comms.*;

import org.json.JSONException;
import org.json.JSONObject;

public class LogIn extends AppCompatActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Requester req = Requester.getInstance(this);
        Token token = Token.getInstance();
        /*if (token.getValue() != null) {
            if (token.getType().equals("ELDER")) {
                Intent startIntent = new Intent(getApplicationContext(), ElderHome.class);
                startActivity(startIntent);
            }
            else {
                Intent startIntent = new Intent(getApplicationContext(), CarerHome.class);
                startActivity(startIntent);
            }
        }*/

        setContentView(R.layout.activity_log_in);
        EditText username = (EditText) findViewById(R.id.username);
        EditText password = (EditText) findViewById(R.id.password);

        //for the moment it only redirects to the elder's home page
        Button enterBtn = (Button)findViewById(R.id.enterBtn);
        enterBtn.setOnClickListener(view -> {

                String user = username.getText().toString();
                String pass = password.getText().toString();

                try {
                    JSONObject params = new JSONObject();
                    params.put("username", user).put("password", pass);

                req.requestAction(ServerAction.USER_LOGIN, params,
                        t -> {
                    try {
                        String s = t.getJSONObject("result").getString("token");
                        token.setValue(s);
                        token.setType(t.getJSONObject("result").getJSONObject("user").getString("accountType"));
                        Toast.makeText(this.getApplicationContext(), token.getType(), Toast.LENGTH_LONG).show();
                        token.setId(t.getJSONObject("result").getJSONObject("user").getInt("id"));
                        token.setEmail(t.getJSONObject("result").getJSONObject("user").getString("email"));
                        token.setFullname(t.getJSONObject("result").getJSONObject("user").getString("fullname"));
                        token.setUsername(user);
                        if (token.getType().equals("ELDER")) {

                            req.requestAction(ServerAction.USER_GET_INFO, null, t2 -> {
                                try {
                                    token.setConnections(t2.getJSONObject("result").getJSONObject("user").getJSONArray("carersList"));
                                    } catch (JSONException e) {}
                             }, new Credentials(token.getEmail(), token.getValue()));

                            finish();
                            Intent startIntent = new Intent(getApplicationContext(), ElderHome.class);
                            startActivity(startIntent);
                        }
                        else {
                            req.requestAction(ServerAction.USER_GET_INFO, null, t2 -> {
                                try {
                                    token.setConnections(t2.getJSONObject("result").getJSONObject("user").getJSONArray("eldersList"));
                                } catch (JSONException e) {}
                            }, new Credentials(token.getEmail(), token.getValue()));

                            finish();
                            Intent startIntent = new Intent(getApplicationContext(), CarerHome.class);
                            startActivity(startIntent);
                        }
                    } catch (JSONException e) {}
                        });
                } catch (JSONException e) {
                }


                if (user.equals("Elder") && pass.equals("123")) {
                    Intent startIntent = new Intent(getApplicationContext(), ElderHome.class);
                    startActivity(startIntent);
                }
                if (user.equals("Carer") && pass.equals("123")) {
                    Intent startIntent = new Intent(getApplicationContext(), CarerHome.class);
                    startActivity(startIntent);
                }
            });
        Button signUpBtn = (Button)findViewById(R.id.signUpBtn);
        signUpBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent startIntent = new Intent(getApplicationContext(), ChooseAccountType.class);
                startActivity(startIntent);
            }
        });

        password.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if ((event != null && (event.getKeyCode() == KeyEvent.KEYCODE_ENTER)) || (actionId == EditorInfo.IME_ACTION_DONE)) {
                    //do what you want on the press of 'done'
                    enterBtn.performClick();
                }
                return false;
            }
        });
    }
}
