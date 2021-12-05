package com.project.cst2335;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.snackbar.Snackbar;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.stream.Collectors;

public class CovidActivity extends AppCompatActivity {
    private String stringURL;
    //variable declaration
    RecyclerView caseList;
    Button sbtn;
    EditText date;
    CovidCases thisCase;
    MyCaseAdapter adt;
    ArrayList<CovidCases> cases= new ArrayList<>();
    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_covid);
        date = findViewById(R.id.etDate);
        sbtn = findViewById(R.id.srchbtn);
       caseList=findViewById(R.id.caserecycler);

       caseList.setAdapter(new MyCaseAdapter());
        adt = new MyCaseAdapter();
        caseList.setAdapter(adt);
       caseList.setLayoutManager(new LinearLayoutManager(this));






        SharedPreferences prefs = getSharedPreferences("MyData", Context.MODE_PRIVATE);
        String searchDate = prefs.getString("Search Date", "");
        SharedPreferences.Editor edtr = prefs.edit();
        date.setText(searchDate);
        sbtn.setOnClickListener(clk -> {
            //Intent nextPage = new Intent(CovidActivity.this, ResultsActivity.class);
            //nextPage.putExtra("Search Date", date.getText().toString());
            String typedDate = date.getText().toString();

            edtr.putString("Search Date", typedDate);
            edtr.apply();

            Toast.makeText(CovidActivity.this, "Getting search results for your entered date ", Toast.LENGTH_LONG).show();

            thisCase = new CovidCases(typedDate);
            cases.add(thisCase);
            date.setText("");
            adt.notifyItemInserted(cases.size() - 1);


            AlertDialog dialog = new AlertDialog.Builder(CovidActivity.this)
                    .setTitle("Getting Covid Results")
                    .setMessage("Please check covid statistics for "+typedDate+" to get an idea of the trend of cases, fatalities, hospitalizations, recoveries and vaccinations.")
                    .setView(new ProgressBar(CovidActivity.this))
                    .show();

            Executor newThread = Executors.newSingleThreadExecutor();
            newThread.execute( () -> {
                /* This runs in a separate thread */
                try{


                    stringURL = "https://api.covid19tracker.ca/reports?after="
                            + URLEncoder.encode(typedDate,"UTF-8");

                    URL url = new URL(stringURL);
                    HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
                    InputStream in = new BufferedInputStream(urlConnection.getInputStream());

                    /*XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
                    factory.setNamespaceAware(false);
                    XmlPullParser xpp = factory.newPullParser();
                    xpp.setInput( in  , "UTF-8");



                    while( xpp.next() != XmlPullParser.END_DOCUMENT )
                    {
                        switch(xpp.getEventType())
                        {
                            case XmlPullParser.START_TAG:
                                if(xpp.getName().equals("temperature"))
                                {
                                    current= xpp.getAttributeValue(null, "value");  //this gets the current temperature

                                    min=xpp.getAttributeValue(null, "min"); //this gets the min temperature

                                    max= xpp.getAttributeValue(null, "max"); //this gets the max temperature
                                }
                                else if(xpp.getName().equals("weather"))
                                {
                                    description= xpp.getAttributeValue(null, "value");  //this gets the weather description

                                    iconName=xpp.getAttributeValue(null, "icon"); //this gets the icon name
                                }
                                else if(xpp.getName().equals("humidity"))
                                {
                                    humidity=xpp.getAttributeValue(null,"value");
                                }
                                break;
                            case XmlPullParser.END_TAG:

                                break;
                            case XmlPullParser.TEXT:
                                break;
                        }

                    }*/


                    String text = (new BufferedReader(
                            new InputStreamReader(in, StandardCharsets.UTF_8)))
                            .lines()
                            .collect(Collectors.joining("\n"));

                    JSONObject theDocument = new  JSONObject( text ); //this converts the String to JSON Object

                    JSONArray dataArray = theDocument.getJSONArray("data");
                    JSONObject position0 = dataArray.getJSONObject(0);

                    int totalCases = position0.getInt("total_cases");
                    int totalFatalities = position0.getInt("total_fatalities");
                    int totalHospitalizations =position0.getInt("total_hospitalizations"); ;
                    int totalRecoveries = position0.getInt("total_recoveries");
                    int totalVaccinations = position0.getInt("total_recoveries");;





                    /*URL imgUrl = new URL( "https://openweathermap.org/img/w/" + iconName + ".png" );
                    HttpURLConnection connection = (HttpURLConnection) imgUrl.openConnection();
                    connection.connect();
                    int responseCode = connection.getResponseCode();
                    if (responseCode == 200) {
                        image = BitmapFactory.decodeStream(connection.getInputStream());

                    }
                    FileOutputStream fOut = null;
                    try {
                        fOut = openFileOutput( iconName + ".png", Context.MODE_PRIVATE);
                        image.compress(Bitmap.CompressFormat.PNG, 100, fOut);
                        fOut.flush();
                        fOut.close();
                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                    }*/

                    runOnUiThread( (  )  -> {
                        TextView tv = findViewById(R.id.totCases);
                        tv.setText("Total Cases are " + totalCases);
                        tv.setVisibility(View.VISIBLE);

                        tv = findViewById(R.id.totFatal);
                        tv.setText("Total Fatalities are " + totalFatalities);
                        tv.setVisibility(View.VISIBLE);

                        tv = findViewById(R.id.totHosp);
                        tv.setText("Total Hospitalizations are " + totalHospitalizations);
                        tv.setVisibility(View.VISIBLE);

                        tv = findViewById(R.id.totRecov);
                        tv.setText("Total Recoveries are " + totalRecoveries);
                        tv.setVisibility(View.VISIBLE);

                        tv = findViewById(R.id.totVacc);
                        tv.setText("Total Vaccinations are " + totalVaccinations);
                        tv.setVisibility(View.VISIBLE);

                        //dialog.hide();
                    });
                    int i=0; //just putting a code line to put breakpoint for above line of code

                }

                catch(IOException | JSONException ioe){
                    Log.e("Connection error:", ioe.getMessage());
                }


            } );







        });
    }

    private class CovidCases{
        String covidDate;

        public CovidCases(String covidDate) {
            this.covidDate = covidDate;
        }
        public String getCovidDate() {
            return covidDate;
        }
    }


    private class MyCaseViews extends RecyclerView.ViewHolder {

        TextView caseprint;


        public MyCaseViews(View itemView) {
            super(itemView);

            caseprint = itemView.findViewById(R.id.caseView);


            itemView.setOnClickListener(click -> {
                int position = getAbsoluteAdapterPosition();


                AlertDialog.Builder builder = new AlertDialog.Builder(CovidActivity.this);

                builder.setTitle("Question")
                        .setMessage("Do you want to delete the date: " + caseprint.getText())
                        .setNegativeButton("NO", (dialog, click1) -> {
                        })
                        .setPositiveButton("YES", (dialog, click2) -> {
                            CovidCases messageClicked = cases.get(position);
                            cases.remove(position);
                            adt.notifyItemRemoved(position);


                            Snackbar.make(caseprint, "You removed item " + position, Snackbar.LENGTH_LONG)
                                    .setAction("Undo", clk -> {
                                        cases.add(position, messageClicked);
                                        adt.notifyItemInserted(position);
                                    })
                                    .show();
                        })
                        .create().show();
            });

        }
    }



    private class MyCaseAdapter extends RecyclerView.Adapter<MyCaseViews>{
        @Override
        public MyCaseViews onCreateViewHolder(ViewGroup parent, int viewType) {
            LayoutInflater inflater = getLayoutInflater();
            int layoutID;
            layoutID = R.layout.activity_results;

            View loadedRow = inflater.inflate(layoutID, parent, false);
            return new MyCaseViews(loadedRow);
        }

        @Override
        public void onBindViewHolder(MyCaseViews holder, int position) {
            CovidCases thisCase = cases.get(position);
            holder.caseprint.setText(thisCase.getCovidDate());

        }

        @Override
        public int getItemCount() {
            return cases.size();
        }



    }


}