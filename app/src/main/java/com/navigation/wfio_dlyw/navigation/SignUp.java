package com.navigation.wfio_dlyw.navigation;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.navigation.wfio_dlyw.comms.Requester;
import com.navigation.wfio_dlyw.comms.ServerAction;
import com.navigation.wfio_dlyw.comms.Token;

import org.json.JSONException;
import org.json.JSONObject;

public class SignUp extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle extras = getIntent().getExtras();
        Token token = Token.getInstance();

        setContentView(R.layout.activity_sign_up);
        Button submitBtn = (Button) findViewById(R.id.submitBtn);
        submitBtn.setOnClickListener(view -> {
                Intent startIntent = new Intent(getApplicationContext(), LogIn.class);
                String type = null;
                if (extras != null){
                    type = extras.getString("type");
                }

                //trim removes leading and trailing whitespace
                EditText usernameS = (EditText) findViewById(R.id.usernameS);
                String usernameSS = usernameS.getText().toString().trim();

                EditText fullnameS = (EditText) findViewById(R.id.fullNameS);
                String fullnameSS = fullnameS.getText().toString().trim();

                EditText emailS = (EditText) findViewById(R.id.emailS);
                String emailSS = emailS.getText().toString().trim();

                EditText passwordS = (EditText) findViewById(R.id.passwordS);
                String passwordSS = passwordS.getText().toString();

                EditText rePasswordS = (EditText) findViewById(R.id.rePasswordS);
                String rePasswordSS = rePasswordS.getText().toString();

                //password same as re-entered and username/email doesnt contain white space

                if(fullnameSS.isEmpty()){
                    Toast.makeText(getApplicationContext(), "Please enter your full name", Toast.LENGTH_LONG).show();
                }else if (usernameSS.isEmpty()) {
                    Toast.makeText(getApplicationContext(), "Please enter your username", Toast.LENGTH_LONG).show();
                }else if(emailSS.isEmpty()){
                    Toast.makeText(getApplicationContext(), "Please enter your E-Mail", Toast.LENGTH_LONG).show();
                }else if(passwordSS.isEmpty()){
                    Toast.makeText(getApplicationContext(), "Please enter a password", Toast.LENGTH_LONG).show();
                }else if(rePasswordSS.isEmpty()){
                    Toast.makeText(getApplicationContext(), "Please re-enter your password", Toast.LENGTH_LONG).show();
                }else if(!rePasswordSS.equals(passwordSS)){
                    Toast.makeText(getApplicationContext(), "Passwords do not match", Toast.LENGTH_LONG).show();
                }else if(usernameSS.contains(" ")){
                    Toast.makeText(getApplicationContext(), "Username cannot contain spaces", Toast.LENGTH_LONG).show();
                }else if(emailSS.contains(" ") || !emailSS.contains("@")){
                    Toast.makeText(getApplicationContext(), "Invalid E-mail address", Toast.LENGTH_LONG).show();
                }else{
                    try {
                        JSONObject params = new JSONObject();
                        params.put("username", usernameSS).put("fullName", fullnameSS)
                                .put("password", passwordSS).put("email", emailSS)
                                .put("accountType", type);

                        Requester req = Requester.getInstance(this);

                        req.requestAction(ServerAction.USER_SIGN_UP, params,
                                t -> {
                                    try {
                                        String tkn = t.getJSONObject("result").getString("token");
                                        token.setValue(tkn);
                                        token.setType(t.getJSONObject("result").getJSONObject("user").getString("accountType"));
                                        token.setId(t.getJSONObject("result").getJSONObject("user").getInt("id"));
                                        token.setEmail(t.getJSONObject("result").getJSONObject("user").getString("email"));
                                        token.setFullname(t.getJSONObject("result").getJSONObject("user").getString("fullname"));
                                        Toast.makeText(this , tkn, Toast.LENGTH_LONG).show();
                                    } catch (JSONException e) {}
                                });


                    } catch (JSONException e) {}
                    startActivity(startIntent);
                }
    });}}
