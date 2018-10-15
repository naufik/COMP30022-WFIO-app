package com.navigation.wfio_dlyw.navigation;

import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.IntentFilter;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.content.Intent;
import android.location.Location;

import android.speech.RecognizerIntent;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;

import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.Circle;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;
import com.miguelcatalan.materialsearchview.MaterialSearchView;
import com.miguelcatalan.materialsearchview.SearchAdapter;
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

import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

public class CarerMaps extends AppCompatActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    private PolylineOptions route = new PolylineOptions();
    private Marker elderLoc;
    private boolean firstCamera = true;
    private BitmapDescriptor elderIcon;

    private CallService.CallServiceReceiver callEventsListener = null;

    private static final String TAG = CarerMaps.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_carer_maps);

        Toolbar myToolbar = (Toolbar) findViewById(R.id.toolbarCM);
        setSupportActionBar(myToolbar);

        Intent notify = new Intent(this, NotifyService.class);
        notify.setAction("notify");
        notify.putExtra("to", getIntent().getStringExtra("from"));

        startService(notify);

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        //make fake list

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

        elderIcon = BitmapDescriptorFactory.fromResource(R.drawable.elderloc);

    }

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
                this.onBackPressed();
                return true;
            case R.id.sms_button:
                Intent smsintent = new Intent(getApplicationContext(), MessageList.class);
                startActivity(smsintent);
                return true;

            case R.id.call_button:
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

    private void stopCall() {
        Intent stopCallIntent = new Intent(this, CallService.class);
        stopCallIntent.setAction("call.stop");
        startService(stopCallIntent);
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

        Intent intent = getIntent();
        try {
            Log.d(TAG, "Getting Intent extra");
            Bundle b = intent.getExtras();
            JSONArray route = new JSONArray(b.getString("route"));
            Log.d(TAG, "Route converted, grabbing checkpoints...");
            for (int i = 0; i < route.length(); i++) {
                Log.d(TAG, "Checkpoint: "+i);
                JSONObject JSONcheckpoint = route.getJSONObject(i);
                LatLng checkpoint = new LatLng(JSONcheckpoint.getDouble("lat"), JSONcheckpoint.getDouble("long"));
                this.route.add(checkpoint);
            }
            Log.d(TAG, "Rendering to map");
            mMap.addPolyline(this.route);
        } catch (NullPointerException | JSONException e) {
            e.printStackTrace();
        }

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

    private void getLocationFromServer() {
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
                }

                renderLoc(location);

            } catch(JSONException e) {
                Log.e(TAG, e.getMessage());
            }
        }, new Credentials("dropcomputing@gmail.com","kontol"));
    }

    private void renderLoc(Location loc) {
        if(loc != null) {
            Log.d(TAG, loc.toString());
            if(elderLoc != null){
                elderLoc.remove();
            }

            LatLng latLngLoc = new LatLng(loc.getLatitude(), loc.getLongitude());
            elderLoc = mMap.addMarker(new MarkerOptions().icon(elderIcon));
            if(firstCamera){
                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLngLoc, 18));
                firstCamera = false;
            }
        }
    }
    @Override
    public void onBackPressed() {
        Token t = Token.getInstance();

        String text = "Are you sure you want to leave navigation?";
        AlertDialog.Builder builder = DialogBuilder.confirmDialog(text, CarerMaps.this);
        builder.setPositiveButton("YES!",new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int id) {
                Intent serviceIntent = new Intent(CarerMaps.this, MsgUpdateService.class);
                serviceIntent.setAction("stop");
                startService(serviceIntent);
                t.setCurrentConnection(null);
                Intent intent = new Intent(getApplicationContext(), ElderHome.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
            }
        });

        builder.setNegativeButton("NO!",new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int id) {
                return;
            }
        });

        builder.show();

    }
}
