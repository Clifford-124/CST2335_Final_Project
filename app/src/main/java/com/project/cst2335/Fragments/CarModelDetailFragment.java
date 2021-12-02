package com.project.cst2335.Fragments;

import android.os.Bundle;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.widget.AppCompatButton;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.project.cst2335.Activities.CarModelDetailActivity;
import com.project.cst2335.Activities.CarbonActivity;
import com.project.cst2335.Database.DatabaseHelper;
import com.project.cst2335.Models.Estimates;
import com.project.cst2335.R;
import com.project.cst2335.Utils.Constants;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;


public class CarModelDetailFragment extends Fragment {

    //Database helper to perform database operations
    private DatabaseHelper db;

    //Views
    private TextView estimateDetails;
    private Button deleteButton;
    private Button saveButton;
    private Button closeButton;

    //Variables
    private String newLine = "\n";
    private String model_id;
    private String model_name;
    private String display;
    private int distance;
    private String distance_unit;
    private String vehicle_make;

    //Estimates object having all estimates related to model
    public Estimates estimates;

    public CarModelDetailFragment() {
    }

    public static CarModelDetailFragment newInstance(String model_id,String model_name, String display,int distance,String distance_unit,String vehicle_make) {
        CarModelDetailFragment fragment = new CarModelDetailFragment();
        Bundle args = new Bundle();

        args.putString(Constants.ARG_MODEL_ID, model_id);
        args.putString(Constants.ARG_MODEL_NAME, model_name);
        args.putString(Constants.ARG_DISPLAY, display);
        args.putInt(Constants.ARG_DISTANCE, distance);
        args.putString(Constants.ARG_DISTANCE_UNIT, distance_unit);
        args.putString(Constants.ARG_VEHICLE_MAKE,vehicle_make);

        fragment.setArguments(args);
        return fragment;
    }
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            vehicle_make = getArguments().getString(Constants.ARG_VEHICLE_MAKE);
            display = getArguments().getString(Constants.ARG_DISPLAY);
            distance_unit = getArguments().getString(Constants.ARG_DISTANCE_UNIT);
            model_id = getArguments().getString(Constants.ARG_MODEL_ID);
            model_name = getArguments().getString(Constants.ARG_MODEL_NAME);
            distance = getArguments().getInt(Constants.ARG_DISTANCE);

            estimates = new Estimates(model_name,distance,distance_unit);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_car_model_detail, container, false);

        estimateDetails = rootView.findViewById(R.id.estimateDetails);

        //Set views
        saveButton = rootView.findViewById(R.id.saveEstimate);
        closeButton = rootView.findViewById(R.id.closeEstimate);
        deleteButton = rootView.findViewById(R.id.deleteEstimate);

        if (display.contentEquals("fromDB")) {
            //Show Delete button & hide Save Button when data is displayed from db
            deleteButton.setVisibility(View.VISIBLE);
            saveButton.setVisibility(View.GONE);
        } else {
            //Hide Delete button & show save Button when data is displayed from api
            deleteButton.setVisibility(View.GONE);
            saveButton.setVisibility(View.VISIBLE);
        }

        //This will close activity
        closeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getActivity().finish();
            }
        });

        //This will take confirmation from user before deleting estimate from database
        deleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Confirmation Before Deletion
                AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                builder.setMessage(getResources().getString(R.string.msg_confirm_delete_estimate));
                builder.setTitle(R.string.msg_title_confirm_delete);
                builder.setNegativeButton(R.string.no, (dialog, cl)->{
                    //if  user clicks no, do nothing
                });

                builder.setPositiveButton(R.string.yes, (dialog, cl)->{
                    // of user confirms then delete the estimates from db
                    onDeleteEstimateFromDB();
                });

                //make the Alert window appear
                builder.create().show();
                //DB Operations
            }
        });

        //This will perform database operations to save data
        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //DB Operations
                doBackgroundProcessing();
            }
        });

        db = new DatabaseHelper(getActivity());

        if (display.contentEquals("fromAPI"))
            onLoadEstimatesFromAPI();
        else if (display.contentEquals("fromDB"))
            onLoadEstimatesFromDB();

        return rootView;
    }

    /**
     *
     * This function is responsible to save data in database
     *
     */
    private void doBackgroundProcessing() {

        AlertDialog dialog = new AlertDialog.Builder(getContext())
                .setTitle(getResources().getString(R.string.saving_estimate))
                .setView(new ProgressBar(getContext()))
                .setCancelable(false)
                .show();

        Executor newThread = Executors.newSingleThreadExecutor();
        newThread.execute(() -> { // start the executor
            db.saveModelEstimates(estimates);

            getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    dialog.dismiss();
                    Toast.makeText(getContext(),getResources().getString(R.string.estimate_saved),Toast.LENGTH_LONG).show();
                }
            });
        });
    }

    /**
     *
     * this function will delete estimate details from database
     *
     */
    public void onDeleteEstimateFromDB() {
        AlertDialog dialog = new AlertDialog.Builder(getContext())
                .setTitle(getResources().getString(R.string.deleting_estimate))
                .setView(new ProgressBar(getContext()))
                .setCancelable(false)
                .show();

        Executor newThread = Executors.newSingleThreadExecutor();
        newThread.execute( () -> {
            int id = Integer.parseInt(model_id);
            boolean deleted = db.deleteEstimateByID(id);
            getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    dialog.dismiss();
                    if (deleted) {
                        Toast.makeText(getContext(),getResources().getString(R.string.estimate_delete_confirmation), Toast.LENGTH_SHORT).show();
                        getActivity().finish();
                    } else {
                        Toast.makeText(getContext(),getResources().getString(R.string.estimate_delete_error), Toast.LENGTH_SHORT).show();
                    }
                }
            });
        });

    }


    /**
     *
     * This function will load estimate details from database by id
     * and set in Textview
     */
    public void onLoadEstimatesFromDB() {

        AlertDialog dialog = new AlertDialog.Builder(getContext())
                .setTitle(getResources().getString(R.string.loading))
                .setView(new ProgressBar(getContext()))
                .setCancelable(false)
                .show();

        Executor newThread = Executors.newSingleThreadExecutor();
        newThread.execute( () -> {
            int id = Integer.parseInt(model_id);
            Estimates estimate = db.getModelById(id);
            if (estimate != null)
            {
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        dialog.dismiss();
                        String estimateText = estimate.toString(getContext());
                        estimateDetails.setText(estimateText);
                    }
                });
            }
        });
    }

    /**
     *
     * This function will load estimate details from API
     *
     */
    public void onLoadEstimatesFromAPI() {

        AlertDialog dialog = new AlertDialog.Builder(getContext())
                .setTitle(getResources().getString(R.string.downloading))
                .setView(new ProgressBar(getContext()))
                .setCancelable(false)
                .show();

        Executor newThread = Executors.newSingleThreadExecutor();
        newThread.execute( () -> {
            HttpURLConnection urlConnection = null;
            try {

                URL url = new URL(Constants.VEHICLE_ESTIMATES_URL);
                urlConnection = (HttpURLConnection) url.openConnection();

                urlConnection.setRequestProperty("Authorization", "Bearer "+ Constants.API_KEY);
                urlConnection.setRequestMethod("POST");
                urlConnection.setRequestProperty("Content-Type", "application/json");
                urlConnection.setRequestProperty("Accept", "application/json");

                urlConnection.setDoOutput(true);
                urlConnection.setDoInput(true);

                JSONObject object = new JSONObject();

                object.put("type","vehicle");
                object.put("distance_unit",distance_unit);
                object.put("distance_value",distance);
                object.put("vehicle_model_id",model_id);
                System.out.printf("Logs "+object.toString()+"");

                OutputStream out = new BufferedOutputStream(urlConnection.getOutputStream());
                BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(
                        out, "UTF-8"));
                writer.write(object.toString());
                writer.flush();

                int statusCode = urlConnection.getResponseCode();
                if (statusCode == 201) {
                    BufferedReader buff = new BufferedReader(new InputStreamReader(urlConnection.getInputStream()));
                    StringBuilder dta = new StringBuilder();
                    String chunks = "";
                    while ((chunks = buff.readLine()) != null) {
                        dta.append(chunks);
                    }
                    JSONObject estimate= new JSONObject(dta.toString());
                    updateValues(estimate.getJSONObject("data").getJSONObject("attributes"));
                }
                dialog.dismiss();
            }
            catch(Exception e) {
                Log.e("API-logs",e.getMessage());
                e.printStackTrace();
            }finally {
                if (urlConnection != null) {
                    urlConnection.disconnect();
                }
            }
        });
    }


    /**
     *
     * This function will update Textview with estimate details
     *
     * @param estimate
     * @throws JSONException
     */
    public void updateValues(JSONObject estimate) throws JSONException {

        estimates.setCarbon_g(estimate.getDouble("carbon_g"));
        estimates.setCarbon_kg(estimate.getDouble("carbon_kg"));
        estimates.setCarbon_lb(estimate.getDouble("carbon_lb"));
        estimates.setCarbon_mt(estimate.getDouble("carbon_mt"));

        String estimateText = estimates.toString(getContext());
        estimateDetails.setText(estimateText);
    }
}