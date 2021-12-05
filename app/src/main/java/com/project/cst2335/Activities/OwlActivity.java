package com.project.cst2335.Activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.material.navigation.NavigationView;
import com.google.android.material.snackbar.Snackbar;
import com.project.cst2335.Adapters.OwlModelAdapter;
import com.project.cst2335.Database.DatabaseHelper;
import com.project.cst2335.Models.OwlWord;
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
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

/**
 *
 * Oql activity responsible to display
 * definitions of word from Database and from API
 *
 * @author Renu
 * @version 1.0
 */
public class OwlActivity extends AppCompatActivity  implements OwlModelAdapter.ItemClickListener {

    //Declaring Sharedpreferences to stpre data
    SharedPreferences sharedPref;
    SharedPreferences.Editor editor;
    String preference = "finalProject";

    //Views
    EditText searchWord;
    Button searchWordBtn, openSavedWordsBtn;
    RecyclerView wordDefinitionList;
    LinearLayout rootLayout;

    //Adapter for Recycler
    OwlModelAdapter adt;

    //Contains list of model class
    //Created from API or from Database
    ArrayList<OwlWord> wordDefinitions;

    //Database functions
    private DatabaseHelper db;

    //Views
    DrawerLayout drawer;
    ActionBarDrawerToggle toggle;
    NavigationView navigationView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_owl);

        Toolbar myToolbar = findViewById(R.id.toolbar_main);

        String activity = getResources().getString(R.string.owl_bot_activity_main);

        myToolbar.setTitle(activity);

        setSupportActionBar(myToolbar);

        drawer = findViewById(R.id.drawer_layout);
        toggle = new ActionBarDrawerToggle(this,drawer,myToolbar,R.string.navigation_drawer_open,R.string.navigation_drawer_close); //create Hamburger button
        drawer.addDrawerListener(toggle); //make the button popout
        toggle.syncState();

        navigationView = findViewById(R.id.popout_menu_2);

        navigationView.setCheckedItem(R.id.navpexelsProject);

        //when user clicks on the popout menu
        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                int id = item.getItemId();
                if (id == R.id.navpexelsProject) {
                    // starting new activity when project is selected from the navigation drawer
                    Intent newIntent = new Intent(OwlActivity.this, PexelsActivity.class);
                    startActivity(newIntent);
                    // drawer.closeDrawers();
                } else if (id == R.id.carbonInterface) {
                    Intent newIntent = new Intent(OwlActivity.this, CarbonActivity.class);
                    startActivity(newIntent);
                    finish();
                } else if (id == R.id.dictionaryProject) {
                    // starting dictonary activity when project is selected from the toolbar icon
                    Intent OwlIntent = new Intent(OwlActivity.this, OwlActivity.class);
                    startActivity(OwlIntent);
                    finish();
                }
                drawer.closeDrawer(GravityCompat.START);//close the drawer
                return true;
            }
        });

        //Initializing all views
        searchWord = (EditText) findViewById(R.id.owlBotSearch);
        searchWordBtn = (Button) findViewById(R.id.searchWord);
        openSavedWordsBtn = (Button) findViewById(R.id.openSavedDefinitions);

        wordDefinitionList = (RecyclerView) findViewById(R.id.modelList);

        rootLayout = (LinearLayout) findViewById(R.id.rootLayout);

        //Load Data from sharedPreferences if any
        retrieveData();

        //Search Button Listener to search models through API
        searchWordBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                loadFromAPI();
            }
        });

        //Saved Button Listener to retrive data from database
        openSavedWordsBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                loadFromDB();
            }
        });

        db = new DatabaseHelper(this);

        wordDefinitions = new ArrayList<OwlWord>();

        //Set Layout in Recycler View
        wordDefinitionList.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
    }


    /**
     *
     * Reccycler view's adapter click listener. Will start new activity
     * when user selects any word definition from list
     *
     * @param model
     * @param display
     */
    @Override
    public void onItemClick(OwlWord model, String display) {

        //Create new Intent
        Intent detailIntent = new Intent(OwlActivity.this, OwlDetailActivity.class);

        //Pass data in intent
        detailIntent.putExtra("word", model);

        if (display.contentEquals("fromAPI")) {
            //set display as fromAPI and pass all details related to model
            detailIntent.putExtra(Constants.ARG_DISPLAY, "fromAPI");
        } else if (display.contentEquals("fromDB")) {
            //If model is From database set display as fromDB and fetch details from db
            detailIntent.putExtra(Constants.ARG_DISPLAY, "fromDB");
            //Clear recycler view's adapter
            wordDefinitionList.setAdapter(null);
        }
        //start activity
        startActivity(detailIntent);
    }

    /**
     *
     * Will display definitions in recyclerview from database or from api
     *
     * @param display
     */
    public void showModels(String display) {
        Snackbar.make(rootLayout, getResources().getString(R.string.owl_definitions_found,wordDefinitions.size()), Snackbar.LENGTH_LONG).show();

        adt = new OwlModelAdapter(this, wordDefinitions);
        adt.setDisplay(display);

        //Set Adapter & Layout in Recycler View
        wordDefinitionList.setAdapter(adt);

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
        String searchWordValue = prefs.getString("owlSearchWord", "-");
        if (!searchWordValue.equals("-")) searchWord.setText(searchWordValue);
    }

    /**
     * This function will validate input from user and display error message in Toast
     *
     * @param searchWordValue
     *
     * @return
     */
    public boolean validateInputs(String searchWordValue) {
        if (searchWordValue.equals("")) {
            Toast.makeText(OwlActivity.this, getResources().getString((R.string.owl_enter_word)), Toast.LENGTH_LONG).show();
            searchWord.requestFocus();
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
        String serachWordValue = searchWord.getText().toString();
        if (validateInputs(serachWordValue)) {
            if (Utilities.checkConnectivity(OwlActivity.this)) {
                saveData(serachWordValue);
                onLoadModels(serachWordValue);
            } else {
                Snackbar.make(rootLayout, getResources().getString(R.string.msg_no_internet), Snackbar.LENGTH_LONG).show();
            }
        }
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.help:
                // showing help alert dialog
                showHelpDialog();
                break;
            case R.id.pexelsProject:
                // starting new activity when project is selected from the roolbar icon
                Intent newIntent = new Intent(OwlActivity.this, PexelsActivity.class);
                startActivity(newIntent);
                break;
            case R.id.carbonInterfaceMenu:
                Intent intent2 = new Intent(OwlActivity.this, CarbonActivity.class);
                startActivity(intent2);
                break;
        }
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
        menu.findItem(R.id.OwlMenu).setVisible(false);
        return true;
    }

    /**
     *
     * Help Menu - will display AlertDialog box
     *
     */
    public void showHelpDialog() {
        new AlertDialog.Builder(OwlActivity.this)
                .setTitle(getResources().getString(R.string.owl_interface))
                .setMessage(getResources().getString(R.string.owl_interface_menu))
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
    public void saveData(String searchWordValue) {
        editor = getSharedPreferences(preference, MODE_PRIVATE).edit();
        editor.putString("owlSearchWord", searchWordValue);
        editor.apply();
    }


    /**
     *
     * Will load data from API
     * Parse response in CarModels Array list
     *
     */
    public void onLoadModels(String wordSearchValue) {

        AlertDialog dialog = new AlertDialog.Builder(OwlActivity.this)
                .setTitle(getResources().getString(R.string.downloading))
                .setMessage(getResources().getString(R.string.owl_download_definition,wordSearchValue))
                .setView(new ProgressBar(OwlActivity.this))
                .setCancelable(false)
                .show();

        wordDefinitions.clear();

        Executor newThread = Executors.newSingleThreadExecutor();
        newThread.execute(() -> {
            try {

                URL url = new URL(Constants.OWL_URL_GET_WORDS + "/" + wordSearchValue);
                HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setRequestProperty("Authorization", Constants.OWL_API_KEY);
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

                    Log.e("Yash","test"+dta.toString());
                    JSONObject mainObject = new JSONObject(dta.toString());
                    JSONArray definitions = mainObject.getJSONArray("definitions");
                    String pronunciation = mainObject.getString("pronunciation");
                    String word = mainObject.getString("word");

                    for (int i = 0; i < definitions.length(); i++) {
                        JSONObject definitionObj = definitions.getJSONObject(i);

                        String type =definitionObj.getString("type");
                        String definition =definitionObj.getString("definition");
                        String example =definitionObj.getString("example");
                        String imageUrl =definitionObj.getString("image_url");

                        OwlWord model = new OwlWord(type,definition,example,imageUrl,word,pronunciation);
                        wordDefinitions.add(model);
                    }

                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            dialog.hide();
                            showModels("fromAPI");
                        }
                    });

                }
            } catch (Exception e) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        dialog.hide();
                    }
                });
                e.printStackTrace();
            }
        });
    }


    /**
     *
     * Will load data from database
     * and parse in OwlWord Array list
     *
     */
    private void loadFromDB() {
        AlertDialog dialog = new AlertDialog.Builder(OwlActivity.this)
                .setTitle(getResources().getString(R.string.loading))
                .setMessage(getResources().getString(R.string.owl_loading_definitions))
                .setView(new ProgressBar(OwlActivity.this))
                .setCancelable(false)
                .show();

        Executor newThread = Executors.newSingleThreadExecutor();
        newThread.execute(() -> { // start the executor

            wordDefinitions.clear();
            wordDefinitions = db.getWords();
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    //adt.setDisplay("fromDB");
                    dialog.dismiss();
                    showModels("fromDB");
                }
            });
        });
    }
}