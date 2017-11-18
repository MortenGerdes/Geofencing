package com.example.peter.aflevering2;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback, LocationListener {
    private static int GEOFENCE_MAPSACTIVITY_REQUEST_CODE = 102;
    private GoogleMap mMap;
    private Location finalLocation = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
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
        boolean isGPSAvailable = false, isNetworkAvailable = false;
        Location networkLocation = null;
        Location GPSLocation = null;
        LocationManager lm = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

        isGPSAvailable = lm.isProviderEnabled(LocationManager.GPS_PROVIDER);
        isNetworkAvailable = lm.isProviderEnabled(LocationManager.NETWORK_PROVIDER);


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
        if (isGPSAvailable) {
            GPSLocation = lm.getLastKnownLocation(LocationManager.GPS_PROVIDER);
        }
        if (isNetworkAvailable) {
            networkLocation = lm.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
        }

        if (GPSLocation != null && networkLocation != null) {

            //smaller the number more accurate result will
            if (GPSLocation.getAccuracy() > networkLocation.getAccuracy()) {
                finalLocation = networkLocation;
            }
            else {
                finalLocation = GPSLocation;
            }
        }
        else {
            if (GPSLocation != null) {
                finalLocation = GPSLocation;
            }
            else if (networkLocation != null) {
                finalLocation = networkLocation;
            }
            else
            {
                lm.requestLocationUpdates(LocationManager.GPS_PROVIDER, 5000, 10f, this);
            }
        }

        double longitude = finalLocation.getLongitude();
        double latitude = finalLocation.getLatitude();

        mMap = googleMap;


        // Add a marker in Sydney and move the camera
        LatLng currentLocation = new LatLng(latitude,longitude );
        mMap.addMarker(new MarkerOptions().position(currentLocation).title("Current location"));
        mMap.moveCamera(CameraUpdateFactory.newLatLng(currentLocation));

        mMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
            @Override
            public void onMapClick(LatLng latLng) {
                Marker chosenLocation = mMap.addMarker(new MarkerOptions()
                .position(latLng)
                .title("Chosen location"));
                chosenLocation.showInfoWindow();

                if(chosenLocation.isInfoWindowShown()){
                    //ManageGeofence geoActivity = new ManageGeofence();
                   // geoActivity.setCurrentLatLng(latLng);

                    SharedPreferences sharedPref = getSharedPreferences("shared", Context.MODE_PRIVATE);
                    SharedPreferences.Editor editor = sharedPref.edit();
                    editor.putString("latitude", ""+ latLng.latitude);
                    editor.putString("longitude", ""+latLng.longitude);
                    editor.commit();
                    startManageGeofence();
                }
            }
        });
        
    }

    public void startManageGeofence(){
        Intent intent = new Intent(this, ManageGeofence.class);
        startActivityForResult(intent,GEOFENCE_MAPSACTIVITY_REQUEST_CODE );
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Log.d("Geofence", "WENT HERE 2");

        if (requestCode == GEOFENCE_MAPSACTIVITY_REQUEST_CODE){

            if(resultCode == RESULT_OK){
                setResult(Activity.RESULT_OK, data);
                finish();
            }
            else{
                Log.d("GeofenceResult", "error in creating geofence (MapsActivity)");
            }
        }
    }

    @Override
    public void onLocationChanged(Location location) {
        finalLocation = location;

    }

    @Override
    public void onStatusChanged(String s, int i, Bundle bundle) {

    }

    @Override
    public void onProviderEnabled(String s) {

    }

    @Override
    public void onProviderDisabled(String s) {

    }
}
