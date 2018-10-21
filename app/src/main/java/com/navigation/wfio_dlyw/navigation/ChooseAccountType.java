package com.navigation.wfio_dlyw.navigation;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

/**
 * class for activity to chose account type before signing up
 */
public class ChooseAccountType extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_choose_account_type);

        //be a carer by clicking picture of carer
        ImageView chooseCarer = findViewById(R.id.chooseCarer);
        chooseCarer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent startIntent = new Intent(getApplicationContext(), SignUp.class);
                startIntent.putExtra("type", "CARER");
                startActivity(startIntent);
            }
        });

        //be an elder by clicking picture of elder
        ImageView chooseElder = findViewById(R.id.chooseElder);
        chooseElder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent startIntent = new Intent(getApplicationContext(), SignUp.class);
                startIntent.putExtra("type", "ELDER");
                startActivity(startIntent);
            }
        });

        //be a carer by clicking the text view
        TextView carerText = findViewById(R.id.carerText);
        carerText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent startIntent = new Intent(getApplicationContext(), SignUp.class);
                startIntent.putExtra("type", "CARER");
                startActivity(startIntent);
            }
        });

        //be an elder by clicking the text view
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
