package com.project.cst2335.Fragments;

import android.os.Bundle;

import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.project.cst2335.R;
import com.project.cst2335.Utils.Constants;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;


public class CarModelDetailFragment extends Fragment {

    private TextView estimateDetails;
    private Button deleteButton;
    private Button saveButton;
    private Button closeButton;

    private String newLine = "\n";

    private String model_id;
    private String model_name;
    private String display;
    private int distance;
    private String distance_unit;
    private String vehicle_make;

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
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_car_model_detail, container, false);

        estimateDetails = rootView.findViewById(R.id.estimateDetails);

        onLoadModels();
        return rootView;
    }

    public void onLoadModels() {

        AlertDialog dialog = new AlertDialog.Builder(getContext())
                .setTitle("Downloading")
                .setView(new ProgressBar(getContext()))
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
                object.put("distance_unit","mi");
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

    public void updateValues(JSONObject estimate) throws JSONException {
        String estimateText = getResources().getString(R.string.carbon_estimates,estimate.getDouble("carbon_g"),
                estimate.getDouble("carbon_lb"),
                estimate.getDouble("carbon_kg"),
                estimate.getDouble("carbon_mt"));
        estimateDetails.setText(estimateText);
    }
}