package com.project.cst2335.Activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.appcompat.widget.Toolbar;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.material.snackbar.Snackbar;
import com.project.cst2335.Adapters.CarModelAdapter;
import com.project.cst2335.Fragments.CarModelDetailFragment;
import com.project.cst2335.Models.CarModel;
import com.project.cst2335.R;
import com.project.cst2335.Utils.Constants;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

/**
 *
 * Carbon Detect activity responsible to display
 * list of models for 3 vehicle makers from Database and from API
 *
 * @author Cliff
 * @version 1.0
 */
public class CarbonActivity extends AppCompatActivity {

    //Declaring Sharedpreferences to stpre data
    SharedPreferences sharedPref;
    SharedPreferences.Editor editor;
    String preference = "finalProject";

    //Views
    AutoCompleteTextView vehicleCompany;
    EditText distance;
    AppCompatButton search,open_saved_models;
    RecyclerView modelList;
    ConstraintLayout rootLayout;

    //Adapter for Recycler
    CarModelAdapter adt;

    //Contains list of model class
    //Created from API or from Database
    ArrayList<CarModel> models;

    //Contains Vehicle make name and id
    HashMap<String, String> vehicleCompanyArray;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_carbon_detect);

        Toolbar myToolbar = findViewById(R.id.toolbar_main);

        String activity = getResources().getString(R.string.carbon_dioxide_activity_main);
        String author = getResources().getString(R.string.carbon_dioxide_author_name);
        String version = getResources().getString(R.string.carbon_dioxide_version);

        myToolbar.setTitle(activity);
        myToolbar.setSubtitle(author + " - " + version);
        setSupportActionBar(myToolbar);

        //Initializing all views
        vehicleCompany =  (AutoCompleteTextView)findViewById(R.id.vehicleCompanies);
        distance = (EditText) findViewById(R.id.travelDistance);
        search = (AppCompatButton) findViewById(R.id.searchModels);
        open_saved_models = (AppCompatButton) findViewById(R.id.openSavedModels);
        modelList = (RecyclerView) findViewById(R.id.modelList);
        rootLayout = (ConstraintLayout) findViewById(R.id.rootLayout);

        //Adding 2 vehicle_makes in Array
        vehicleCompanyArray = new HashMap<String,String>();
        vehicleCompanyArray.put("Nissan","bf111d61-70c6-476f-bf45-9bad9e526d4c");
        vehicleCompanyArray.put("Toyoto","2b1d0cd5-59be-4010-83b3-b60c5e5342da");
        vehicleCompanyArray.put("Ford","647f3fec-9a31-48c8-acc2-359f39cc3122");

        //Showing list of Vehicle Companies
        ArrayAdapter<String> adapter = new ArrayAdapter<String>
                (this,android.R.layout.select_dialog_item, vehicleCompanyArray.keySet().toArray(new String[0]));

        vehicleCompany.setThreshold(1);
        //Set Adapter in AutoComplete TextView
        vehicleCompany.setAdapter(adapter);
        vehicleCompany.setTextColor(Color.WHITE);

        //Load Data from sharedPreferences if any
        retrieveData();

        //Search Button Listener to search models through API
        search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                validateInputs();
            }
        });

        //Saved Button Listener to retrive data from database
        open_saved_models.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                validateInputs();
            }
        });


        models = new ArrayList<CarModel>();
        //Set RecyclerView Layout
        adt = new CarModelAdapter(this,models,Integer.parseInt(distance.getText().toString()),"ml","Toyoto");
        //Set Adapter & Layout in Recycler View
        modelList.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
        modelList.setAdapter(adt);

    }


    public void showCarModels() {
        Snackbar.make(rootLayout, "Total "+models.size()+" vehicle makes found", Snackbar.LENGTH_LONG).show();
        adt.notifyDataSetChanged();
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
            Toast.makeText(CarbonActivity.this,"Please enter travel distance",Toast.LENGTH_LONG).show();
            distance.requestFocus();
        } else if (company.equals("")) {
            Toast.makeText(CarbonActivity.this,"Please enter vehicle company",Toast.LENGTH_LONG).show();
            vehicleCompany.requestFocus();
        }
        else {
            saveData(company, travelDistance);
            models.clear();
            adt.notifyDataSetChanged();
            onLoadModels();
        }
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()){
            case R.id.help:
                // showing help alert dialog
                showHelpDialog();
                break;
            case R.id.pexelsProject:
                // starting new activity when project is selected from the roolbar icon
                Intent newIntent = new Intent(CarbonActivity.this, PexelsActivity.class);
                startActivity(newIntent);
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return true;
    }

    public void showHelpDialog() {
        new AlertDialog.Builder(CarbonActivity.this)
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
        editor.apply();
    }

    public void onLoadModels() {
        String vehicleMakeID = vehicleCompanyArray.get(vehicleCompany.getText().toString());

        AlertDialog dialog = new AlertDialog.Builder(CarbonActivity.this)
                .setTitle("Downloading")
                .setMessage("Vehicle models for "+vehicleCompany.getText())
                .setView(new ProgressBar(CarbonActivity.this))
                .show();

        Executor newThread = Executors.newSingleThreadExecutor();
        newThread.execute( () -> {
            try {

                URL url = new URL(Constants.VEHICLE_MODELS_URL+"/"+vehicleMakeID+"/vehicle_models");
                HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setRequestProperty("Authorization", "Bearer "+Constants.API_KEY);
                urlConnection.setRequestMethod("GET");
                int statusCode = urlConnection.getResponseCode();
                if (statusCode == 200) {
                    InputStream it = new BufferedInputStream(urlConnection.getInputStream());
                    InputStreamReader read = new InputStreamReader(it);
                    BufferedReader buff = new BufferedReader(read);
                    StringBuilder dta = new StringBuilder();
                    String chunks = "";
                    while ((chunks = buff.readLine()) != null) {
                        dta.append(chunks);
                    }

                    JSONArray carrray = new JSONArray(dta.toString());
                    for (int i = 0; i < carrray.length(); i++) {
                        System.out.println("Data..." + carrray.getJSONObject(i).toString());
                        JSONObject carObj = carrray.getJSONObject(i).getJSONObject("data");
                        CarModel cmodel = new CarModel(carObj.getString("id"),
                                carObj.getJSONObject("attributes").getString("name"));
                        models.add(cmodel);
                    }

                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            dialog.hide();
                            showCarModels();
                        }
                    });

                }
            }
            catch(Exception e) {
                e.printStackTrace();
            }
        });
    }

}