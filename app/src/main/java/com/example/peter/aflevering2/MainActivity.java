package com.example.peter.aflevering2;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.Geofence;
import com.google.android.gms.location.GeofencingRequest;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;

import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class MainActivity extends AppCompatActivity implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, LocationListener {

    public static final String TAG = "Aflevering 2";
    private static final double STORCENTER_NORD_LATITUDE = 56.17047693;
    private static final double STORCENTER_NORD_LONGITUDE = 10.18840313;
    private static final float STORCENTER_NORD_RADIUS = 1500;
    private static final int LOCATION_REQUEST_CODE = 1;
    private GoogleApiClient mGoogleApiClient;
    boolean mRequestingLocationUpdates; //has user turned location updates on or off?
    private String mLastUpdateTime;
    private Location mCurrentLocation;
    private LocationRequest mLocationRequest;
    private Geofence mStorcenterNordFence;
    private GeofencingRequest mGeofencingRequest;
    private PendingIntent mPendingIntent;
    private List<Geofence> mGeofenceList = new ArrayList<>();

    private Button mBtLaunchActivity;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toast.makeText(getApplicationContext(), "Trying to create geofence", Toast.LENGTH_LONG).show();

        //Setup Google API
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addApi(LocationServices.API)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .build();
        mLocationRequest = new LocationRequest();
        mRequestingLocationUpdates = true; //Starting the app the location updating is on

        mBtLaunchActivity = findViewById(R.id.openMap);
        mBtLaunchActivity.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                startMap();
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        mGoogleApiClient.connect();
    }

    @Override
    protected void onStop() {
        super.onStop();
        mGoogleApiClient.disconnect();
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        Log.d(TAG, "Google Play Services connected!");

        // Let's create a Geofence around the Hovedbanegård
        mStorcenterNordFence = new Geofence.Builder()
                .setRequestId("storcenter_nord")
                .setExpirationDuration(Geofence.NEVER_EXPIRE)
                .setTransitionTypes(Geofence.GEOFENCE_TRANSITION_ENTER)
                .setCircularRegion(STORCENTER_NORD_LATITUDE, STORCENTER_NORD_LONGITUDE, STORCENTER_NORD_RADIUS)
                .build();

        mGeofencingRequest = new GeofencingRequest.Builder()
                .addGeofence(mStorcenterNordFence)
                .setInitialTrigger(GeofencingRequest.INITIAL_TRIGGER_ENTER)
                .build();

        // Create an Intent pointing to the IntentService
        Intent intent = new Intent(this,
                ReceiveGeoFenceTransitionService.class);
        mPendingIntent = PendingIntent.getService(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);

        if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) ==
                PackageManager.PERMISSION_GRANTED &&
                ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) ==
                        PackageManager.PERMISSION_GRANTED) {
            LocationServices.GeofencingApi.addGeofences(mGoogleApiClient, mGeofencingRequest, mPendingIntent);
            Log.d(TAG, "We added the geofence!");
            Toast.makeText(this, "We added the geofence!", Toast.LENGTH_SHORT).show();
            mGeofenceList.add(mStorcenterNordFence);
        } else {
            ActivityCompat.requestPermissions(this, new String[]{
                            Manifest.permission.ACCESS_FINE_LOCATION,
                            Manifest.permission.ACCESS_COARSE_LOCATION},
                    LOCATION_REQUEST_CODE);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case LOCATION_REQUEST_CODE: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    String message = "Location permission accepted. Geofence will be created.";
                    Toast.makeText(getApplicationContext(), message, Toast.LENGTH_LONG).show();

                    // OK, request it now
                    if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                        // TODO: Consider calling
                        //    ActivityCompat#requestPermissions
                        // here to request the missing permissions, and then overriding
                        //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                        //                                          int[] grantResults)
                        // to handle the case where the user grants the permission. See the documentation
                        // for ActivityCompat#requestPermissions for more details.
                        return;
                    }
                    LocationServices.GeofencingApi.addGeofences(mGoogleApiClient, mGeofencingRequest, mPendingIntent);
                    Log.d(TAG, "We added the geofence!");

                } else {

                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                    String message = "Location permission denied. Geofence will not work.";
                    Toast.makeText(getApplicationContext(), message, Toast.LENGTH_LONG).show();
                }
                return;
            }

            // other 'case' lines to check for other
            // permissions this app might request
        }
    }


    @SuppressLint("MissingPermission")
    private void startLocationUpdates() {
        LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, this);
    }

    @Override
    public void onLocationChanged(Location location) {
        mCurrentLocation = location;
        mLastUpdateTime = DateFormat.getTimeInstance().format(new Date());

    }

    private void updateUI(){
    }

    public void startMap()
    {
        Intent intent = new Intent(this, MapsActivity.class);
        startActivity(intent);
    }

    @Override
    public void onConnectionSuspended(int i) {
        Log.d(TAG, "Google Play Services connection suspended!");
        mGoogleApiClient.connect();
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        Log.d(TAG, "Google Play Services connection failed!");
    }

}
