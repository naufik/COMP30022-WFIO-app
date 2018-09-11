package com.navigation.wfio_dlyw.navigation;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class NewPassword extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_password);

        EditText newPassword = (EditText) findViewById(R.id.newPassword);
        EditText retypeNewPassword = (EditText) findViewById(R.id.retypeNewPassword);

        String newPasswordS = newPassword.getText().toString();
        String retypeNewPasswordS = retypeNewPassword.getText().toString();


        Button newPasswordBtn = (Button)findViewById(R.id.newPasswordBtn);
        newPasswordBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //as of now it goes back to elder settings even if your originally login as a carer
                if(newPasswordS.equals(retypeNewPasswordS)){
                    Intent startIntent = new Intent(getApplicationContext(), ElderSettings.class);
                    startActivity(startIntent);
                }else{
                    Toast.makeText(getApplicationContext(), "Passwords do not match", Toast.LENGTH_LONG).show();
                }
            }
        });
    }
}
