package com.project.cst2335;

import android.content.Context;

import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;


import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.snackbar.Snackbar;


import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
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

/**

 */
public  class Covid_Dates_Fragment extends Fragment {


    //Create an OpenHelper to store data:
    CovidDbOpener opener;   // object of CovidDbOpener
    Button covidSearchButton;
    EditText covidSearchContent;
    RecyclerView covidRecyclerView;
    MyCovidAdapter covidAdapter;
    ArrayList<CovidInformation> covidInfoArrayList;
    SQLiteDatabase databaseOpener;

    /**
     * This is the onCreateView method, which will inflate the layout with covid details once the
     *      * date is selected.
     * @param inflater
     * @param container
     * @param savedInstanceState
     * @return
     */
    @Override //similar to onCreate in ChatRoom.java
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View covidLayout = inflater.inflate(R.layout.covid_main, container, false);
        //find button, editText, and recycleview:
        covidSearchContent = covidLayout.findViewById(R.id.covid_editText);
        covidSearchButton = covidLayout.findViewById(R.id.searchBtn);
        opener = new CovidDbOpener(getContext());
        databaseOpener = opener.getWritableDatabase();
        covidRecyclerView = covidLayout.findViewById(R.id.covid_myRecycler);
        covidAdapter = new MyCovidAdapter();
        covidRecyclerView.setAdapter(covidAdapter);
        covidRecyclerView.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false));
        covidInfoArrayList = new ArrayList<>();

        /**
         * This is sharedpreferences object which will store the last entered value in
         * date search field when the application restarts
         * */

        SharedPreferences prefs = this.getActivity().getSharedPreferences("MyData", Context.MODE_PRIVATE);
        String searchDate = prefs.getString("Search Date", "");
        SharedPreferences.Editor edtr = prefs.edit();
        covidSearchContent.setText(searchDate);
        /**
         * Click Listener for search button
         * to
         * .
         */
        covidSearchButton.setOnClickListener(click -> {
            String typed = covidSearchContent.getText().toString();
            Toast.makeText(getActivity(), "OK! Here are search results for your entered date ", Toast.LENGTH_SHORT).show();
            edtr.putString("Search Date", typed);
            edtr.apply();
            AlertDialog alertDialog = new AlertDialog.Builder(getContext())
                    .setTitle("Fetching Results")
                    .setMessage("Searching for covid cases from " + typed + " till date")
                    .setView(new ProgressBar(getContext()))
                    .show();


            Executor newThread = Executors.newSingleThreadExecutor();
            newThread.execute(() -> {

                try {
                    String serverUrl = "https://api.covid19tracker.ca/reports?after=%s";

                    String stringURL = String.format(serverUrl, URLEncoder.encode(typed, "UTF-8"));

                    URL url = new URL(stringURL); //build the server connection
                    HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
                    InputStream in = new BufferedInputStream(urlConnection.getInputStream());
                    String text = (new BufferedReader(
                            new InputStreamReader(in, StandardCharsets.UTF_8)
                    )).lines()
                            .collect(Collectors.joining("\n"));

                    JSONObject theDocument = new JSONObject(text);
                    JSONArray dateArray = theDocument.getJSONArray("data");
                    //run iterations to get date for each position in the array
                    for (int j = 0; j < dateArray.length(); j++) {

                        JSONObject objPos = dateArray.getJSONObject(j);
                        String date = objPos.getString("date");
                        int total_cases = objPos.getInt("total_cases");
                        int total_fatalities = objPos.getInt("total_fatalities");
                        int total_hospitalizations = objPos.getInt("total_hospitalizations");
                        int total_recoveries = objPos.getInt("total_recoveries");
                        int total_vaccinations = objPos.getInt("total_vaccinations");

                        CovidInformation thisCovid = new CovidInformation(date, total_cases, total_fatalities, total_hospitalizations, total_vaccinations, total_recoveries);

                        covidInfoArrayList.add(thisCovid);
                        covidSearchContent.setText("");

                        getActivity().runOnUiThread(() -> {
                            covidAdapter.notifyItemInserted(covidInfoArrayList.size() - 1);

                        });
                    }
                    getActivity().runOnUiThread(() -> alertDialog.hide());


                } catch (IOException | JSONException ioe) {
                    Log.e("Connection error", ioe.getMessage());
                }

            });

        });

        return covidLayout;

    }

    public class MyCovidAdapter extends RecyclerView.Adapter<MyCasesViewHolder> {

        @NonNull
        @Override
        public MyCasesViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

            //Load a new row from the layout file:
            LayoutInflater li = getLayoutInflater();
            View thisRow;
            //import layout for a row:

            thisRow = li.inflate(R.layout.covid_view, parent, false);

            return new MyCasesViewHolder( thisRow );
        }


        //initializes a Row at position
        @Override
        public void onBindViewHolder(MyCasesViewHolder holder, int position) { //need an ArrayList to hold all the messages.



            CovidInformation thisCovidSearch = covidInfoArrayList.get(position);


            holder.tView.setText(thisCovidSearch.getDate());
        }

        public int getItemViewType(int position){
            return 5;

        }

        @Override
        public int getItemCount() {
            return covidInfoArrayList.size() ; //how many items in the list
        }
    }

    //this holds TextViews on a row:
    public class MyCasesViewHolder extends RecyclerView.ViewHolder{
        TextView tView;


        //View will be a ConstraintLayout
        public MyCasesViewHolder(View itemView) {
            super(itemView);

            itemView.setOnClickListener( click -> {
                CovidMainActivity parent = (CovidMainActivity) getContext();

                int position = getAbsoluteAdapterPosition();
                parent.userClickedMessage(covidInfoArrayList.get(position), position );
                Snackbar.make(itemView, "You selected a date to show details", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();


            });

            tView = itemView.findViewById(R.id.search_date);

        }

        /*public void notifyMessageDeleted(CovidInformation chosenSearch, int chosenPosition)
        {
            CovidInformation typed = covidInfoArrayList.get(chosenPosition);

            AlertDialog.Builder builder = new AlertDialog.Builder(  getContext() );

            builder.setTitle("Question:")
                    .setMessage("Do you want to delete this:" + typed.getDate())
                    .setNegativeButton("Negative", (dialog, click1)->{ })
                    .setPositiveButton("Positive", (dialog, click2)->{

                        covidInfoArrayList.remove(chosenPosition);
                        covidAdapter.notifyItemRemoved(chosenPosition);
                        Snackbar.make(covidSearchContent, "You removed item # " + chosenPosition, Snackbar.LENGTH_LONG)
                                .setAction("Undo", (click4)-> {
                                    covidInfoArrayList.add(chosenPosition, typed);
                                    covidAdapter.notifyItemInserted(chosenPosition);
                                    //reinsert into the database
                                    databaseOpener.execSQL( String.format( " Insert into %s values (\"%d\", \"%d\", \"%d\",\"%d\", \"%d\", \"%d\" );",
                                            CovidDbOpener.Table_name, chosenSearch.getId(), chosenSearch.getTotal_cases(), chosenSearch.getTotal_fatalities(),
                                            chosenSearch.getTotal_hospitalizations(), chosenSearch.getTotal_vaccinations(), chosenSearch.getTotal_recoveries()));

                                })
                                .show();
                        //delete from database:, returns number of rows deleted
                        databaseOpener.delete(CovidDbOpener.Table_name,
                                CovidDbOpener.Col_id +" = ?", new String[] { Long.toString( chosenSearch.getId() )  });
                    }).create().show();

        }*/
    }
    /**
     * class CovidInformation, for finding total cases,fatalities,recovreies, hospitalization and vaccination
     * Stores covid information as an object which can be stored in the array
     */
    class CovidInformation {
        private int total_cases;
        private int total_fatalities;
        private int total_vaccinations;
        private int total_hospitalizations;
        private int total_recoveries;
        private long id;
        private String date;

        /**
         * @param total_cases
         * @param total_fatalities
         * @param total_hospitalizations
         * @param total_recoveries
         * @param total_vaccinations
         */
        public CovidInformation(long id, String date, int total_cases, int total_fatalities, int total_hospitalizations, int total_vaccinations, int total_recoveries) {
            this.total_cases = total_cases;
            this.total_fatalities = total_fatalities;
            this.total_hospitalizations = total_hospitalizations;
            this.total_recoveries = total_recoveries;
            this.total_vaccinations = total_vaccinations;
            this.id = id;
            this.date = date;
        }

        public CovidInformation( String date, int total_cases, int total_fatalities, int total_hospitalizations, int total_vaccinations, int total_recoveries) {
            this.total_cases = total_cases;
            this.total_fatalities = total_fatalities;
            this.total_hospitalizations = total_hospitalizations;
            this.total_recoveries = total_recoveries;
            this.total_vaccinations = total_vaccinations;

            this.date = date;
        }

        public CovidInformation(String typed, long  id) {
            this.id = id;
            this.date = typed;
        }


        public String getDate() {
            return date;
        }

        /**
         * @return
         */
        public long  getId() {
            return id;

        }

        /**
         * @return
         */
        public int getTotal_cases() {
            return total_cases;
        }

        /**
         * @return
         */
        public int getTotal_fatalities() {
            return total_fatalities;
        }

        /**
         * @return
         */
        public int getTotal_vaccinations() {
            return total_vaccinations;
        }

        /**
         * @return
         */
        public int getTotal_hospitalizations() {
            return total_hospitalizations;
        }

        /**
         * @return
         */
        public int getTotal_recoveries() {
            return total_recoveries;
        }
    }



    public void notifyMessageSaved(CovidInformation selectedDateInformation, int selectedPosition) {

        Cursor results = databaseOpener.rawQuery("Select * from " + CovidDbOpener.Table_name + ";", null);//no arguments to the query
        //Convert column names to indices:
        int idIndex = results.getColumnIndex(CovidDbOpener.Col_id);

        int _idCol_Date = results.getColumnIndex(CovidDbOpener.Col_date);
        int col_total_cases_saved = results.getColumnIndex(CovidDbOpener.Col_total_cases);
        int col_total_fatalities_saved = results.getColumnIndex(CovidDbOpener.Col_total_fatalities);
        int col_total_host_saved = results.getColumnIndex(CovidDbOpener.Col_total_hospitalizations);
        int col_total_vacc_saved = results.getColumnIndex(CovidDbOpener.Col_total_vaccinations);
        int col_total_recovery = results.getColumnIndex(CovidDbOpener.Col_total_recoveries);


        //cursor is pointing to row -1
        while (results.moveToNext()) //returns false if no more data
        { //pointing to row 2
            int id = results.getInt(idIndex);
            String date = results.getString(_idCol_Date);
            int col_cases_saved = results.getInt(col_total_cases_saved);
            int col_fatalities_saved = results.getInt(col_total_fatalities_saved);
            int col_hospitalization_saved = results.getInt(col_total_host_saved);
            int col_vaccination_saved = results.getInt(col_total_vacc_saved);
            int col_recovery_saved = results.getInt(col_total_recovery);


            //add to arrayList:
            covidInfoArrayList.add(new CovidInformation(id, date, col_cases_saved, col_fatalities_saved, col_hospitalization_saved,
                    col_vaccination_saved, col_recovery_saved));


        }

    }


}




