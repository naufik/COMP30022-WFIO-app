package com.navigation.wfio_dlyw.navigation;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class ElderSettings extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_elder_settings);
        //instead of doing this, setHint "onCreate" by grabbing the user's current data

        EditText fullname = (EditText) findViewById(R.id.fullNameES);
        EditText lastname = (EditText) findViewById(R.id.usernameES);
        EditText email = (EditText) findViewById(R.id.emailES);

        String fullnameS = fullname.getText().toString();
        String lastnameS = lastname.getText().toString();
        String emailS = email.getText().toString();

        Button applyChangesES = (Button) findViewById(R.id.applyChangesES);
        applyChangesES.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!fullnameS.isEmpty()) {
                    fullname.setHint(fullnameS);
                    Toast.makeText(getApplicationContext(), "Changes Applied", Toast.LENGTH_LONG).show();
                }
                if (!lastnameS.isEmpty()) {
                    lastname.setHint(lastnameS);
                }
                if (!emailS.isEmpty()) {
                    email.setHint(emailS);
                }
            }
        });


        Toolbar myToolbar = (Toolbar) findViewById(R.id.toolbarES);
        setSupportActionBar(myToolbar);

        Button elderLogOutBtn = (Button)findViewById(R.id.elderLogOutBtn);
        elderLogOutBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent startIntent = new Intent(getApplicationContext(), LogIn.class);
                startActivity(startIntent);
            }
        });

        Button changePassword = (Button)findViewById(R.id.changePasswordES);
        changePassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent startIntent = new Intent(getApplicationContext(), EnterCurrentPassword.class);
                startActivity(startIntent);
            }
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
