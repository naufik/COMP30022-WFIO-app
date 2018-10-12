package com.navigation.wfio_dlyw.navigation;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;


public class ChooseAccountType extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_choose_account_type);
        ImageView chooseCarer = findViewById(R.id.chooseCarer);
        chooseCarer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent startIntent = new Intent(getApplicationContext(), SignUp.class);
                startIntent.putExtra("type", "CARER");
                startActivity(startIntent);
            }
        });
        ImageView chooseElder = findViewById(R.id.chooseElder);
        chooseElder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent startIntent = new Intent(getApplicationContext(), SignUp.class);
                startIntent.putExtra("type", "ELDER");
                startActivity(startIntent);
            }
        });

        TextView carerText = findViewById(R.id.carerText);
        carerText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent startIntent = new Intent(getApplicationContext(), SignUp.class);
                startIntent.putExtra("type", "CARER");
                startActivity(startIntent);
            }
        });
        TextView elderText = findViewById(R.id.elderText);
        elderText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent startIntent = new Intent(getApplicationContext(), SignUp.class);
                startIntent.putExtra("type", "ELDER");
                startActivity(startIntent);
            }
        });

    }
}
