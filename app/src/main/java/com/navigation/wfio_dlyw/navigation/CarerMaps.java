package com.navigation.wfio_dlyw.navigation;

import android.content.IntentFilter;
import android.content.Intent;
import android.location.Location;

import android.os.Bundle;

import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;
import com.navigation.wfio_dlyw.comms.Credentials;
import com.navigation.wfio_dlyw.comms.NotifyService;
import com.navigation.wfio_dlyw.comms.Requester;
import com.navigation.wfio_dlyw.comms.ServerAction;
import com.navigation.wfio_dlyw.comms.Token;
import com.navigation.wfio_dlyw.utility.DialogBuilder;
import com.navigation.wfio_dlyw.twilio.CallService;
import com.navigation.wfio_dlyw.twilio.TwilioUtils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Timer;
import java.util.TimerTask;

/**
 * CarerMaps is started upon the carer accepting the help request from the elder. Upon accepting,
 * CarerMaps renders the route grabbed from the notification and the elder's location from the
 * server. CarerMaps also has features to call the elder and message them.
 *
 * @author Samuel Tumewa
 */
public class CarerMaps extends AppCompatActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    private PolylineOptions route = new PolylineOptions();
    private Marker elderLoc;
    private boolean firstCamera = true;
    private BitmapDescriptor elderIcon;

    private static final int ZOOM_PADDING = 200;

    private CallService.CallServiceReceiver callEventsListener = null;

    private static final String TAG = CarerMaps.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_carer_maps);

        // Initialize toolbar
        Toolbar myToolbar = (Toolbar) findViewById(R.id.toolbarCM);
        setSupportActionBar(myToolbar);

        // Initialize notification service
        Intent notify = new Intent(this, NotifyService.class);
        notify.setAction("notify");
        notify.putExtra("to", getIntent().getStringExtra("from"));
        startService(notify);

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        // Initialize token connection
        Token token = Token.getInstance(this);
        String email = getIntent().getStringExtra("from");
        if (email != null) {
            for (int i = 0; i < token.getConnections().length(); i++){
                try {
                    JSONObject elder = token.getConnections().getJSONObject(i);
                    if (elder.getString("email").equals(email)){
                        token.setCurrentConnection(elder);
                        token.createSessionMessages();
                        break;
                    }
                } catch (JSONException e){}
            }
        }

        // Initialize elder logo
        elderIcon = BitmapDescriptorFactory.fromResource(R.drawable.elderloc);
    }

    // Initialize listener for calls from elder
    private void initializeCallEventsListener(Menu menu) {
        callEventsListener = new CallService.CallServiceReceiver() {
            private MenuItem item = menu.getItem(0);

            @Override
            public void onDisconnect() {
                item.setIcon(R.drawable.ic_call);
            }

            @Override
            public void onConnected() {
                item.setIcon(R.drawable.ic_hangup);
            }

            @Override
            public void onCallFailure() {
                item.setIcon(R.drawable.ic_call);
            }
        };

        IntentFilter filter = new IntentFilter();
        filter.addAction(CallService.ON_CONNECT);
        filter.addAction(CallService.ON_DISCONNECT);
        filter.addAction(CallService.ON_FAILURE);

        registerReceiver(callEventsListener, filter);
    }

    // Calls the connected elder
    private void makeCall() {
        try {
            Intent callIntent = new Intent(this, CallService.class);
            callIntent.setAction("call.start");
            callIntent.putExtra("to", Token.getInstance(this).getCurrentConnection()
                    .getString("username"));
            startService(callIntent);
        } catch (Exception e) {
            Toast.makeText( this, "currently not being connected to anyone" ,
                    Toast.LENGTH_LONG).show();
        }
    }

    // Initialize options menu
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.carermaps,menu);

        initializeCallEventsListener(menu);
        return true;
    }

    @Override
    public void onDestroy() {
        stopCall();
        unregisterReceiver(callEventsListener);
        callEventsListener = null;
        super.onDestroy();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.back_button:
                // Back button pressed
                this.onBackPressed();
                return true;

            case R.id.sms_button:
                // Opens the message activity
                Intent smsintent = new Intent(getApplicationContext(), MessageList.class);
                startActivity(smsintent);
                return true;

            case R.id.call_button:
                // Calls the elder through Twilio
                if (TwilioUtils.getInstance(this).getCall() == null) {
                    makeCall();
                } else {
                    stopCall();
                }
                return true;

            default:
                // If we got here, the user's action was not recognized.
                // Invoke the superclass to handle it.
                return super.onOptionsItemSelected(item);
        }
    }

    // End call with elder
    private void stopCall() {
        Intent stopCallIntent = new Intent(this, CallService.class);
        stopCallIntent.setAction("call.stop");
        startService(stopCallIntent);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        Intent intent = getIntent();
        try {
            // Grabs route from notification intent and renders
            Bundle b = intent.getExtras();
            JSONArray JSONroute = new JSONArray(b.getString("route"));
            for (int i = 0; i < JSONroute.length(); i++) {
                JSONObject JSONcheckpoint = JSONroute.getJSONObject(i);
                LatLng checkpoint = new LatLng(JSONcheckpoint.getDouble("lat"), JSONcheckpoint.getDouble("long"));
                route.add(checkpoint);
            }
            mMap.addPolyline(route);

            // Zooms to route once map fully loaded
            mMap.setOnMapLoadedCallback(() -> {
                LatLngBounds.Builder builder = new LatLngBounds.Builder();
                for(LatLng checkpoint : route.getPoints()){
                    builder.include(checkpoint);
                }
                LatLngBounds zoomBounds = builder.build();
                CameraUpdate zoom = CameraUpdateFactory.newLatLngBounds(zoomBounds, ZOOM_PADDING);
                mMap.animateCamera(zoom);
            });

        } catch (NullPointerException | JSONException e) {
            e.printStackTrace();
        }

        // Grab elder location every second
        Timer timer = new Timer();
        timer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                CarerMaps.this.runOnUiThread(() -> {
                    getLocationFromServer();
                });
            }
        }, 0, 1000);
    }

    // Grab location of elder from server
    private void getLocationFromServer() {
        Requester req = Requester.getInstance(this);
        req.requestAction(ServerAction.MESSAGE_PULL, null, t -> {
            try {
                Location location = null;
                Location destination = null;

                // JSONArray manipulation obtained from server
                JSONArray locations = t.getJSONObject("result").getJSONArray("messages");

                if(locations.length() != 0){

                    JSONArray JSONlocation = locations
                            .getJSONObject(0)
                            .getJSONObject("location")
                            .getJSONArray("coordinates");

                    location = new Location("location");
                    location.setLatitude(JSONlocation.getDouble(0));
                    location.setLongitude(JSONlocation.getDouble(1));
                }

                renderLoc(location);

            } catch(JSONException e) {
                Log.e(TAG, e.getMessage());
            }
        }, new Credentials(Token.getInstance(CarerMaps.this).getEmail(),
                Token.getInstance(CarerMaps.this).getValue()));
    }

    // Renders elder's location on map
    private void renderLoc(Location loc) {
        if(loc != null) {
            Log.d(TAG, loc.toString());
            if(elderLoc != null){
                elderLoc.remove();
            }

            LatLng latLngLoc = new LatLng(loc.getLatitude(), loc.getLongitude());
            elderLoc = mMap.addMarker(new MarkerOptions().position(latLngLoc).icon(elderIcon).anchor(0.5f, 0.5f));
        }
    }
    @Override
    public void onBackPressed() {
        Token t = Token.getInstance();

        // Confirm with user whether quitting navigation, if yes, stop message service updates also
        String text = "Are you sure you want to leave navigation?";
        AlertDialog.Builder builder = DialogBuilder.confirmDialog(text, CarerMaps.this);
        builder.setPositiveButton("YES!", (dialog, id) -> {
            Intent serviceIntent = new Intent(CarerMaps.this, MsgUpdateService.class);
            serviceIntent.setAction("stop");
            startService(serviceIntent);
            t.setCurrentConnection(null);
            Intent intent = new Intent(getApplicationContext(), CarerHome.class);
//                intent.setFlags(Intent. );
            startActivity(intent);
        });

        builder.setNegativeButton("NO!", (dialog, id) -> {
            return;
        });

        builder.show();

    }
}
