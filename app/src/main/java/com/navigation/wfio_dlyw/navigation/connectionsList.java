package com.navigation.wfio_dlyw.navigation;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import java.util.Random;

public class connectionsList extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        int rowNumber = 3;
        int columnNumber = 3;
        setContentView(R.layout.activity_connections_list);
        TableLayout conTable = findViewById(R.id.connectionsTable);
        for(int i=0; i<rowNumber; i++) {
            TableRow row = new TableRow(connectionsList.this);
            for(int j=0; j<columnNumber; j++) {
                int value = new Random().nextInt(100) + 1;
                TextView tv = new TextView(connectionsList.this);
                tv.setText(String.valueOf(value));
                row.addView(tv);
            }
            conTable.addView(row);
        }
    }
}
