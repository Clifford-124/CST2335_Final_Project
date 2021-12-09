package com.project.cst2335.Activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.appcompat.widget.Toolbar;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.Toast;

import com.google.android.material.navigation.NavigationView;
import com.google.android.material.snackbar.Snackbar;
import com.project.cst2335.Adapters.CarModelAdapter;
import com.project.cst2335.Database.DatabaseHelper;
import com.project.cst2335.Models.CarModel;
import com.project.cst2335.R;
import com.project.cst2335.Utils.Constants;
import com.project.cst2335.Utils.Utilities;

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
public class CarbonActivity extends AppCompatActivity  implements CarModelAdapter.ItemClickListener {

    //Declaring Sharedpreferences to stpre data
    SharedPreferences sharedPref;
    SharedPreferences.Editor editor;
    String preference = "finalProject";

    //Views
    AutoCompleteTextView vehicleCompany;
    EditText distance;
    Button search, open_saved_models;
    RecyclerView modelList;
    LinearLayout rootLayout;
    RadioButton btn_km, btn_mi;

    //Adapter for Recycler
    CarModelAdapter adt;

    //Contains list of model class
    //Created from API or from Database
    ArrayList<CarModel> models;

    //Contains Vehicle make name and id
    HashMap<String, String> vehicleCompanyArray;

    //Database functions
    private DatabaseHelper db;

    //Views
    DrawerLayout drawer;
    ActionBarDrawerToggle toggle;
    NavigationView navigationView;

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


        drawer = findViewById(R.id.drawer_layout);
        toggle = new ActionBarDrawerToggle(this,drawer,myToolbar,R.string.navigation_drawer_open,R.string.navigation_drawer_close); //create Hamburger button
        drawer.addDrawerListener(toggle); //make the button popout
        toggle.syncState();

        navigationView = findViewById(R.id.popout_menu);

        navigationView.setCheckedItem(R.id.navpexelsProject);

        //when user clicks on the popout menu
        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                Utilities.startActivity(CarbonActivity.this,item.getItemId());
                drawer.closeDrawer(GravityCompat.START);//close the drawer
                finish();
                return true;
            }
        });


        //Initializing all views
        vehicleCompany = (AutoCompleteTextView) findViewById(R.id.vehicleCompanies);
        distance = (EditText) findViewById(R.id.travelDistance);
        search = (Button) findViewById(R.id.searchModels);
        open_saved_models = (Button) findViewById(R.id.openSavedModels);
        modelList = (RecyclerView) findViewById(R.id.modelList);
        rootLayout = (LinearLayout) findViewById(R.id.rootLayout);
        btn_km = (RadioButton) findViewById(R.id.unit_km);
        btn_mi = (RadioButton) findViewById(R.id.unit_mi);
        btn_km.setChecked(true);

        //Adding 2 vehicle_makes in Array
        vehicleCompanyArray = new HashMap<String, String>();
        vehicleCompanyArray.put("Nissan", "bf111d61-70c6-476f-bf45-9bad9e526d4c");
        vehicleCompanyArray.put("Toyoto", "2b1d0cd5-59be-4010-83b3-b60c5e5342da");
        vehicleCompanyArray.put("Ford", "647f3fec-9a31-48c8-acc2-359f39cc3122");

        //Showing list of Vehicle Companies
        ArrayAdapter<String> adapter = new ArrayAdapter<String>
                (this, android.R.layout.select_dialog_item, vehicleCompanyArray.keySet().toArray(new String[0]));

        vehicleCompany.setThreshold(1);
        //Set Adapter in AutoComplete TextView
        vehicleCompany.setAdapter(adapter);
        vehicleCompany.setTextColor(Color.BLACK);


        //Load Data from sharedPreferences if any
        retrieveData();

        //Search Button Listener to search models through API
        search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                loadFromAPI();
            }
        });

        //Saved Button Listener to retrive data from database
        open_saved_models.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                loadFromDB();
            }
        });

        db = new DatabaseHelper(this);

        models = new ArrayList<CarModel>();

        //Set Layout in Recycler View
        modelList.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
    }


    /**
     *
     * Reccycler view's adapter click listener. Will start new activity
     * when user selects any model from list
     *
     * @param model
     * @param display
     */
    @Override
    public void onItemClick(CarModel model, String display) {

        String company = vehicleCompany.getText().toString();
        String travelDistance = distance.getText().toString();
        String travelDistanceUnit = "km";
        if (btn_mi.isChecked()) {
            travelDistanceUnit = "mi";
        } else if (btn_km.isChecked()) {
            travelDistanceUnit = "km";
        }

        //Create new Intent
        Intent detailIntent = new Intent(CarbonActivity.this, CarModelDetailActivity.class);

        //Pass data in intent
        detailIntent.putExtra(Constants.ARG_MODEL_ID, model.getId());
        detailIntent.putExtra(Constants.ARG_MODEL_NAME, model.getCarName());
        detailIntent.putExtra(Constants.ARG_VEHICLE_MAKE, company);

        if (display.contentEquals("fromAPI")) {
            //set display as fromAPI and pass all details related to model
            detailIntent.putExtra(Constants.ARG_DISTANCE, Integer.parseInt(travelDistance));
            detailIntent.putExtra(Constants.ARG_DISTANCE_UNIT, travelDistanceUnit);
            detailIntent.putExtra(Constants.ARG_DISPLAY, "fromAPI");
        } else if (display.contentEquals("fromDB")) {
            //If model is From database set display as fromDB and fetch details from db
            detailIntent.putExtra(Constants.ARG_DISPLAY, "fromDB");
        }
        //Clear recycler view's adapter
        modelList.setAdapter(null);
        //start activity
        startActivity(detailIntent);
    }

    /**
     *
     * Will display carmodels in recyclerview from database or from api
     *
     * @param display
     */
    public void showCarModels(String display) {
        Snackbar.make(rootLayout, getResources().getString(R.string.vehicle_make_found,models.size()), Snackbar.LENGTH_LONG).show();
        //adt.notifyDataSetChanged();
        adt = new CarModelAdapter(this, models);
        adt.setDisplay(display);
        //Set Adapter & Layout in Recycler View
        modelList.setAdapter(adt);

        //set Adapter's click listener
        adt.addItemClickListener(this);
    }


    /**
     *
     * Will retrieve data stored in shared preference
     * and set in Views
     */
    public void retrieveData() {
        SharedPreferences prefs = getSharedPreferences(preference, MODE_PRIVATE);
        String companyValue = prefs.getString("company", "-");
        String travelDistanceValue = prefs.getString("travelDistance", "-");
        if (!travelDistanceValue.equals("-")) distance.setText(travelDistanceValue);
        if (!companyValue.equals("-")) vehicleCompany.setText(companyValue);
    }

    /**
     * This function will validate input from user and display error message in Toast
     *
     * @param travelDistance
     * @param company
     * @return
     */
    public boolean validateInputs(String travelDistance,String company) {
        if (travelDistance.equals("")) {
            Toast.makeText(CarbonActivity.this, getResources().getString((R.string.enter_distance)), Toast.LENGTH_LONG).show();
            distance.requestFocus();
            return false;
        } else if (travelDistance.equals("")) {
            Toast.makeText(CarbonActivity.this, getResources().getString(R.string.select_company), Toast.LENGTH_LONG).show();
            vehicleCompany.requestFocus();
            return false;
        }
        return true;
    }


    /**
     *
     * This function will validate user's input
     * and save date in shared preference
     * and will call api
     *
     */
    public void loadFromAPI() {
        String company = vehicleCompany.getText().toString();
        String travelDistance = distance.getText().toString();
        if (validateInputs(travelDistance,company)) {
            if (Utilities.checkConnectivity(CarbonActivity.this)) {
                Utilities.closeKeyboard(CarbonActivity.this,vehicleCompany);
                saveData(company, travelDistance);
                onLoadModels();
            } else {
                Snackbar.make(rootLayout, getResources().getString(R.string.msg_no_internet), Snackbar.LENGTH_LONG).show();
            }
        }
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.help)
            // showing help alert dialog
            showHelpDialog();
        else
            Utilities.startActivity(CarbonActivity.this, item.getItemId());
        return super.onOptionsItemSelected(item);
    }


    /**
     *
     * This function will load help menu
     *
     * @param menu
     * @return
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main_menu, menu);
        menu.findItem(R.id.carbonInterfaceMenu).setVisible(false);

        return true;
    }

    /**
     *
     * Help Menu - will display AlertDialog box
     *
     */
    public void showHelpDialog() {
        new AlertDialog.Builder(CarbonActivity.this)
                .setTitle(getResources().getString(R.string.carbon_interface))
                .setMessage(getResources().getString(R.string.carbon_interface_menu))
                .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        return;
                    }
                }).show();
    }


    /**
     *
     * Will svae data in shared preferences
     *
     * @param company
     * @param travelDistance
     */
    public void saveData(String company, String travelDistance) {
        editor = getSharedPreferences(preference, MODE_PRIVATE).edit();
        editor.putString("company", company);
        editor.putString("travelDistance", travelDistance);
        editor.apply();
    }


    /**
     *
     * Will load data from API
     * Parse response in CarModels Array list
     *
     */
    public void onLoadModels() {
        String vehicleMakeID = vehicleCompanyArray.get(vehicleCompany.getText().toString());

        models.clear();

        AlertDialog dialog = new AlertDialog.Builder(CarbonActivity.this)
                .setTitle(getResources().getString(R.string.downloading))
                .setMessage(getResources().getString(R.string.model_download_message,vehicleCompany.getText()))
                .setView(new ProgressBar(CarbonActivity.this))
                .setCancelable(false)
                .show();

        Executor newThread = Executors.newSingleThreadExecutor();
        newThread.execute(() -> {
            try {

                URL url = new URL(Constants.VEHICLE_MODELS_URL + "/" + vehicleMakeID + "/vehicle_models");
                HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setRequestProperty("Authorization", "Bearer " + Constants.API_KEY);
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
                        JSONObject carObj = carrray.getJSONObject(i).getJSONObject("data");

                        CarModel cmodel = new CarModel(carObj.getString("id"),
                                carObj.getJSONObject("attributes").getString("name"));
                        models.add(cmodel);

                    }

                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            dialog.hide();
                            showCarModels("fromAPI");
                        }
                    });

                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }


    /**
     *
     * Will load data from database
     * and parse in CarModel Array list
     *
     */
    private void loadFromDB() {
        AlertDialog dialog = new AlertDialog.Builder(CarbonActivity.this)
                .setTitle(getResources().getString(R.string.loading))
                .setMessage(getResources().getString(R.string.model_download_message,vehicleCompany.getText()))
                .setView(new ProgressBar(CarbonActivity.this))
                .setCancelable(false)
                .show();

        Executor newThread = Executors.newSingleThreadExecutor();
        newThread.execute(() -> { // start the executor

            models.clear();
            models = db.getModels();
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    //adt.setDisplay("fromDB");
                    dialog.dismiss();
                    showCarModels("fromDB");
                }
            });
        });
    }
}