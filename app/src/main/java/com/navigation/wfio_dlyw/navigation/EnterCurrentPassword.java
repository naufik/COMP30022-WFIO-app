package com.navigation.wfio_dlyw.navigation;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class EnterCurrentPassword extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_enter_current_password);

        Button currentPasswordBtn = (Button)findViewById(R.id.currentPasswordBtn);
        currentPasswordBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent startIntent = new Intent(getApplicationContext(), NewPassword.class);
                startActivity(startIntent);
            }
        });
    }
}
