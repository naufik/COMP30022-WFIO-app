package com.navigation.wfio_dlyw.navigation;

import android.annotation.SuppressLint;
import android.app.IntentService;
import android.content.Intent;
import android.location.Location;
import android.support.annotation.Nullable;
import android.util.Log;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.PolylineOptions;
import com.navigation.wfio_dlyw.comms.Credentials;
import com.navigation.wfio_dlyw.comms.Requester;
import com.navigation.wfio_dlyw.comms.ServerAction;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

public class GeoStatService extends IntentService {

    private LocationRequest mLocationRequest;
    private LocationCallback mLocationCallback;
    private FusedLocationProviderClient mFusedLocationProviderClient;

    private Location currentLocation;
    private String destination;
    private PolylineOptions route;
    private int checkpointIndex;
    private boolean firstCycle = true;

    private String ROUTE_URL;
    private String API_KEY;

    private static final int TIME_INTERVAL = 1000;
    private static final int CHECKPOINT_PROXIMITY = 3;
    private static final String TAG = GeoStatService.class.getSimpleName();

    public GeoStatService() {
        super("GeoStatService");
    }

    @Override
    public void onCreate() {
        super.onCreate();
        Log.d(TAG, "onCreate()");
    }

    @Override
    public int onStartCommand(@Nullable Intent intent, int flags, int startId) {
        Log.d(TAG, "onStartCommand()");
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    protected void onHandleIntent(@Nullable Intent intent) {
        // Try and call this when quitting navigation
        Log.d(TAG, "onHandleIntent()");
        // Initialize second route point as checkpoint
        /*checkpointIndex = 1;

        // Initialize strings
        destination = intent.getStringExtra(ElderNavigation.EXTRA_DESTINATION);
        ROUTE_URL = getResources().getString(R.string.route_url_format);
        API_KEY = getResources().getString(R.string.google_maps_key);

        // Live location provider
        mFusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);

        // Initialize location polling
        mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(TIME_INTERVAL);
        mLocationRequest.setFastestInterval(TIME_INTERVAL);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);

        // Initialize location callback
        mLocationCallback = new LocationCallback() {
            @Override
            public void onLocationResult(LocationResult locationResult) {
                if (locationResult == null) {
                    return;
                }
                for (Location location : locationResult.getLocations()) {
                    currentLocation = location;
                    sendLocToServer(location);
                    Log.d(TAG, String.valueOf(currentLocation));*/
                    /*if (updateRoute) {
                        Log.d(TAG, "Route requires updating");
                        mMap.clear();
                        getRoute(location, destination, response -> {
                            Log.d(TAG, "Route acquired");
                            mCurrentRoute = convertRoute(response);
                            mMap.addPolyline(mCurrentRoute);
                            updateRoute = false;
                        });
                    }
                    Log.d(TAG, "Checking next route update");
                    checkNextRouteUpdate();*/
                    /*if (firstCycle) {
                        getRoute(location, destination, response -> {
                            route = convertRoute(response);
                            firstCycle = false;
                        });
                    }
                    if (closeToCheckpoint()) {
                        checkpointIndex++;
                    }
                }
            }
        };*/

        //mFusedLocationProviderClient.requestLocationUpdates(mLocationRequest,
        //mLocationCallback,
        //null /* Looper */);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.d(TAG, "onDestroy()");
    }

    private void sendLocToServer(Location loc) {
        try {
            JSONObject message = new JSONObject();
            JSONObject location = new JSONObject();
            location.put("lat", loc.getLatitude()).put("long", loc.getLongitude());
            message.put("recipient", 1).put("location", location);

            Requester req = Requester.getInstance(this);
            req.requestAction(ServerAction.MESSAGE_SEND, message, t -> {}, new Credentials("dropcomputing@gmail.com","kontol"));
        } catch(JSONException e) {
            Log.e(TAG, e.getMessage());
        }
    }

    private void getRoute(Location location, String destination, final VolleyCallback callback) {
        String formatDest = destination.replace(" ", "+");
        String formatUrl = String.format(ROUTE_URL, location.getLatitude() + "," + location.getLongitude(), formatDest, API_KEY);
        Log.d(TAG, formatUrl);

        // Instantiate the RequestQueue.
        RequestQueue queue = Volley.newRequestQueue(this);

        // Request a string response from the provided URL.
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, formatUrl,
                null, response -> callback.onSuccess(response),
                error -> Log.e(TAG, "Volley Error"));

        // Add the request to the RequestQueue.
        queue.add(jsonObjectRequest);
    }

    private PolylineOptions convertRoute(JSONObject route) {
        PolylineOptions polylineOptions = new PolylineOptions();

        try {
            JSONObject legs = route.getJSONArray("routes")
                    .getJSONObject(0)
                    .getJSONArray("legs")
                    .getJSONObject(0);

            JSONObject start = legs.getJSONObject("start_location");
            LatLng latLngStart = new LatLng(Double.parseDouble(start.getString("lat")),
                    Double.parseDouble(start.getString("lng")));
            polylineOptions.add(latLngStart);

            JSONArray steps = legs.getJSONArray("steps");
            for(int i = 0; i < steps.length(); i++) {
                JSONObject step = steps.getJSONObject(i).getJSONObject("end_location");
                LatLng latLngStep = new LatLng(Double.parseDouble(step.getString("lat")),
                        Double.parseDouble(step.getString("lng")));
                polylineOptions.add(latLngStep);

                if(i == steps.length() - 1) {
                    Location destination = new Location("destination");
                    destination.setLatitude(Double.parseDouble(step.getString("lat")));
                    destination.setLongitude(Double.parseDouble(step.getString("lng")));
                    //sendLocToServer(destination);
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return polylineOptions;
    }

    private boolean closeToCheckpoint() {
        try {
            List<LatLng> pointList = route.getPoints();
            LatLng checkpoint = pointList.get(checkpointIndex);
            Location locationCheckpoint = new Location("checkpoint");
            locationCheckpoint.setLatitude(checkpoint.latitude);
            locationCheckpoint.setLongitude(checkpoint.longitude);
            float distance = currentLocation.distanceTo(locationCheckpoint);
            //Log.d(TAG, "Distance to next checkpoint: " + distance + "m");

            if (distance <= CHECKPOINT_PROXIMITY) {
                return true;
            }

        } catch (NullPointerException | IndexOutOfBoundsException e) {
            Log.e(TAG, e.getMessage());
        }
        return false;
    }

}
