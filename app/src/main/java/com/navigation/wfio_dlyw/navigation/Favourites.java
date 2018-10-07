package com.navigation.wfio_dlyw.navigation;

import android.content.Intent;
import android.location.Location;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;

import java.util.ArrayList;

public class Favourites extends AppCompatActivity {
    private static ArrayList<FavouriteItem> favourites = new ArrayList<>();

    private RecyclerView mRecyclerView;
    private static FavouritesAdapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_favourites);

        Toolbar myToolbar = findViewById(R.id.toolbarF);
        setSupportActionBar(myToolbar);

        buildRecyclerViewer();
    }

    public void removeItem(int position){
        favourites.remove(position);
        mAdapter.notifyItemRemoved(position);
    }

    public static void setFavorites(String name, double longitude, double latitude){
        //inserts the new item at the last position
        int position = favourites.size();
        Location hey = new Location(name);
        hey.setLatitude(longitude);
        hey.setLongitude(latitude);
        FavouriteItem item = new FavouriteItem(hey);
        favourites.add(position, item);
        mAdapter.notifyItemInserted(position);
    }

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
                Intent intent = new Intent(Favourites.this, CarerHome.class);
                //gives a favorite item for you to parse get info from
                intent.putExtra("Example Item", favourites.get(position));
                startActivity(intent);
            }
        });
    }

}
