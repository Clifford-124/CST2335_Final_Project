package com.project.cst2335;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.snackbar.Snackbar;

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

public class CarbonDetectActivity extends AppCompatActivity {

    SharedPreferences sharedPref;
    SharedPreferences.Editor editor;
    String preference = "finalProject";

    AutoCompleteTextView vehicleCompany;
    EditText distance;
    AppCompatButton search;
    RecyclerView modelList;
    ConstraintLayout rootLayout;

    CarModelAdapter adt;
    ArrayList<CarModel> models;
    String vehicleMakeURL;
    String APIKEY;
    HashMap<String, String> vehicleCompanyArray;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_carbon_detect);

        getSupportActionBar().setTitle("Carbon Dioxide Interface");
        getSupportActionBar().setSubtitle("Cliff");

        vehicleCompany =  (AutoCompleteTextView)findViewById(R.id.vehicleCompanies);
        distance = (EditText) findViewById(R.id.travelDistance);
        search = (AppCompatButton) findViewById(R.id.searchModels);
        modelList = (RecyclerView) findViewById(R.id.modelList);
        rootLayout = (ConstraintLayout) findViewById(R.id.rootLayout);

        vehicleCompanyArray = new HashMap<String,String>();
        vehicleCompanyArray.put("Nissan","bf111d61-70c6-476f-bf45-9bad9e526d4c");
        vehicleCompanyArray.put("Toyoto","2b1d0cd5-59be-4010-83b3-b60c5e5342da");
        vehicleCompanyArray.put("Ford","647f3fec-9a31-48c8-acc2-359f39cc3122");

        //Showing list of Vehicle Companies
        ArrayAdapter<String> adapter = new ArrayAdapter<String>
                (this,android.R.layout.select_dialog_item, vehicleCompanyArray.keySet().toArray(new String[0]));

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

        models = new ArrayList<CarModel>();

        adt = new CarModelAdapter(models);
        modelList.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
        modelList.setAdapter(adt);

        vehicleMakeURL = getResources().getString(R.string.vehicle_make_url);
        APIKEY = getResources().getString(R.string.carbon_api_key);
    }

    class CarModel {
        private String name;
        private String id;

        CarModel(String id,String name) {
            this.name= name;
            this.id = id;
        }

        String getCarName() {
            return this.name;
        }

        String getId() {
            return this.id;
        }
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
            Toast.makeText(CarbonDetectActivity.this,"Please enter travel distance",Toast.LENGTH_LONG).show();
            distance.requestFocus();
        } else if (company.equals("")) {
            Toast.makeText(CarbonDetectActivity.this,"Please enter vehicle company",Toast.LENGTH_LONG).show();
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
        editor.apply();
    }

    public void onLoadModels() {


        String vehicleMakeID = vehicleCompanyArray.get(vehicleCompany.getText().toString());

        AlertDialog dialog = new AlertDialog.Builder(CarbonDetectActivity.this)
                .setTitle("Downloading")
                .setMessage("Vehicle models for "+vehicleCompany.getText())
                .setView(new ProgressBar(CarbonDetectActivity.this))
                .show();

        Executor newThread = Executors.newSingleThreadExecutor();
        newThread.execute( () -> {
            try {

                URL url = new URL(vehicleMakeURL+"/"+vehicleMakeID+"/vehicle_models");
                HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setRequestProperty("Authorization", "Bearer "+APIKEY);
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

    private class CarModels extends RecyclerView.ViewHolder {
        TextView modelName;

        public CarModels(View itemView) {
            super(itemView);
            itemView.setOnClickListener(click -> {

                int position = getAdapterPosition();
                String model = ((CarModel)models.get(position)).getCarName();
                Toast.makeText(getApplicationContext(),model,Toast.LENGTH_LONG).show();

            });

            modelName = itemView.findViewById(R.id.modelName);
        }
    }

    private class CarModelAdapter extends RecyclerView.Adapter<CarModels> {

        ArrayList<CarModel> carModels;

        public CarModelAdapter(ArrayList<CarModel> carModels) {
            this.carModels = carModels;
        }

        @NonNull
        @Override
        public CarModels onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View itemView = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.layout_model, parent, false);
            CarModels holder = new CarModels(itemView);
            return holder;
        }

        @Override
        public void onBindViewHolder(CarModels holder, int position) {
            holder.modelName.setText(((CarModel)carModels.get(position)).getCarName());
        }

        @Override
        public int getItemCount() {
            return carModels.size();
        }
    }
}