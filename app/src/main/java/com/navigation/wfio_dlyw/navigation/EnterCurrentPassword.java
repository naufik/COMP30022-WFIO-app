package com.navigation.wfio_dlyw.navigation;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class EnterCurrentPassword extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_enter_current_password);



        Button currentPasswordBtn = (Button)findViewById(R.id.currentPasswordBtn);
        currentPasswordBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                EditText currentPassword = (EditText)findViewById(R.id.currentPassword);
                String currentPasswordS = currentPassword.getText().toString();
                if(currentPasswordS.isEmpty()){
                    Toast.makeText(getApplicationContext(), "Please enter your current " +
                            "password", Toast.LENGTH_LONG).show();
                }
                else{
                    Intent startIntent = new Intent(getApplicationContext(), NewPassword.class);
                    startActivity(startIntent);
                }
            }
        });
    }
}
