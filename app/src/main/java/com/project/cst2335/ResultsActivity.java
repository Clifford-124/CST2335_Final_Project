package com.project.cst2335;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.android.material.snackbar.Snackbar;

public class ResultsActivity extends AppCompatActivity {
    RecyclerView caseList;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_results);
        caseList = findViewById(R.id.resultsRecycler);
        caseList.setAdapter(new MyCaseAdapter());


        Intent fromPrevious = getIntent();
        String searchDate = fromPrevious.getStringExtra("Search Date");

        TextView cView= findViewById(R.id.caseView);
        cView.setText("Covid Cases after:  " + searchDate);
    }

    private class MyCaseViews extends RecyclerView.ViewHolder{

        TextView caseView;

        public MyCaseViews(View itemView) {
            super(itemView);
            caseView = itemView.findViewById(R.id.casesbydate);
            itemView.setOnClickListener(click->{

                Snackbar.make(caseView, "This is covid data for date: " , Snackbar.LENGTH_LONG).show();
                        AlertDialog.Builder builder = new AlertDialog.Builder(ResultsActivity.this);

                        builder.setTitle("Confirm!")
                                .setMessage("Make sure date format is correct" + caseView)
                                .create().show();
            });

        }
    }

    private class MyCaseAdapter extends RecyclerView.Adapter<MyCaseViews> {
        @Override
        public MyCaseViews onCreateViewHolder(ViewGroup parent, int viewType) {
            return null;
        }

        @Override
        public void onBindViewHolder(MyCaseViews holder, int position) {

        }

        @Override
        public int getItemCount() {
            return 0;
        }
    }
}