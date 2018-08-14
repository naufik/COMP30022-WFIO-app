package com.navigation.wfio_dlyw.navigation;

import android.content.Intent;
import android.media.Image;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

public class ChooseAccountType extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_choose_account_type);
        ImageView chooseCarer = (ImageView) findViewById(R.id.chooseCarer);
        chooseCarer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent startIntent = new Intent(getApplicationContext(), SignUp.class);
                startActivity(startIntent);
            }
        });
        ImageView chooseElder = (ImageView) findViewById(R.id.chooseElder);
        chooseElder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent startIntent = new Intent(getApplicationContext(), SignUp.class);
                startActivity(startIntent);
            }
        });
    }
}
