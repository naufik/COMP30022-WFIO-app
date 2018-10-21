package com.navigation.wfio_dlyw.navigation;

import android.content.DialogInterface;
import android.content.Intent;
import android.location.Location;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.widget.Toast;

import com.navigation.wfio_dlyw.comms.Credentials;
import com.navigation.wfio_dlyw.comms.Requester;
import com.navigation.wfio_dlyw.comms.ServerAction;
import com.navigation.wfio_dlyw.comms.Token;
import com.navigation.wfio_dlyw.utility.DialogBuilder;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Activity that displays favorite objects with the help of adapter
 */
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

    /***
     * Insert a new favoriteItem into current view
     * @param position the index to put then item
     * @param item the favoriteItem to be inserted
     */
    public void insertFavourites(int position, FavouriteItem item){
        Log.d("favorite", item.getName());
        favourites.add(position, item);
        mAdapter.notifyItemInserted(position);
    }

    /***
     * remove a favoriteItem from current view
     * @param position the index of the item to be removed
     */
    public void removeItem(int position){
        favourites.remove(position);
        mAdapter.notifyItemRemoved(position);
    }

    /***
     * Create favoriteItems to be passed into the favorite adapter
     */
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

    /***
     * Build a recycler viewer as a place for the adapter to show favoriteItems
     */
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
                Token t = Token.getInstance();
                Requester req = Requester.getInstance(getApplicationContext());
                try {
                    String name = t.getFavorites().getJSONObject(position).getString("name");

                    String text = "Are you sure you want to delete " + name;
                    AlertDialog.Builder builder = DialogBuilder.confirmDialog(text, Favourites.this);
                    builder.setPositiveButton("YES!",new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int id) {
                            t.getFavorites().remove(position);
                            JSONObject params = new JSONObject();
                            try {
                                params.put("favorites", t.getFavorites());
                            } catch (Exception e) {}
                            req.requestAction(ServerAction.USER_MODIFY_RECORD,params,delete->{
                                Toast.makeText(Favourites.this, name + " is no longer your favorite place", Toast.LENGTH_SHORT).show();
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

            @Override
            public void onMapClick(int position) {
                Intent intent = new Intent();
                //gives a favorite item for you to parse get info from
                String destination = favourites.get(position).getName();
                intent.putExtra("FavoriteItem", destination);
                setResult(RESULT_OK, intent);
                finish();
            }
        });
    }
}
