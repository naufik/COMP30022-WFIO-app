package com.navigation.wfio_dlyw.navigation;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

public class LogIn extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_log_in);
        //for the moment it only redirects to the elder's home page
        Button enterBtn = (Button)findViewById(R.id.enterBtn);
        enterBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                EditText username = (EditText) findViewById(R.id.username);
                EditText password = (EditText) findViewById(R.id.password);

                String user = username.getText().toString();
                String pass = password.getText().toString();

                if (user.equals("Elder") && pass.equals("123")) {
                    Intent startIntent = new Intent(getApplicationContext(), ElderHome.class);
                    startActivity(startIntent);
                }
                if (user.equals("Carer") && pass.equals("123")) {
                    Intent startIntent = new Intent(getApplicationContext(), CarerHome.class);
                    startActivity(startIntent);
                }
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
    }
}
