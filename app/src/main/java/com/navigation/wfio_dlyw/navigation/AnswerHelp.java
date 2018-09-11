package com.navigation.wfio_dlyw.navigation;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Button;

public class AnswerHelp extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_answer_help);

        Button decline = (Button)findViewById(R.id.decline);
        decline.setOnClickListener(view -> {
            Intent startIntent = new Intent(getApplicationContext(), CarerHome.class);
            startActivity(startIntent);
        });
    }
}
