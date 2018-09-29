package com.navigation.wfio_dlyw.navigation;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.navigation.wfio_dlyw.comms.Credentials;
import com.navigation.wfio_dlyw.comms.Requester;
import com.navigation.wfio_dlyw.comms.ServerAction;
import com.navigation.wfio_dlyw.comms.Token;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

public class MyElders extends AppCompatActivity {
    private ArrayList<ElderItem> elders;

    private RecyclerView mRecyclerView;
    private ConnectAdapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        elders = new ArrayList<>();
        setContentView(R.layout.activity_my_elders);

        Toolbar myToolbar = (Toolbar) findViewById(R.id.toolbarME);
        setSupportActionBar(myToolbar);

        createElders();
        buildRecyclerViewer();
    }

    public void insertItem(int position, ElderItem item){
        elders.add(position, item);
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
        Token t = Token.getInstance();
        Requester.getInstance(this).requestAction(ServerAction.USER_GET_INFO, null, res -> {
            try {
                JSONArray eList = res.getJSONObject("result").getJSONObject("user").getJSONArray("eldersList");
                for (int i = 0; i < eList.length(); ++i) {
                    JSONObject currentElder = eList.getJSONObject(i);
                    insertItem(i, new ElderItem("" + currentElder.getString("fullname"), "" + currentElder.getString("username"), currentElder.getInt("id")));
                }
            } catch (Exception e) {

            }
        }, new Credentials(t.getEmail(), t.getValue()));
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

            //CarerHome should be changed to ElderMaps instead
            @Override
            public void onMapClick(int position){
                Intent intent = new Intent(MyElders.this, CarerHome.class);
                intent.putExtra("Example Item", elders.get(position));
                startActivity(intent);
            }


            @Override
            public void onDeleteClick(int position) {
                removeItem(position);
            }
        });
    }
}
