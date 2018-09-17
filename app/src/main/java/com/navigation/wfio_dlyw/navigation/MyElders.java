package com.navigation.wfio_dlyw.navigation;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import java.util.ArrayList;

public class MyElders extends AppCompatActivity {
    private ArrayList<ElderItem> elders;

    private RecyclerView mRecyclerView;
    private ConnectAdapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;

    private Button buttonInsert;
    private Button buttonRemove;
    private EditText editTextInsert;
    private EditText editTextRemove;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_elders);

        createElders();
        buildRecyclerViewer();
        setButtons();

    }

    public void insertItem(int position){
        elders.add(position, new ElderItem("New Item At Position"+position, "This is Line 2"));
        mAdapter.notifyItemInserted(position);
    }

    public void removeItem(int position){
        elders.remove(position);
        mAdapter.notifyItemRemoved(position);
    }

    public void changeItem(int position, String text){
        elders.get(position).changeText1(text);
        mAdapter.notifyItemChanged(position);
    }

    public void createElders(){
        elders = new ArrayList<>();
        elders.add(new ElderItem("stacy", "stacy"));
        elders.add(new ElderItem("Damn", "stacy"));
        elders.add(new ElderItem("John", "stacy"));
    }

    public void buildRecyclerViewer(){
        mRecyclerView = findViewById(R.id.recyclerView);
        mRecyclerView.setHasFixedSize(true);
        mLayoutManager = new LinearLayoutManager(this);
        mAdapter = new ConnectAdapter(elders);

        mRecyclerView.setLayoutManager(mLayoutManager);
        mRecyclerView.setAdapter(mAdapter);

        mAdapter.setOnItemClickListener(new ConnectAdapter.OnItemClickListener() {
//            @Override
//            public void onItemClick(int position) {
//                changeItem(position, "clicked");
//            }

            @Override
            public void onItemClick(int position){
                Intent intent = new Intent(MyElders.this, MessageList.class);
                intent.putExtra("Example Item", elders.get(position));
                startActivity(intent);
            }

            @Override
            public void onMapClick(int position){
                Intent intent = new Intent(MyElders.this, Maps.class);
                intent.putExtra("Example Item", elders.get(position));
                startActivity(intent);
            }


            @Override
            public void onDeleteClick(int position) {
                removeItem(position);
            }
        });
    }

    public void setButtons(){
        buttonInsert = findViewById(R.id.button_insert);
        buttonRemove = findViewById(R.id.button_remove);
        editTextInsert = findViewById(R.id.edittext_insert);
        editTextRemove = findViewById(R.id.edittext_remove);

        buttonInsert.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int position = Integer.parseInt(editTextInsert.getText().toString());
                insertItem(position);
            }
        });

        buttonRemove.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int position = Integer.parseInt(editTextRemove.getText().toString());
                removeItem(position);
            }
        });

    }



}
