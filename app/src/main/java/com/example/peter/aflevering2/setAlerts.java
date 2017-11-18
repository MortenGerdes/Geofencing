package com.example.peter.aflevering2;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;

public class setAlerts extends AppCompatActivity {

    private Spinner spinner;
    private Button createAlertButton;
    private EditText alertMessage;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_set_alerts);

        spinner = findViewById(R.id.spinner);
        setDropDownAdapter(spinner);

        createAlertButton = findViewById(R.id.createAlertButton);


    }

    private void setDropDownAdapter(Spinner spinner)
    {
        Bundle bundle = this.getIntent().getExtras();

        ArrayAdapter<String> adp = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, bundle.getStringArrayList("array"));
        adp.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adp);
    }
}
