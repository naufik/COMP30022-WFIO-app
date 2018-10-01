package com.navigation.wfio_dlyw.navigation;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;

public class AnswerHelp extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_answer_help);

        TextView text = findViewById(R.id.textView5);
        text.setText(getIntent().getStringExtra("fromName") + " needs help navigating!");

        Button accept = findViewById(R.id.acceptbutton);
        accept.setOnClickListener(view -> {
            Intent startIntent = new Intent(getApplicationContext(), CarerMaps.class);
            startIntent.setAction("can-help");
            startIntent.putExtra("from", getIntent().getStringExtra("from"));
            startActivity(startIntent);
        });

        Button decline = findViewById(R.id.decline);
        decline.setOnClickListener(view -> {
            Intent startIntent = new Intent(getApplicationContext(), CarerHome.class);
            startIntent.setAction("cannot-help");
            startActivity(startIntent);
        });
    }
}
