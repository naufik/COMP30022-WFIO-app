package com.navigation.wfio_dlyw.navigation;

import android.Manifest;
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

public class ElderMaps extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    private FusedLocationProviderClient mFusedLocationProviderClient;
    private boolean mLocationPermission;
    private LocationRequest mLocationRequest;
    private LocationCallback mLocationCallback;
    private Location mCurrentLocation;
    private String destination;
    private Location mDefaultLocation;

    private SensorEventListener eventListener;
    private Sensor sensor;
    private SensorManager sensorManager;

    private static final int CASE_ACCESS_FINE_LOCATION = 1;
    private static final String TAG = ElderMaps.class.getSimpleName();
    //private final LatLng mDefaultLocation = new LatLng(-37.8070, 144.9612);

    private final String ROUTE_URL = "https://maps.googleapis.com/maps/api/directions/json?origin=%s&destination=%s&key=%s";
    private final String API_KEY = "AIzaSyBbm1wwfULDJFvSC44OoTa_G8XAnGgV6XM";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_elder_maps);
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        mFusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);

        mDefaultLocation = new Location("Zen Apartments");
        mDefaultLocation.setLatitude(-37.8070);
        mDefaultLocation.setLongitude(144.9612);

        Intent intent = getIntent();
        destination = intent.getStringExtra(ElderNavigation.EXTRA_DESTINATION);

        initLocationRequest();
        mLocationCallback = new LocationCallback() {
            @Override
            public void onLocationResult(LocationResult locationResult) {
                if (locationResult == null) {
                    return;
                }
                for (Location location : locationResult.getLocations()) {
                    mCurrentLocation = location;
                    sendLocToServer();
                }
            }
        };

        sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        sensor = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);

        if (sensor == null) {
            finish();
        }

        eventListener = new SensorEventListener() {
            @Override
            public void onSensorChanged(SensorEvent event) {
                if (event.values[2] < 1 && event.values[1] > 8) {
                    Log.d(TAG, "Listening");
                }
            }

            @Override
            public void onAccuracyChanged(Sensor sensor, int accuracy) {
                // empty
            }
        };
    }

    protected void initLocationRequest() {
        mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(1000);
        mLocationRequest.setFastestInterval(1000);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
    }

    private void startLocationUpdates() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        mFusedLocationProviderClient.requestLocationUpdates(mLocationRequest,
                mLocationCallback,
                null /* Looper */);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        // Add a marker in Sydney and move the camera
        // LatLng sydney = new LatLng(-34, 151);
        // mMap.addMarker(new MarkerOptions().position(sydney).title("Marker in Sydney"));
        // mMap.moveCamera(CameraUpdateFactory.newLatLng(sydney));

        getLocationPermission();
        updateLocationUI();
        getLocation();

        startLocationUpdates();
    }

    private void getLocationPermission() {
        if (ContextCompat.checkSelfPermission(this.getApplicationContext(),
                android.Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            Log.d(TAG, "Location permission granted.");
            mLocationPermission = true;
        } else {
            Log.d(TAG, "Requesting location permission...");
            ActivityCompat.requestPermissions(this,
                    new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION},
                    CASE_ACCESS_FINE_LOCATION);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String permissions[],
                                           @NonNull int[] grantResults) {
        mLocationPermission = false;
        switch (requestCode) {
            case CASE_ACCESS_FINE_LOCATION: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Log.d(TAG, "Location permission granted.");
                    mLocationPermission = true;
                }
            }
        }
        updateLocationUI();
    }

    private void updateLocationUI() {
        if (mMap == null) {
            return;
        }
        try {
            if (mLocationPermission) {
                mMap.setMyLocationEnabled(true);
                mMap.getUiSettings().setMyLocationButtonEnabled(true);
            } else {
                mMap.setMyLocationEnabled(false);
                mMap.getUiSettings().setMyLocationButtonEnabled(false);
                Log.d(TAG, "Location permission not granted, setting to null");
                mCurrentLocation = null;
                getLocationPermission();
            }
        } catch (SecurityException e) {
            Log.e("Exception: %s", e.getMessage());
        }
    }

    private void getLocation() {
        try {
            if (mLocationPermission) {
                Task locationResult = mFusedLocationProviderClient.getLastLocation();
                locationResult.addOnCompleteListener(this, task -> {
                    if (task.isSuccessful() && task.getResult() != null) {
                        Log.d(TAG, "Current location found. Rendering...");
                        // Set the map's camera position to the current location of the device.
                        mCurrentLocation = (Location) task.getResult();
                        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(
                                new LatLng(mCurrentLocation.getLatitude(),
                                        mCurrentLocation.getLongitude()), 15));

                        sendLocToServer();

                        Log.d(TAG, "Running getRoute()");
                        getRoute();
                    } else {
                        Log.d(TAG, "Current location is null. Using defaults.");
                        Log.e(TAG, String.format("Exception: %s", task.getException()));
                        LatLng mDefaultLatLng = new LatLng(mDefaultLocation.getLatitude(),
                                mDefaultLocation.getLongitude());
                        mCurrentLocation = mDefaultLocation;
                        // mMap.addMarker(new MarkerOptions().position(mDefaultLatLng));
                        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(mDefaultLatLng, 15));
                        mMap.getUiSettings().setMyLocationButtonEnabled(false);
                    }
                });
            } else {
                Log.d(TAG, "Location permission not granted.");
            }
        } catch(SecurityException e)  {
            Log.e("Exception: %s", e.getMessage());
        }
    }

    private void sendLocToServer() {
        try {
            JSONObject message = new JSONObject();
            JSONObject location = new JSONObject();
            location.put("lat", mCurrentLocation.getLatitude()).put("long", mCurrentLocation.getLongitude());
            message.put("recipient", 1).put("location", location);

            Requester req = Requester.getInstance(this);
            req.requestAction(ServerAction.MESSAGE_SEND, message, t -> {}, new Credentials("dropcomputing@gmail.com","kontol"));
        } catch(JSONException e) {
            Log.e(TAG, e.getMessage());
        }
    }

    private void getRoute() {
        LatLng latLng = new LatLng(mCurrentLocation.getLatitude(), mCurrentLocation.getLongitude());

        String formatDest = destination.replace(" ", "+");
        String formatUrl = String.format(ROUTE_URL, mCurrentLocation.getLatitude() + "," + mCurrentLocation.getLongitude(), formatDest, API_KEY);
        Log.d(TAG, formatUrl);

        // Instantiate the RequestQueue.
        RequestQueue queue = Volley.newRequestQueue(this);

        // Request a string response from the provided URL.
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, formatUrl,
                null, response -> drawRoute(response), error -> Log.e(TAG, "Volley Error"));

        // Add the request to the RequestQueue.
        queue.add(jsonObjectRequest);
    }

    private void drawRoute(JSONObject route) {
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
            }

            Polyline polyline = mMap.addPolyline(polylineOptions);

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        sensorManager.registerListener(eventListener,sensor,SensorManager.SENSOR_DELAY_FASTEST);
    }

    @Override
    protected void onPause() {
        super.onPause();
        sensorManager.unregisterListener(eventListener);
    }
}
