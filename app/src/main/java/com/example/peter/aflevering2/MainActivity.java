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
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.Geofence;
import com.google.android.gms.location.GeofencingRequest;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;

import java.text.DateFormat;
import java.util.Date;

public class MainActivity extends AppCompatActivity implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, LocationListener {

    public static final String TAG = "Aflevering 2";
    private static final double STORCENTER_NORD_LATITUDE = 56.170608;
    private static final double STORCENTER_NORD_LONGITUDE = 10.188461;
    private static final float STORCENTER_NORD_RADIUS = 1500;
    private GoogleApiClient mGoogleApiClient;
    boolean mRequestingLocationUpdates; //has user turned location updates on or off?
    private String mLastUpdateTime;
    private Location mCurrentLocation;
    private LocationRequest mLocationRequest;
    private Geofence mStorcenterNordFence;
    private GeofencingRequest mGeofencingRequest;
    private PendingIntent mPendingIntent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);

        Toast.makeText(getApplicationContext(), "Trying to create geofence", Toast.LENGTH_LONG).show();

        //Setup Google API
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addApi(LocationServices.API)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .build();
        mLocationRequest = new LocationRequest();
        mRequestingLocationUpdates = true; //Starting the app the location updating is on
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
    public void onConnectionSuspended(int cause) {
        mGoogleApiClient.connect();
    }

    @Override
    public void onConnectionFailed(ConnectionResult result) {
    }

    @Override
    public void onConnected(Bundle connectionHint) {
        //Create fence around Storcenter Nord
        mStorcenterNordFence = new Geofence.Builder().setRequestId("storcenter_nord")
                .setExpirationDuration(Geofence.NEVER_EXPIRE)
                .setTransitionTypes(Geofence.GEOFENCE_TRANSITION_ENTER)
                .setCircularRegion(STORCENTER_NORD_LATITUDE, STORCENTER_NORD_LONGITUDE, STORCENTER_NORD_RADIUS)
                .build();
        //Create Geofencing Request
        mGeofencingRequest = new GeofencingRequest.Builder()
                .setInitialTrigger(GeofencingRequest.INITIAL_TRIGGER_ENTER)
                .addGeofence(mStorcenterNordFence)
                .build();

        //Create Intent pointing to intent service
        Intent intent = new Intent(this, ReceiveGeoFenceTransitionService.class);
        mPendingIntent = PendingIntent.getService(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        //some permission stuff created by android Studio
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        LocationServices.GeofencingApi.addGeofences(mGoogleApiClient, mGeofencingRequest, mPendingIntent);
        Toast.makeText(getApplicationContext(), "Geofence added!?", Toast.LENGTH_LONG).show();
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
}
