package com.project.cst2335.Activities;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.project.cst2335.Fragments.CarModelDetailFragment;
import com.project.cst2335.R;
import com.project.cst2335.Utils.Constants;

public class CarModelDetailActivity extends AppCompatActivity {

    Button saveButton,deleteButton,closeButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_car_model_detail);

        Toolbar myToolbar = findViewById(R.id.toolbar_detail);

        String activity = getResources().getString(R.string.carbon_dioxide_activity_main);
        String author = getResources().getString(R.string.carbon_dioxide_author_name);
        String version = getResources().getString(R.string.carbon_dioxide_version);

        myToolbar.setTitle(activity);
        myToolbar.setSubtitle(author + " - " + version);

        setSupportActionBar(myToolbar);

        //estimateDetails = (TextView) findViewById(R.id.esti);
        String model_id = getIntent().getStringExtra(Constants.ARG_MODEL_ID);
        String model_name = getIntent().getStringExtra(Constants.ARG_MODEL_NAME);
        String display = getIntent().getStringExtra(Constants.ARG_DISPLAY);
        int distance = getIntent().getIntExtra(Constants.ARG_DISTANCE,0);
        String distance_unit = getIntent().getStringExtra(Constants.ARG_DISTANCE_UNIT);
        String vehicle_nake = getIntent().getStringExtra(Constants.ARG_VEHICLE_MAKE);

        Toast.makeText(this,model_id , Toast.LENGTH_SHORT).show();

        saveButton = findViewById(R.id.saveEstimate);
        closeButton = findViewById(R.id.closeEstimate);
        deleteButton = findViewById(R.id.deleteEstimate);

        if (display.contentEquals("fromDB")) {
            deleteButton.setVisibility(View.VISIBLE);
            saveButton.setVisibility(View.GONE);
        } else {
            deleteButton.setVisibility(View.GONE);
            saveButton.setVisibility(View.VISIBLE);
        }

        closeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        deleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //DB Operations
            }
        });
        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //DB Operations
            }
        });
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
