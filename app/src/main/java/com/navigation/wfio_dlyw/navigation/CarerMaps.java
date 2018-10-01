package com.navigation.wfio_dlyw.navigation;

import android.content.Intent;
import android.location.Location;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.util.Log;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.navigation.wfio_dlyw.comms.Credentials;
import com.navigation.wfio_dlyw.comms.NotifyService;
import com.navigation.wfio_dlyw.comms.Requester;
import com.navigation.wfio_dlyw.comms.ServerAction;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.Timer;
import java.util.TimerTask;

public class CarerMaps extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    private LatLng dest;

    private static final String TAG = CarerMaps.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_carer_maps);

        Intent notify = new Intent(this, NotifyService.class);
        notify.setAction("notify");
        notify.putExtra("to", getIntent().getStringExtra("from"));

        startService(notify);

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }


    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        Timer timer = new Timer();
        timer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                CarerMaps.this.runOnUiThread(() -> {
                    getLocationsFromServer();
                });
            }
        }, 0, 1000);
    }

    private void getLocationsFromServer() {
        Requester req = Requester.getInstance(this);
        req.requestAction(ServerAction.MESSAGE_PULL, null, t -> {
            try {
                Location location = null;
                Location destination = null;

                JSONArray locations = t.getJSONObject("result").getJSONArray("messages");

                if(locations.length() != 0){

                    JSONArray JSONlocation = locations
                            .getJSONObject(0)
                            .getJSONObject("location")
                            .getJSONArray("coordinates");

                    location = new Location("location");
                    location.setLatitude(JSONlocation.getDouble(0));
                    location.setLongitude(JSONlocation.getDouble(1));

                    if(locations.length() != 1) {
                        JSONArray JSONdest = locations
                                .getJSONObject(locations.length() - 1)
                                .getJSONObject("location")
                                .getJSONArray("coordinates");

                        destination = new Location("destination");
                        destination.setLatitude(JSONdest.getDouble(0));
                        destination.setLongitude(JSONdest.getDouble(1));
                    }
                }

                renderLocs(location, destination);

            } catch(JSONException e) {
                Log.e(TAG, e.getMessage());
            }
        }, new Credentials("dropcomputing@gmail.com","kontol"));
    }

    private void renderLocs(Location dest, Location loc) {
        if(loc != null) {
            mMap.clear();

            LatLng latLngLoc = new LatLng(loc.getLatitude(), loc.getLongitude());
            mMap.addCircle(new CircleOptions().center(latLngLoc));
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLngLoc, 25));

            if(dest != null) {
                LatLng latLngDest = new LatLng(dest.getLatitude(), dest.getLongitude());
                this.dest = latLngDest;
            }
            mMap.addMarker(new MarkerOptions().position(this.dest));
        }
    }
}
