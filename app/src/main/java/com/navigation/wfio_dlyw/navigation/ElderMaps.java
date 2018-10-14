package com.navigation.wfio_dlyw.navigation;

import android.annotation.SuppressLint;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.location.Location;
import android.os.Handler;
import android.os.IBinder;
import android.os.Messenger;
import android.os.Message;
import android.os.RemoteException;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.Toast;

import com.VoidDDQ.Cam.GeoStatService;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.PolylineOptions;
import com.miguelcatalan.materialsearchview.MaterialSearchView;
import com.navigation.wfio_dlyw.comms.Credentials;
import com.navigation.wfio_dlyw.comms.Requester;
import com.navigation.wfio_dlyw.comms.ServerAction;
import com.navigation.wfio_dlyw.comms.Token;
import com.navigation.wfio_dlyw.twilio.CallService;
import com.navigation.wfio_dlyw.twilio.TwilioUtils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

public class ElderMaps extends AppCompatActivity implements OnMapReadyCallback {
    // Location variables
    private GoogleMap mMap;
    private FusedLocationProviderClient mFusedLocationProviderClient;
    private boolean mLocationPermissionGranted = true;
    private LocationRequest mLocationRequest;
    private LocationCallback mLocationCallback;
    private Location mCurrentLocation;
    private Location mDefaultLocation;
    private PolylineOptions route;
    private String destination;
    private boolean updateRoute;
    private String ROUTE_URL;
    private String API_KEY;

    // Sensor variables
    private SensorEventListener eventListener;
    private Sensor sensor;
    private SensorManager sensorManager;

    // Service variables
    Messenger mService = null;
    boolean mIsBound;

    // Constant variables
    public static final int MSG_REQUEST_LOCATION = 1;
    public static final int MSG_REQUEST_ROUTE = 2;
    public static final int MSG_REQUEST_CHECKPOINT = 3;
    public static final int MSG_UPDATE_DESTINATION = 4;
    public static final int MSG_PAUSE_UPDATE = 5;
    public static final int MSG_RESUME_UPDATE = 6;
    public static final int MSG_SEND_ROUTE = 7;
    public static final int MSG_SEND_CREDENTIALS = 8;
    public static final int MSG_REQUEST_DISTANCE = 9;

    private static final String TAG = ElderMaps.class.getSimpleName();
    private static final int TIME_INTERVAL = 1000;
    private static final int DEFAULT_ZOOM = 15;
    private static final int CHECKPOINT_PROXIMITY = 25;
    private static final String KEY_CAMERA_POSITION = "camera_position";
    private static final String KEY_LOCATION = "location";

    //other variables
    private MaterialSearchView searchView;
    private boolean routeGenerated;
    private CallService.CallServiceReceiver callEventsHandler;

    // Service to client message handler
    class IncomingHandler extends Handler {
        @Override
        public void handleMessage(Message msg) {
            Log.d(TAG, "Handling Service-To-ElderMaps message...");
            switch (msg.what) {
                case MSG_REQUEST_LOCATION:
                    mCurrentLocation = (Location) msg.obj;
                    mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(
                            new LatLng(mCurrentLocation.getLatitude(),
                                    mCurrentLocation.getLongitude()), DEFAULT_ZOOM));
                    break;
                case MSG_REQUEST_ROUTE:
                    // Update map with new route
                    try {
                        route = (PolylineOptions) msg.obj;
                        mMap.clear();
                        mMap.addPolyline(route);
                    } catch (NullPointerException e) {
                        e.printStackTrace();
                    }
                    break;
                case MSG_UPDATE_DESTINATION:
                    routeGenerated =true;
                    // After destination updated, grab new route, callback above
                    try {
                        Message resp = Message.obtain(null, MSG_REQUEST_ROUTE);
                        resp.replyTo = mMessenger;
                        mService.send(resp);
                    } catch (RemoteException e) {
                        e.printStackTrace();
                    }
                    break;
            }
        }
    }

    final Messenger mMessenger = new Messenger(new IncomingHandler());

    // Main service interface
    private ServiceConnection mConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
            Log.d(TAG, "Service connected");
            mService = new Messenger(iBinder);
            try {
                Message msg = Message.obtain(null, MSG_REQUEST_LOCATION);
                msg.replyTo = mMessenger;
                mService.send(msg);

                Message msg2 = Message.obtain(null, MSG_REQUEST_ROUTE);
                msg2.replyTo = mMessenger;
                mService.send(msg2);

                Token token = Token.getInstance(ElderMaps.this);
                String[] credentials = {token.getEmail(), token.getValue()};
                Message msg3 = Message.obtain(null, MSG_SEND_CREDENTIALS, credentials);
                msg3.replyTo = mMessenger;
                mService.send(msg3);
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }

        @Override
        public void onServiceDisconnected(ComponentName componentName) {
            Log.d(TAG, "Service disconnected");
            mService = null;
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.d(TAG, "ElderMaps created");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_elder_maps);
        Token token = Token.getInstance(this);

        // Connects to a carer if available
        String email = getIntent().getStringExtra("from");
        if (email != null) {
            for (int i = 0; i < token.getConnections().length(); i++){
                try {
                    JSONObject carer = token.getConnections().getJSONObject(i);
                    if (carer.getString("email").equals(email)){
                        token.setCurrentConnection(carer);
                        token.createSessionMessages();
                        break;
                    }
                } catch (JSONException e){}
            }
        }

        // Asynchronously setup map
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        // Live location provider
        mFusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);

        // Default location if live location inaccessible
        mDefaultLocation = new Location("Zen Apartments");
        mDefaultLocation.setLatitude(-37.8070);
        mDefaultLocation.setLongitude(144.9612);

        // Should use this getintent, if you want to open elder's location and get elder's details from myelders->onmapclick button - Farhan
        //Intent intent = getIntent();
        //ElderItem elderItem = intent.getParcelableExtra("Example Item");
        //String name = elderItem.getText1();

        //findtoolbar
        Toolbar myToolbar = (Toolbar) findViewById(R.id.toolbarEM);
        setSupportActionBar(myToolbar);

        // Initialize sensor
        sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        sensor = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        if (sensor == null) {
            finish();
        }
        eventListener = new SensorEventListener() {
            @Override
            public void onSensorChanged(SensorEvent event) {
                //Log.d(TAG, "onSensorChanged");
                if(event.values[2] < 2 && event.values[1] > 8){
                    finish();
                }
            }

            @Override
            public void onAccuracyChanged(Sensor sensor, int i) {
                return;
            }
        };

        // Upon reopening activity
        if (savedInstanceState != null) {
            mCurrentLocation = savedInstanceState.getParcelable(KEY_LOCATION);
            //mCameraPosition = savedInstanceState.getParcelable(KEY_CAMERA_POSITION);
        }

        //make fake list
        String[] list = new String[] { "Barney" , "is", "a", "dinosaur", "of", "our", "imagination"};

        //searchview stuff
        MaterialSearchView searchView = (MaterialSearchView) findViewById(R.id.search_view);
        this.searchView = searchView;
        searchView.setSuggestions(list);

        searchView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String query = (String) parent.getItemAtPosition(position);
                searchView.closeSearch();
                //query is the clicked string use that to search for destination
                Log.d("test", query);
            }
        });

        searchView.setOnQueryTextListener(new MaterialSearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                if(query == null) {
                    return false;
                }

                // Sends new destination to service
                try {
                    Message msg = Message.obtain(null, MSG_UPDATE_DESTINATION, query);
                    msg.replyTo = mMessenger;
                    mService.send(msg);
                } catch (RemoteException e) {
                    e.printStackTrace();
                }

                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                //Do some magic
                return false;
            }
        });

        searchView.setOnSearchViewListener(new MaterialSearchView.SearchViewListener() {
            @Override
            public void onSearchViewShown() {
                //Do some magic
            }

            @Override
            public void onSearchViewClosed() {
                //Do some magic
            }
        });

        Button viewMessages = (Button) findViewById(R.id.viewMessages);
        viewMessages.setOnClickListener(view -> {
            if (Token.getInstance(this).getCurrentConnection() != null) {
                Intent smsintent = new Intent(getApplicationContext(), MessageListElder.class);
                startActivity(smsintent);
            }else{
                Toast.makeText(this, "Please connect to a Carer to enable messaging", Toast.LENGTH_LONG).show();
            }
        });

        Button helpMe = (Button) findViewById(R.id.helpMe);
        helpMe.setOnClickListener(view -> {
            if(routeGenerated) {
                try {
                    Message msg = Message.obtain(null, MSG_SEND_ROUTE);
                    msg.replyTo = mMessenger;
                    mService.send(msg);
                } catch (RemoteException e) {
                    e.printStackTrace();
                }
            }else{
                Toast.makeText(this, "Please select a destination to request for help", Toast.LENGTH_LONG).show();
            }
        });
    }

    //inflate toolbar
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        Toast.makeText(this, "hey", Toast.LENGTH_LONG).show();
        getMenuInflater().inflate(R.menu.search, menu);

        MenuItem item = menu.findItem(R.id.action_search);
        searchView.setMenuItem(item);

        callEventsHandler = new CallService.CallServiceReceiver() {
            private MenuItem item =
                    menu.getItem( 0 );

            @Override
            public void onDisconnect() {
                item.setIcon( R.drawable.ic_call );
                item.setEnabled( true );
            }

            @Override
            public void onConnected() {
                Toolbar toolbar = findViewById( R.id.toolbarML );

                item.setIcon( R.drawable.ic_hangup );
                item.setEnabled( true );
            }

            @Override
            public void onCallFailure() {
                Toolbar toolbar = findViewById( R.id.toolbarML );
                item.setIcon( R.drawable.ic_call );
                item.setEnabled( true );
            }
        };

        IntentFilter filter = new IntentFilter();
        filter.addAction(CallService.ON_CONNECT);
        filter.addAction( CallService.ON_DISCONNECT );
        filter.addAction( CallService.ON_FAILURE );
        registerReceiver(callEventsHandler, filter);
        return true;
    }

    @Override
    public void onDestroy() {
        unregisterReceiver(callEventsHandler);
        callEventsHandler = null;
        super.onDestroy();
    }

    //setmenu item
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.back_button:
                Intent startIntent = new Intent(getApplicationContext(), ElderHome.class);
                startActivity(startIntent);
                return true;
            case R.id.star_button:
                if(routeGenerated){
                    //do stuff (add to favorites)
                    Toast.makeText(this, "Favorites added", Toast.LENGTH_LONG).show();
                    return true;
                }else{
                    Toast.makeText(this, "Please select a destination", Toast.LENGTH_LONG).show();
                }
                break;
            case R.id.call_button:
                if(Token.getInstance(this).getCurrentConnection() != null) {
                    if (TwilioUtils.getInstance(this).getCall() == null) {
                        makeCall();
                        item.setEnabled(false);
                    } else {
                        stopCall();
                    }
                    return true;
                }else{
                    Toast.makeText(this, "Please connect to a Carer to enable voice call", Toast.LENGTH_LONG).show();
                }
                break;
            default:
                return super.onOptionsItemSelected(item);
        }
        return super.onOptionsItemSelected(item);
    }

    private void stopCall() {
        Intent stopCallIntent = new Intent(this, CallService.class);
        stopCallIntent.setAction("call.stop");
        startService(stopCallIntent);
    }

    private void makeCall() {
        try {
            Intent callIntent = new Intent(this, CallService.class);
            callIntent.setAction("call.start");
            callIntent.putExtra("to", Token.getInstance(this).getCurrentConnection()
                    .getString("username"));
            startService(callIntent);
        } catch (JSONException e) {
            Toast.makeText( this, "currently not being connected to anyone" ,
                    Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        Log.d(TAG, "Map ready");
        mMap = googleMap;
        updateLocationUI();

        //Log.d(TAG, "Binding service...");
        bindService(new Intent(this, GeoStatService.class), mConnection, Context.BIND_AUTO_CREATE);
        mIsBound = true;
    }

    @SuppressLint("MissingPermission")
    private void updateLocationUI() {
        Log.d(TAG, "Updating location UI");
        mMap.setMyLocationEnabled(true);
        mMap.getUiSettings().setMyLocationButtonEnabled(true);
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
        sensorManager.registerListener(eventListener,sensor,SensorManager.SENSOR_DELAY_FASTEST);
    }

    @Override
    protected void onPause() {
        super.onPause();
        sensorManager.unregisterListener(eventListener);
    }

    @Override
    protected void onStop() {
        super.onStop();
        /*try {
            Message msg = Message.obtain(null, MSG_PAUSE_UPDATE);
            msg.replyTo = mMessenger;
            mService.send(msg);
        } catch (RemoteException e) {
            e.printStackTrace();
        }*/
        if (mIsBound) {
            unbindService(mConnection);
        }
        mIsBound = false;
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(getApplicationContext(), ElderHome.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
    }

}
