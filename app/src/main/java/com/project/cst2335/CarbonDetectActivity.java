package com.project.cst2335;

import androidx.appcompat.app.AppCompatActivity;

import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.EditText;

public class CarbonDetectActivity extends AppCompatActivity {

    protected String[] vehicleCompanies = {"Nissan","Toyoto","Ford"};

    protected SharedPreferences sharedPref;
    protected SharedPreferences.Editor editor;
    protected String preference = "finalProject";

    protected AutoCompleteTextView vehicleCompany;
    protected EditText distance;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_carbon_detect);

        vehicleCompany =  (AutoCompleteTextView)findViewById(R.id.vehicleCompanies);
        distance = (EditText) findViewById(R.id.travelDistance);

        //Showing list of Vehicle Companies
        ArrayAdapter<String> adapter = new ArrayAdapter<String>
                (this,android.R.layout.select_dialog_item,vehicleCompanies);

        vehicleCompany.setThreshold(1);
        vehicleCompany.setAdapter(adapter);
        vehicleCompany.setTextColor(Color.RED);

    }
}