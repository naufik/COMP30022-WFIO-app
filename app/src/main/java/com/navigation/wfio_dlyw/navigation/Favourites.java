package com.navigation.wfio_dlyw.navigation;

import android.content.Intent;
import android.location.Location;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;

import com.VoidDDQ.Cam.UnityPlayerActivity;
import com.navigation.wfio_dlyw.comms.Credentials;
import com.navigation.wfio_dlyw.comms.Requester;
import com.navigation.wfio_dlyw.comms.ServerAction;
import com.navigation.wfio_dlyw.comms.Token;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class Favourites extends AppCompatActivity {
    private static ArrayList<FavouriteItem> favourites;

    private RecyclerView mRecyclerView;
    private static FavouritesAdapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_favourites);
        favourites = new ArrayList<>();

        Toolbar myToolbar = findViewById(R.id.toolbarF);
        setSupportActionBar(myToolbar);

        createFavorites();
        buildRecyclerViewer();
    }

    public void insertFavourites(int position, FavouriteItem item){
        Log.d("favorite", item.getName());
        favourites.add(position, item);
        mAdapter.notifyItemInserted(position);
    }

    public void removeItem(int position){
        favourites.remove(position);
        mAdapter.notifyItemRemoved(position);
    }

    public void createFavorites(){
        Token t = Token.getInstance();
        Requester.getInstance(this).requestAction(ServerAction.USER_GET_INFO, null, res->{
            try{
                JSONArray fList = res.getJSONObject("result").getJSONObject("user").getJSONArray("favorites");
                for(int i=0; i<fList.length(); ++i){
                    JSONObject currentFavourite = fList.getJSONObject(i);
                    Location location = new Location(currentFavourite.getString("name"));
                    JSONObject point = currentFavourite.getJSONObject("location");
                    location.setLatitude(point.getJSONArray("coordinates").getDouble(0));
                    location.setLongitude(point.getJSONArray("coordinates").getDouble(1));

                    insertFavourites(i, new FavouriteItem(location));
                }
            } catch (JSONException e) {

            }
        }, new Credentials(t.getEmail(), t.getValue()));
    }

//    public static void setFavorites(String name, double longitude, double latitude){
//        //inserts the new item at the last position
//        int position = favourites.size();
//        Location hey = new Location(name);
//        hey.setLatitude(longitude);
//        hey.setLongitude(latitude);
//        FavouriteItem item = new FavouriteItem(hey);
//    }

    public void buildRecyclerViewer() {
        mRecyclerView = findViewById(R.id.favouriteView);
        mRecyclerView.setHasFixedSize(true);
        mLayoutManager = new LinearLayoutManager(this);
        mAdapter = new FavouritesAdapter(favourites);

        mRecyclerView.setLayoutManager(mLayoutManager);
        mRecyclerView.setAdapter(mAdapter);

        mAdapter.setOnItemClickListener(new FavouritesAdapter.OnItemClickListener() {
            @Override
            public void onDeleteClick(int position) {
                removeItem(position);
            }

            @Override
            public void onMapClick(int position) {
                Intent intent = new Intent(Favourites.this, UnityPlayerActivity.class);
                //gives a favorite item for you to parse get info from
                String destination = favourites.get(position).getName();
                intent.putExtra("FavouriteItem", destination);
                startActivity(intent);
            }
        });
    }

}
