package com.navigation.wfio_dlyw.navigation;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.navigation.wfio_dlyw.comms.Token;

import org.json.JSONException;
import org.json.JSONObject;

public class AnswerHelp extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_answer_help);
        Token token = Token.getInstance();

        TextView text = findViewById(R.id.textView5);
        text.setText(getIntent().getStringExtra("fromName") + " needs help navigating!");
        for (int i =0; i<token.getConnections().length(); i++){
            try{
                JSONObject elder = token.getConnections().getJSONObject(i);
                if(elder.getString("email").equals(getIntent().getStringExtra("from"))){
                    token.setCurrentConnection(elder);
                    token.createSessionMessages();
                    Toast.makeText(this, elder.getString("fullname"), Toast.LENGTH_SHORT).show();
                    break;
                }
            } catch (JSONException e){}
        }

        Button accept = findViewById(R.id.acceptbutton);
        accept.setOnClickListener(view -> {
            Intent startIntent = new Intent(getApplicationContext(), CarerMaps.class);
            startIntent.setAction("can-help");
            startIntent.putExtra("from", getIntent().getStringExtra("from"));
            this.finish();
            startActivity(startIntent);
        });

        Button decline = findViewById(R.id.decline);
        decline.setOnClickListener(view -> {
            Intent startIntent = new Intent(getApplicationContext(), CarerHome.class);
            startIntent.setAction("cannot-help");
            this.finish();
            startActivity(startIntent);
        });
    }
}
