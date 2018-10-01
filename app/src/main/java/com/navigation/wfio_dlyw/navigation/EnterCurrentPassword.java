package com.navigation.wfio_dlyw.navigation;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;


public class EnterCurrentPassword extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_enter_current_password);

        Button currentPasswordBtn = findViewById(R.id.currentPasswordBtn);
        currentPasswordBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                EditText currentPassword = findViewById(R.id.currentPassword);
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

        //for use outside of onclicklistener scope
        EditText currentPasswordEnter = findViewById(R.id.currentPassword);
        currentPasswordEnter.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if ((event != null && (event.getKeyCode() == KeyEvent.KEYCODE_ENTER)) || (actionId == EditorInfo.IME_ACTION_DONE)) {
                    //do what you want on the press of 'done'
                    currentPasswordBtn.performClick();
                }
                return false;
            }
        });
    }
}
