package com.example.peter.aflevering2;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.model.LatLng;

import org.w3c.dom.Text;

public class ManageGeofence extends AppCompatActivity {

    private TextView latitudeTextView;
    private TextView longitudeTextView;
    private Button createButton;
    private EditText geofenceNameText;
    private EditText geofenceRadius;
    private EditText message;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manage_geofence);
        latitudeTextView = findViewById(R.id.latitudeText);
        longitudeTextView = findViewById(R.id.longitudeText);
        createButton = findViewById(R.id.createButton);
        geofenceNameText = findViewById(R.id.geofenceName);
        geofenceRadius = findViewById(R.id.radius);
        message = findViewById(R.id.setMessageText);

        final SharedPreferences sharedPref = getSharedPreferences("shared", Context.MODE_PRIVATE);
        final String latitude = sharedPref.getString("latitude", "does not exist");
        final String longitude = sharedPref.getString("longitude", "does not exist");

        latitudeTextView.setText("Latitude: " + latitude);
        longitudeTextView.setText("longitude: " + longitude);
        Log.d("manageGeofence", latitude + longitude);

        createButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if(geofenceNameText.getText().equals(null)){
                    Toast.makeText(getApplicationContext(), "Please enter a name for the geofence", Toast.LENGTH_LONG).show();
                    return;
                }

                if(geofenceRadius.equals(null)){
                    Toast.makeText(getApplicationContext(), "Please enter a radius for the geofence", Toast.LENGTH_SHORT).show();
                    return;
                }

                if(message.equals(null)){
                    Toast.makeText(getApplicationContext(), "Please enter an alert message", Toast.LENGTH_LONG).show();
                }

                String geofenceName = geofenceNameText.getText().toString();
                String messageText = message.getText().toString();
                int radius = Integer.parseInt(geofenceRadius.getText().toString());

                Intent intent = new Intent();
                intent.putExtra("latitude", Double.parseDouble(latitude));
                intent.putExtra("longitude", Double.parseDouble(longitude));
                intent.putExtra("name", geofenceName);
                intent.putExtra("radius", radius);
                intent.putExtra("message", messageText);
                setResult(Activity.RESULT_OK, intent);
                finish();
            }
        });


    }

}
