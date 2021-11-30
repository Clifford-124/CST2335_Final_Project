package com.project.cst2335;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.snackbar.Snackbar;

import java.util.ArrayList;

public class CovidActivity extends AppCompatActivity {

    //variable declaration
    RecyclerView caseList;
    Button sbtn;
    EditText date;
    CovidCases thisCase;
    MyCaseAdapter adt;
    ArrayList<CovidCases> cases= new ArrayList<>();
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

            edtr.putString("Search Date", date.getText().toString());
            edtr.apply();

            Toast.makeText(CovidActivity.this, "Getting search results for your entered date ", Toast.LENGTH_LONG).show();

            thisCase = new CovidCases(typedDate);
            cases.add(thisCase);
            date.setText("");
            adt.notifyItemInserted(cases.size() - 1);

            //startActivity(nextPage);

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
                        .setMessage("Do you want to delete the message: " + caseprint.getText())
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