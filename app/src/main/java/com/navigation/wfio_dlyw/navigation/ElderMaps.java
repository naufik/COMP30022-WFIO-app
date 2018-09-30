package com.navigation.wfio_dlyw.navigation;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.location.Location;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.util.Log;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.android.gms.tasks.Task;
import com.navigation.wfio_dlyw.comms.Credentials;
import com.navigation.wfio_dlyw.comms.Requester;
import com.navigation.wfio_dlyw.comms.ServerAction;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

public class ElderMaps extends FragmentActivity implements OnMapReadyCallback {

    // Location variables
    private GoogleMap mMap;
    private FusedLocationProviderClient mFusedLocationProviderClient;
    private boolean mLocationPermissionGranted = true;
    private LocationRequest mLocationRequest;
    private LocationCallback mLocationCallback;
    private Location mCurrentLocation;
    private Location mDefaultLocation;
    private PolylineOptions mCurrentRoute;
    private String destination;
    private boolean updateRoute;
    private String ROUTE_URL;
    private String API_KEY;

    // Sensor variables
    private SensorEventListener eventListener;
    private Sensor sensor;
    private SensorManager sensorManager;

    // Constant variables
    private static final int PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION = 1;
    private static final String TAG = ElderMaps.class.getSimpleName();
    private static final int TIME_INTERVAL = 1000;
    private static final int DEFAULT_ZOOM = 15;
    private static final int CHECKPOINT_PROXIMITY = 25;
    private static final String KEY_CAMERA_POSITION = "camera_position";
    private static final String KEY_LOCATION = "location";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Asynchronously setup map
        setContentView(R.layout.activity_elder_maps);
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        // Live location provider
        // mFusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);

        // Default location if live location inaccessible
        mDefaultLocation = new Location("Zen Apartments");
        mDefaultLocation.setLatitude(-37.8070);
        mDefaultLocation.setLongitude(144.9612);

        // Should use this getintent, if you want to open elder's location and get elder's details from myelders->onmapclick button - Farhan
        /*Intent intent = getIntent();
        ElderItem elderItem = intent.getParcelableExtra("Example Item");
        String name = elderItem.getText1();*/

        // Initialize strings
        // Intent intent = getIntent();
        // destination = intent.getStringExtra(ElderNavigation.EXTRA_DESTINATION);
        // ROUTE_URL = getResources().getString(R.string.route_url_format);
        // API_KEY = getResources().getString(R.string.google_maps_key);

        // Initialize other variables
        // updateRoute = true;

        // Initialize location polling
        // mLocationRequest = new LocationRequest();
        // mLocationRequest.setInterval(TIME_INTERVAL);
        // mLocationRequest.setFastestInterval(TIME_INTERVAL);
        // mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);

        // Initialize location callback
        /*mLocationCallback = new LocationCallback() {
            @Override
            public void onLocationResult(LocationResult locationResult) {
                if (locationResult == null) {
                    return;
                }
                for (Location location : locationResult.getLocations()) {
                    mCurrentLocation = location;
                    //sendLocToServer(location);
                    Log.d(TAG, String.valueOf(location));
                    if (updateRoute) {
                        Log.d(TAG, "Route requires updating");
                        getRoute(location, destination, response -> {
                            Log.d(TAG, "Route acquired");
                            mCurrentRoute = convertRoute(response);
                            mMap.clear();
                            mMap.addPolyline(mCurrentRoute);
                            updateRoute = false;
                        });
                    }
                    Log.d(TAG, "Checking next route update");
                    checkNextRouteUpdate();
                }
            }
        };*/

        // Initialize sensor
        sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        sensor = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        if (sensor == null) {
            finish();
        }
        eventListener = new SensorEventListener() {
            @Override
            public void onSensorChanged(SensorEvent event) {
                if(event.values[2] < 2 && event.values[1] > 8){
                    finish();
                }
            }
            @Override
            public void onAccuracyChanged(Sensor sensor, int accuracy) {
                // empty
            }
        };

        // Upon reopening activity
        if (savedInstanceState != null) {
            mCurrentLocation = savedInstanceState.getParcelable(KEY_LOCATION);
            //mCameraPosition = savedInstanceState.getParcelable(KEY_CAMERA_POSITION);
        }

    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        //getLocationPermission();
        updateLocationUI();
        //startLocationUpdates();
    }

    /*private void onPermissionGranted() {
        updateLocationUI();
        startLocationUpdates();
    }

    private void getLocationPermission() {
        Log.d(TAG, "getLocationPermission() started");
        if (ContextCompat.checkSelfPermission(this.getApplicationContext(),
                android.Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            mLocationPermissionGranted = true;
            onPermissionGranted();
        } else {
            ActivityCompat.requestPermissions(this,
                    new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION},
                    PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String permissions[],
                                           @NonNull int[] grantResults) {
        Log.d(TAG, "onRequestPermissionResult() started");
        mLocationPermissionGranted = false;
        switch (requestCode) {
            case PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    mLocationPermissionGranted = true;
                    Log.d(TAG, "Permission granted on callback");
                    onPermissionGranted();
                } else {
                    finish();
                }
            }
        }
        //updateLocationUI();
    }*/

    private void updateLocationUI() {
        Log.d(TAG, "updateLocationUI() started");
        if (mMap == null) {
            return;
        }
        try {
            if (mLocationPermissionGranted) {
                mMap.setMyLocationEnabled(true);
                mMap.getUiSettings().setMyLocationButtonEnabled(true);
            } else {
                mMap.setMyLocationEnabled(false);
                mMap.getUiSettings().setMyLocationButtonEnabled(false);
                mCurrentLocation = null;
                //getLocationPermission();
            }
        } catch (SecurityException e)  {
            Log.e("Exception: %s", e.getMessage());
        }
    }

    /*private void getLocation() {
        try {
            if (mLocationPermissionGranted) {
                Task locationResult = mFusedLocationProviderClient.getLastLocation();
                locationResult.addOnCompleteListener(this, task -> {
                    if (task.isSuccessful() && task.getResult() != null) {
                        //Log.d(TAG, "Current location found. Rendering...");
                        mCurrentLocation = (Location) task.getResult();
                        //Log.d(TAG, "Running getRoute()");
                        //getRoute();
                    } else {
                        Log.d(TAG, "Current location is null. Using defaults.");
                        Log.d(TAG, "Task successful: " + String.valueOf(task.isSuccessful()));
                        Log.d(TAG, "Task result: " + String.valueOf(task.getResult()));
                        Log.e(TAG, String.format("Exception: %s", task.getException()));
                        LatLng mDefaultLatLng = new LatLng(mDefaultLocation.getLatitude(),
                                mDefaultLocation.getLongitude());
                        mCurrentLocation = mDefaultLocation;
                        mMap.getUiSettings().setMyLocationButtonEnabled(false);
                    }
                    //mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(
                            //new LatLng(mCurrentLocation.getLatitude(),
                                    //mCurrentLocation.getLongitude()), DEFAULT_ZOOM));
                });
            } else {
                Log.d(TAG, "Location permission not granted.");
            }
        } catch(SecurityException e)  {
            Log.e("Exception: %s", e.getMessage());
        }
    }*/

    /*private void sendLocToServer(Location loc) {
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

    private void checkNextRouteUpdate() {
        // Set to true once current position is within proximity of 2nd polyline point
        try {
            List<LatLng> pointList = mCurrentRoute.getPoints();
            LatLng checkpoint = pointList.get(1);
            Location locationCheckpoint = new Location("checkpoint");
            locationCheckpoint.setLatitude(checkpoint.latitude);
            locationCheckpoint.setLongitude(checkpoint.longitude);
            float distance = mCurrentLocation.distanceTo(locationCheckpoint);
            Log.d(TAG, "Distance to next checkpoint: " + distance + "m");

            if (distance <= CHECKPOINT_PROXIMITY) {
                updateRoute = true;
            }

        } catch (NullPointerException | IndexOutOfBoundsException e) {
            Log.e(TAG, e.getMessage());
        }
    }*/

    @SuppressLint("MissingPermission")
    private void startLocationUpdates() {
        mFusedLocationProviderClient.requestLocationUpdates(mLocationRequest,
                mLocationCallback,
                null /* Looper */);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        if (mMap != null) {
            outState.putParcelable(KEY_CAMERA_POSITION, mMap.getCameraPosition());
            outState.putParcelable(KEY_LOCATION, mCurrentLocation);
            super.onSaveInstanceState(outState);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        // sensorManager.registerListener(eventListener,sensor,SensorManager.SENSOR_DELAY_FASTEST);
    }

    @Override
    protected void onPause() {
        super.onPause();
        // sensorManager.unregisterListener(eventListener);
    }
}
