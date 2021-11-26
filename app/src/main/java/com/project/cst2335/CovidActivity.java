package com.project.cst2335;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

public class CovidActivity extends AppCompatActivity {

    //variable declaration

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_covid);

        EditText date = findViewById(R.id.etDate);
        Button sbtn = findViewById(R.id.srchbtn);
        TextView enter = findViewById(R.id.enterDate);

        SharedPreferences prefs = getSharedPreferences("MyData", Context.MODE_PRIVATE);
        String searchDate = prefs.getString("Search Date", "");

        date.setText(searchDate);


        SharedPreferences.Editor edtr = prefs.edit();


        sbtn.setOnClickListener(clk -> {
            Intent nextPage = new Intent(CovidActivity.this, ResultsActivity.class);
            nextPage.putExtra("Search Date", date.getText().toString());
            edtr.putString("Search Date", date.getText().toString());
            Toast.makeText(CovidActivity.this, "Getting search results for your entered date ", Toast.LENGTH_LONG).show();
            edtr.apply();
            startActivity(nextPage);

        });
    }
}