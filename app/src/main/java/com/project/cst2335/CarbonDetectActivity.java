package com.project.cst2335;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;

import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.EditText;
import android.widget.Toast;

public class CarbonDetectActivity extends AppCompatActivity {

    protected String[] vehicleCompanies = {"Nissan","Toyoto","Ford"};

    protected SharedPreferences sharedPref;
    protected SharedPreferences.Editor editor;
    protected String preference = "finalProject";

    protected AutoCompleteTextView vehicleCompany;
    protected  AppCompatButton search;
    protected EditText distance;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_carbon_detect);

        getSupportActionBar().setTitle("Carbon Dioxide Interface");
        getSupportActionBar().setSubtitle("Cliff");

        search = (AppCompatButton) findViewById(R.id.searchModels);
        vehicleCompany =  (AutoCompleteTextView)findViewById(R.id.vehicleCompanies);
        distance = (EditText) findViewById(R.id.travelDistance);

        //Showing list of Vehicle Companies
        ArrayAdapter<String> adapter = new ArrayAdapter<String>
                (this,android.R.layout.select_dialog_item,vehicleCompanies);

        vehicleCompany.setThreshold(1);
        vehicleCompany.setAdapter(adapter);
        vehicleCompany.setTextColor(Color.RED);
        retrieveData();

        search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                validateInputs();
            }
        });
    }
    public void retrieveData() {
        SharedPreferences prefs = getSharedPreferences (preference,MODE_PRIVATE);
        String companyValue = prefs.getString("company", "-");
        String travelDistanceValue = prefs.getString("travelDistance", "-");
        if (!travelDistanceValue.equals("-")) distance.setText(travelDistanceValue);
        if (!companyValue.equals("-")) vehicleCompany.setText(companyValue);
    }

    public void validateInputs() {
        String company = vehicleCompany.getText().toString();
        String travelDistance = distance.getText().toString();
        if (travelDistance.equals("")) {
            Toast.makeText(CarbonDetectActivity.this,"Please enter travel distance", Toast.LENGTH_LONG).show();
            distance.requestFocus();
        } else if (company.equals("")) {
            Toast.makeText(CarbonDetectActivity.this,"Please enter vehicle company",Toast.LENGTH_LONG).show();
            vehicleCompany.requestFocus();
        }
        else {
            saveData(company, travelDistance);
            onLoadModels();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    public void showHelpDialog() {
        new AlertDialog.Builder(CarbonDetectActivity.this)
                .setTitle("Carbon Dioxide Interface")
                .setMessage("This interface will calculates how much CO2 (carbon dioxide) is generated from driving a car")
                .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        return;
                    }
                }).show();
    }
    public void saveData(String company,String travelDistance) {
        editor = getSharedPreferences(preference, MODE_PRIVATE).edit();
        editor.putString("company", company);
        editor.putString("travelDistance", travelDistance);
        Log.e("SharedPref","Company: "+company);
        Log.e("SharedPref","Travel Distance: "+travelDistance);
        editor.apply();
    }

    public void onLoadModels() {

    }
}