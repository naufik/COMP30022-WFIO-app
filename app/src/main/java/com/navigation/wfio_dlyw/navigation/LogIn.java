package com.navigation.wfio_dlyw.navigation;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import android.view.inputmethod.EditorInfo;
import android.widget.TextView;
import android.view.KeyEvent;

import com.google.firebase.FirebaseApp;
import com.navigation.wfio_dlyw.utility.*;

import com.navigation.wfio_dlyw.comms.*;

import org.json.JSONException;
import org.json.JSONObject;

public class LogIn extends AppCompatActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Requester req = Requester.getInstance(this);
        FirebaseApp.initializeApp(this.getApplicationContext());
        Token token = Token.getInstance(this.getApplicationContext());

        Intent initIntent = new Intent(this, InitializeService.class);
        startService(initIntent);

        setContentView(R.layout.activity_log_in);
        EditText username = findViewById(R.id.username);
        EditText password = findViewById(R.id.password);
        Text2Speech t2t = new Text2Speech(getApplicationContext());

        //for the moment it only redirects to the elder's home page
        Button enterBtn = findViewById(R.id.enterBtn);
        enterBtn.setOnClickListener(view -> {

                String user = username.getText().toString();
                String pass = password.getText().toString();

                try {
                    JSONObject params = new JSONObject();
                    params.put("username", user).put("password", pass);

                req.requestAction(ServerAction.USER_LOGIN, params,
                        t -> {
                    try {
                        if(!t.getBoolean("ok")){
                            Toast.makeText(this, "Login Failed", Toast.LENGTH_SHORT).show();
                            return;
                        }
                        String s = t.getJSONObject("result").getString("token");
                        token.setValue(s);
                        token.setType(t.getJSONObject("result").getJSONObject("user").getString("accountType"));
                        //  Toast.makeText(this.getApplicationContext(), token.getType(), Toast.LENGTH_LONG).show();
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
                            token.createSessionMessages();
                            t2t.read("Welcome "+token.getFullName());
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
                            token.createSessionMessages();
                            t2t.read("Welcome "+token.getFullName()+" Nice to see you helping others today");
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
        Button signUpBtn = findViewById(R.id.signUpBtn);
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
