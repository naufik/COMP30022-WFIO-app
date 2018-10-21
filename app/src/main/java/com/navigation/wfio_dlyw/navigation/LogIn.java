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

import com.VoidDDQ.Cam.UnityPlayerActivity;
import com.google.firebase.FirebaseApp;
import com.navigation.wfio_dlyw.utility.*;

import com.navigation.wfio_dlyw.comms.*;

import org.json.JSONException;
import org.json.JSONObject;

/**
* Class that handles all logging in process
*/
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

        JSONObject storedC = FileIO.getCredentials(getApplicationContext());
        if (storedC != null){
            try {
                req.requestAction(ServerAction.USER_GET_INFO, null, t -> {
                    try {
                        t.getJSONObject("result").put("token", storedC.get("token"));
                        loggingIn(t, token);
                        checkAccount(token);
                    } catch (Exception e) {}
                }, new Credentials(storedC.getString("email"),storedC.getString("token")));
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

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
                                if (!t.getBoolean("ok")) {
                                    Toast.makeText(this, "Login Failed", Toast.LENGTH_SHORT).show();
                                    return;
                                }
                                this.loggingIn(t, token);
                                FileIO.storeCredentials(token.getValue(),token.getEmail(), this.getApplicationContext());
                                this.checkAccount(token);
                            } catch (JSONException e) {
                            }
                        });
            } catch (JSONException e) {}
        });

        //Start signup activity
        Button signUpBtn = findViewById(R.id.signUpBtn);
        signUpBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent startIntent = new Intent(getApplicationContext(), ChooseAccountType.class);
                startActivity(startIntent);
            }
        });

        //Pressing enter during process of entering password is equal to pressing LogIn button
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

    /***
     * Login as a user and create a new session
     * @param t the user information
     * @param token token to store the information
     */
    private void loggingIn(JSONObject t, Token token){
        try {
            Log.d("Login", t.toString());
            Log.d("Login2",t.getJSONObject("result").toString());
            token.setValue(t.getJSONObject("result").getString("token"));
            token.setType(t.getJSONObject("result").getJSONObject("user").getString("accountType"));
            Log.d("Login2", "4");
            token.setEmail(t.getJSONObject("result").getJSONObject("user").getString("email"));
            Log.d("Login2", "5");
            token.setFullname(t.getJSONObject("result").getJSONObject("user").getString("fullname"));
            token.setUsername(t.getJSONObject("result").getJSONObject("user").getString("username"));
            Log.d("Login2", "6");

        } catch (Exception e) {e.printStackTrace();}
    }

    /***
     * check whether the inserted data are valid
     * @param token token that store the data
     */
    private void checkAccount(Token token){
        Intent startIntent;
        Requester req = Requester.getInstance(getApplicationContext());
        Text2Speech t2t = new Text2Speech(getApplicationContext());
        if (token.getType().equals("ELDER")) {

            req.requestAction(ServerAction.USER_GET_INFO, null, t2 -> {
                try {
                    token.setConnections(t2.getJSONObject("result").getJSONObject("user").getJSONArray("carersList"));
                    token.setFavorites(t2.getJSONObject("result").getJSONObject("user").getJSONArray("favorites"));
                } catch (JSONException e) {
                }
            }, new Credentials(token.getEmail(), token.getValue()));
            startIntent = new Intent(getApplicationContext(), ElderHome.class);
        } else {
            req.requestAction(ServerAction.USER_GET_INFO, null, t2 -> {
                try {
                    token.setConnections(t2.getJSONObject("result").getJSONObject("user").getJSONArray("eldersList"));
                } catch (JSONException e) {
                }
            }, new Credentials(token.getEmail(), token.getValue()));
            finish();
            startIntent = new Intent(getApplicationContext(), CarerHome.class);
        }
        token.createSessionMessages();
        t2t.read("Welcome " + token.getFullName());
        finish();
        startActivity(startIntent);
    }

    /**
     * Pressing the back button closes the app
     */
    @Override
    public void onBackPressed() {
        // will not expect to receive data
        Intent main = new Intent(Intent.ACTION_MAIN);
        // launch the home screen
        main.addCategory(Intent.CATEGORY_HOME);
        // become start of a new task on this stack
        main.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(main);
    }
}
