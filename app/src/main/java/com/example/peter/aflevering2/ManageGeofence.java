package com.example.peter.aflevering2;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;

import com.google.android.gms.maps.model.LatLng;

import org.w3c.dom.Text;

public class ManageGeofence extends AppCompatActivity {

    private TextView latitudeTextView;
    private TextView longitudeTextView;
    private Button createButton;
    public LatLng currentLatLng;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manage_geofence);
        latitudeTextView = findViewById(R.id.latitudeText);
        longitudeTextView = findViewById(R.id.longtitudeText);
        createButton = findViewById(R.id.createButton);
        //latitudeTextView.setText("Latitude: "+currentLatLng.latitude);

        //longitudeTextView.setText("Longtitude: "+currentLatLng.longitude);


    }
/*
    public void setCurrentLatLng(LatLng latLng){
        currentLatLng = latLng;
    }*/
}
