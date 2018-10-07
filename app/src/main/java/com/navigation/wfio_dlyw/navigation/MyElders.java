package com.navigation.wfio_dlyw.navigation;

import android.content.Intent;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.widget.Toast;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import com.navigation.wfio_dlyw.comms.Credentials;
import com.navigation.wfio_dlyw.comms.Requester;
import com.navigation.wfio_dlyw.comms.ServerAction;
import com.navigation.wfio_dlyw.comms.Token;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class MyElders extends AppCompatActivity {
    private ArrayList<ElderItem> elders;

    private RecyclerView mRecyclerView;
    private ConnectAdapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;
    private AlertDialog.Builder builder = null;
    private Requester req;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        req = Requester.getInstance(this);
        elders = new ArrayList<>();
        setContentView(R.layout.activity_my_elders);
        builder = new AlertDialog.Builder(this);

        Toolbar myToolbar = findViewById(R.id.toolbarME);
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
                    Log.d("online Elder", ""+currentElder.getInt("id"));
                    Log.d("offline Elder", ""+t.getConnections().getJSONObject(i).getInt("id"));
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

            @Override
            public void onItemClick(int position){
                Intent intent = new Intent(MyElders.this, MessageList.class);
                intent.putExtra("Example Item", elders.get(position));
                try {
                    Token.getInstance().setCurrentConnection(Token.getInstance().getConnections()
                            .getJSONObject(position));
                    startActivity(intent);
                } catch (JSONException e) {

                }
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
                Token t = Token.getInstance();
                Log.d("itemPrint", ""+elders.get(position).getmId());
                try {
                    Log.d("itemCompare", "" + t.getConnections().getJSONObject(position).getInt("id")); 
                    t.getConnections().remove(position);
                    JSONObject params = new JSONObject();
                    params.put("connections",t.getConnections());
                    req.requestAction(ServerAction.USER_MODIFY_RECORD,params,delete->{
                        Toast.makeText(MyElders.this, "updated connections list", Toast.LENGTH_SHORT).show();
                    },new Credentials(t.getEmail(),t.getValue()));
                } catch (JSONException e) {}

                removeItem(position);
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
                Intent startIntent = new Intent(getApplicationContext(), CarerHome.class);
                startActivity(startIntent);
                return true;

            default:
                // If we got here, the user's action was not recognized.
                // Invoke the superclass to handle it.
                return super.onOptionsItemSelected(item);
        }
    }
}


