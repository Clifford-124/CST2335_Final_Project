package com.project.cst2335;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.snackbar.Snackbar;

public class PexelsActivity extends AppCompatActivity {

    private EditText searchItem;

    private AlertDialog alert;
    private Button search_button;
    private SharedPreferences prefs;
    private String search_word;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pexels);

        searchItem =  (EditText) findViewById(R.id.searchItem);
        search_button = (Button)  findViewById(R.id.search_button);

        //Store search word typed by user in SharedPreferences
        prefs = getSharedPreferences("myData", Context.MODE_PRIVATE);

        //check if file associated with name "value" is there, if not set to empty string
        //getXXX(key, default value)
        search_word = prefs.getString("value","test");
        searchItem.setText(search_word);

        //when the search button is clicked
        search_button.setOnClickListener((vw)->{

            validateInput();
        });
    }


    /**
     * This function checks if the search box is empty,
     * if empty we pop up a Toast as a message to the user
     */
    public void validateInput() {

        String userInput = searchItem.getText().toString();
        if (userInput.equals("")) {
            Toast.makeText(PexelsActivity.this, "Please enter something to search", Toast.LENGTH_LONG).show();
            searchItem.requestFocus(); //select the search item for user to enter input

        }else {

            //AlertDialog
            AlertDialog.Builder builder = new AlertDialog.Builder(PexelsActivity.this);
            builder.setMessage("Do you want to search for "+userInput +" using internet connection ?");
            builder.setTitle("Question: ");

            builder.setNegativeButton("No", (dialog, cl)->{
                //if  user clicks no, do nothing

            });

            builder.setPositiveButton("Yes", (dialog, cl)->{
                //get input text
                String user_input_string = searchItem.getText().toString();

                //save data
                SharedPreferences.Editor editor = prefs.edit();
                editor.putString("value",user_input_string);//store key and value
                editor.apply();

                TextView messageText = (TextView) findViewById(R.id.message);
                Snackbar.make(messageText, "Sending request over internet to get images...", Snackbar.LENGTH_LONG).show();
            });

            //make the Alert window appear
            builder.create().show();

        }
    }


}