package com.navigation.wfio_dlyw.navigation;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
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
import com.navigation.wfio_dlyw.utility.DialogBuilder;

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
    private Requester req = Requester.getInstance(this);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        elders = new ArrayList<>();
        setContentView(R.layout.activity_my_elders);
        Bundle extras = getIntent().getExtras();

        Toolbar myToolbar = findViewById(R.id.toolbarME);
        setSupportActionBar(myToolbar);

        createElders();
        buildRecyclerViewer();

        if(extras!=null) {
            Log.d("ME","is this working");
            String text = "You are now connected with " + extras.getString("name");
            DialogBuilder.okDialog(text,this).show();
        }
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
                try {
                    String name = t.getConnections().getJSONObject(position).getString("fullname");

                    String text = "Are you sure you want to disconnect from " + name;
                    AlertDialog.Builder builder = DialogBuilder.confirmDialog(text, MyElders.this);
                    builder.setPositiveButton("YES!",new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int id) {
                            t.getConnections().remove(position);
                            JSONObject params = new JSONObject();
                            try {
                                params.put("connections", t.getConnections());
                            } catch (Exception e) {}
                            req.requestAction(ServerAction.USER_MODIFY_RECORD,params,delete->{
                                Toast.makeText(MyElders.this, name + " is no longer connected with you", Toast.LENGTH_SHORT).show();
                                removeItem(position);
                            },new Credentials(t.getEmail(),t.getValue()));
                        }
                    });

                    builder.setNegativeButton("NO!",new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int id) {
                            return;
                        }
                    });
                    builder.show();
                } catch (JSONException e) {}
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


