package com.navigation.wfio_dlyw.navigation;

import android.annotation.SuppressLint;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
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
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
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
import com.VoidDDQ.Cam.UnityPlayerActivity;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.maps.CameraUpdate;
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
import com.navigation.wfio_dlyw.utility.DialogBuilder;
import com.navigation.wfio_dlyw.utility.Text2Speech;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

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

    private static final String TAG = ElderMaps.class.getSimpleName();
    private static final int DEFAULT_ZOOM = 15;
    private static final String KEY_CAMERA_POSITION = "camera_position";
    private static final String KEY_LOCATION = "location";

    //other variables
    private MaterialSearchView searchView;
    private boolean routeGenerated;
    private CallService.CallServiceReceiver callEventsHandler;

    private Text2Speech tts = new Text2Speech(ElderMaps.this);
    private String favorite = "";

    final Messenger mMessenger = new Messenger(new IncomingHandler());

    // Service to client message handler
    class IncomingHandler extends Handler {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case GeoStatService.MSG_REQUEST_LOCATION:
                    // Location retrieved from service, moving camera
                    mCurrentLocation = (Location) msg.obj;
                    mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(
                            new LatLng(mCurrentLocation.getLatitude(),
                                    mCurrentLocation.getLongitude()), DEFAULT_ZOOM));
                    break;

                case GeoStatService.MSG_REQUEST_ROUTE:
                    // Update map with new route, request for direction afterwards
                    try {
                        route = (PolylineOptions) msg.obj;
                        mMap.clear();
                        mMap.addPolyline(route);

                        notifyService(GeoStatService.MSG_REPLY_ZOOM, null);
                        notifyService(GeoStatService.MSG_REQUEST_DIRECTION, null);
                    } catch (NullPointerException e) {
                        e.printStackTrace();
                    }
                    break;

                case GeoStatService.MSG_UPDATE_DESTINATION:
                    // After destination updated, grab new route, callback above
                    routeGenerated =true;
                    notifyService(GeoStatService.MSG_REQUEST_ROUTE, null);
                    break;

                case GeoStatService.MSG_REPLY_ZOOM:
                    // Zoom retrieved from service
                    if (msg.obj != null) {
                        CameraUpdate zoom = (CameraUpdate) msg.obj;
                        mMap.animateCamera(zoom);
                    }
                    break;

                case GeoStatService.MSG_REQUEST_DIRECTION:
                    // Direction retrieved from service
                    Log.d(TAG, "Direction retrieved: " + String.valueOf(msg.obj));
                    String direction = String.valueOf(msg.obj);
                    tts.read(direction);
                    break;

            }
        }
    }
    public void resetData() {
        notifyService(GeoStatService.MSG_UPDATE_DESTINATION,"");
    }
    // Main service interface
    private ServiceConnection mConnection = new ServiceConnection() {

        // Called when reopening Maps from Favorites or AR
        @Override
        public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
            mService = new Messenger(iBinder);

            Token token = Token.getInstance(ElderMaps.this);
            String[] credentials = {token.getEmail(), token.getValue()};
            notifyService(GeoStatService.MSG_SEND_CREDENTIALS, credentials);

            notifyService(GeoStatService.MSG_REQUEST_LOCATION, null);

            // Skip to route request if starting activity from AR or from Favorites without selecting a favorite location
            if (!favorite.equals("")) {
                notifyService(GeoStatService.MSG_UPDATE_DESTINATION, favorite);
            } else {
                notifyService(GeoStatService.MSG_REQUEST_ROUTE, favorite);
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
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_elder_maps);
        Token token = Token.getInstance(this);
        Log.d(TAG, "ElderMaps created");

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
                notifyService(GeoStatService.MSG_UPDATE_DESTINATION, query);
                favorite = "";
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
    }

    //inflate toolbar
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
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
                this.onBackPressed();
                return true;
            case R.id.star_button:
                if(routeGenerated){
                    notifyService(GeoStatService.MSG_SAVE_FAVORITES, null);
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
            case R.id.myFavourites:
                Intent favouriteIntent = new Intent(getApplicationContext(), Favourites.class);
                startActivityForResult(favouriteIntent, 1);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
        return super.onOptionsItemSelected(item);
    }

    // Method called if returning to Maps from Favorites
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Log.d(TAG, "onActivityResult");
        favorite = "";
        if (requestCode == 1) {
            if (resultCode == RESULT_OK) {
                favorite = data.getStringExtra("FavoriteItem");
            }
        }

        Log.d(TAG, favorite);
    }

    // Method called when view messages clicked
    public void viewMessages (View view) {
        if (Token.getInstance(this).getCurrentConnection() != null) {
            Intent smsintent = new Intent(getApplicationContext(), MessageListElder.class);
            startActivity(smsintent);
        }else{
            Toast.makeText(this, "Please connect to a Carer to enable messaging", Toast.LENGTH_LONG).show();
        }
    }

    // Method called when SOS clicked
    public void helpMe (View view) {
        if(routeGenerated) {
            notifyService(GeoStatService.MSG_SEND_ROUTE, null);

            Timer timer = new Timer();
            timer.scheduleAtFixedRate(new TimerTask() {
                @Override
                public void run() {
                    ElderMaps.this.runOnUiThread(() -> {
                        int id = connectionEstablished();
                        if(id != 0){
                            notifyService(GeoStatService.MSG_SEND_CONNECTION_ID, id);
                            timer.cancel();
                        }
                    });
                }
            }, 0, 100);

        }else{
            Toast.makeText(this, "Please select a destination to request for help", Toast.LENGTH_LONG).show();
        }
    }

    private int connectionEstablished(){
        JSONObject connection= Token.getInstance(this).getCurrentConnection();
        if(connection != null){
            try {
                return connection.getInt("id");
            } catch (JSONException e) {
                e.printStackTrace();
                return 0;
            }
        }
        return 0;
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

        bindService(new Intent(this, GeoStatService.class), mConnection, Context.BIND_AUTO_CREATE);
        mIsBound = true;

        Log.d(TAG, "Maps resumed");
    }

    @Override
    protected void onPause() {
        Log.d(TAG, "Maps paused");
        super.onPause();
        sensorManager.unregisterListener(eventListener);
    }

    @Override
    protected void onStop() {
        Log.d(TAG, "Maps stopped");
        super.onStop();

        if (mIsBound) {
            unbindService(mConnection);
        }
        mIsBound = false;
    }

    @Override
    public void onBackPressed() {
        Token t = Token.getInstance(getApplicationContext());

        String text = "Are you sure you want to leave navigation?";
        AlertDialog.Builder builder = DialogBuilder.confirmDialog(text, ElderMaps.this);
        builder.setPositiveButton("YES!", (dialog, id) -> {

            notifyService(GeoStatService.MSG_UPDATE_DESTINATION,"");

            Intent serviceIntent = new Intent(ElderMaps.this, MsgUpdateService.class);
            serviceIntent.setAction("stop");
            startService(serviceIntent);
            t.setCurrentConnection(null);
            Intent intent = new Intent(getApplicationContext(), ElderHome.class);
            Log.d("EM", t.getValue());
            finish();
            Log.d("EM2", t.getValue());
            startActivity(intent);
        });

        builder.setNegativeButton("NO!", (dialog, id) -> {
            return;
        });

        builder.show();

    }

    private void notifyService(int msgCode, Object toSend) {
        try {
            Message msg = Message.obtain(null, msgCode, toSend);
            msg.replyTo = mMessenger;
            mService.send(msg);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

}
