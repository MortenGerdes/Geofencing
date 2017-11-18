package com.example.peter.aflevering2;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

public class setAlerts extends AppCompatActivity {

    private Spinner spinner;
    private Button createAlertButton;
    private Button deleteButton;
    private EditText alertMessageField;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_set_alerts);

        spinner = findViewById(R.id.spinner);
        setDropDownAdapter(spinner);

        alertMessageField = findViewById(R.id.setAlertText);

        createAlertButton = findViewById(R.id.createAlertButton);
        deleteButton = findViewById(R.id.deleteButton);
        createAlertButton.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View view) {

                if (spinner.getSelectedItem() == null){
                    Toast.makeText(getApplicationContext(), "Please select a geolocation", Toast.LENGTH_LONG).show();
                }

                else if(alertMessageField.getText() == null){
                    Toast.makeText(getApplicationContext(), "Please type an alert message", Toast.LENGTH_LONG).show();
                }
                else{
                    Intent intent = new Intent();
                    String chosenGeo = spinner.getSelectedItem().toString();
                    String alertMessage = alertMessageField.getText().toString();
                    intent.putExtra("selected_geofence", chosenGeo);
                    intent.putExtra("alert_message", alertMessage);
                    intent.putExtra("which_button", "edit");
                    setResult(RESULT_OK, intent);
                    finish();
                }

            }
        });

        deleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(spinner.getSelectedItem() == null){
                    Toast.makeText(getApplicationContext(), "Please select a geolocation", Toast.LENGTH_LONG).show();
                }
                else{
                    Intent intent = new Intent();
                    String chosenGeo = spinner.getSelectedItem().toString();
                    intent.putExtra("selected_geofence", chosenGeo);
                    intent.putExtra("which_button", "delete");
                    setResult(RESULT_OK, intent);
                    finish();
                }
            }
        });


    }

    private void setDropDownAdapter(Spinner spinner)
    {
        Bundle bundle = this.getIntent().getExtras();

        ArrayAdapter<String> adp = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, bundle.getStringArrayList("array"));
        adp.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adp);
    }
}
