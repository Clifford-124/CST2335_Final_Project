package com.project.cst2335.Activities;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.project.cst2335.Fragments.CarModelDetailFragment;
import com.project.cst2335.Fragments.PhotoListFragment;
import com.project.cst2335.Models.CarModel;
import com.project.cst2335.R;
import com.project.cst2335.Utils.Constants;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

/**
 *
 * CarModelDetail activity responsible to display
 * selected model estimates
 *
 * @author Cliff
 * @version 1.0
 */
public class CarModelDetailActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_car_model_detail);

        //Setting Toolbar
        Toolbar myToolbar = findViewById(R.id.toolbar_detail);

        String activity = getResources().getString(R.string.carbon_dioxide_activity_main);
        String author = getResources().getString(R.string.carbon_dioxide_author_name);
        String version = getResources().getString(R.string.carbon_dioxide_version);

        myToolbar.setTitle(activity);
        myToolbar.setSubtitle(author + " - " + version);

        setSupportActionBar(myToolbar);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }
        //Fetch Data
        String model_id = getIntent().getStringExtra(Constants.ARG_MODEL_ID);
        String model_name = getIntent().getStringExtra(Constants.ARG_MODEL_NAME);
        String display = getIntent().getStringExtra(Constants.ARG_DISPLAY);
        int distance = getIntent().getIntExtra(Constants.ARG_DISTANCE,0);
        String distance_unit = getIntent().getStringExtra(Constants.ARG_DISTANCE_UNIT);
        String vehicle_nake = getIntent().getStringExtra(Constants.ARG_VEHICLE_MAKE);

        //Load Fragment and pass data
        CarModelDetailFragment detailFragment =
                CarModelDetailFragment.newInstance(model_id,
                        model_name,
                        display,
                        distance,
                        distance_unit,vehicle_nake);


        FragmentManager fMgr = getSupportFragmentManager();
        FragmentTransaction tx = fMgr.beginTransaction();
        tx.add(R.id.modelDetail, detailFragment);
        tx.commit();
    }
}