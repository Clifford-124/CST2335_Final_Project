package com.project.cst2335;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;

import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

public class PexelsActivity extends AppCompatActivity {

    EditText searchItem;
    AppCompatButton searchPictures;

    protected SharedPreferences sharedPref;
    protected SharedPreferences.Editor editor;
    protected String preference = "finalProject";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pexels);

        getSupportActionBar().setTitle("Pexels");
        getSupportActionBar().setSubtitle("Seema");

        searchItem = (EditText) findViewById(R.id.searchItem);
        searchPictures = (AppCompatButton) findViewById(R.id.searchPictures);

        retrieveData();

        searchPictures.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                validateInputs();
            }
        });
    }
    public void retrieveData() {
        SharedPreferences prefs = getSharedPreferences (preference,MODE_PRIVATE);
        String searchValue = prefs.getString("pexelsSearch", "-");
        if (!searchValue.equals("-")) searchItem.setText(searchValue);
    }

    public void validateInputs() {
        String searchValue = searchItem.getText().toString();
        if (searchValue.equals("")) {
            Toast.makeText(PexelsActivity.this,"Please enter something to search",Toast.LENGTH_LONG).show();
            searchItem.requestFocus();
        }
        else {
            saveData(searchValue);
            onLoadModels();
        }
    }

    public void saveData(String searchValue) {
        editor = getSharedPreferences(preference, MODE_PRIVATE).edit();
        editor.putString("pexelsSearch", searchValue);
        editor.apply();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        if (item.getItemId() == R.id.help) {
            showHelpDialog();
        }
        return super.onOptionsItemSelected(item);
    }

    public void showHelpDialog() {
        new AlertDialog.Builder(PexelsActivity.this)
                .setTitle("Pexel Interface")
                .setMessage("This interface will search for pictures in Pexels")
                .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        return;
                    }
                }).show();
    }
    public void onLoadModels() {

    }
}