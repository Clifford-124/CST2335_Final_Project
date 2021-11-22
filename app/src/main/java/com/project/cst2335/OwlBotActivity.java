package com.project.cst2335;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.snackbar.Snackbar;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

public class OwlBotActivity extends AppCompatActivity {

    private String stringURL;
    RecyclerView wordList;
    EditText txtWordSearch;
    SharedPreferences prefs;
    Button btnSearch;
    MyWordAdapter adt;
    ArrayList<String> arrayWords = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_owlbot_dictionary);

        wordList = findViewById(R.id.myrecycler);
        txtWordSearch = findViewById(R.id.textView);
        prefs = getSharedPreferences("MyData", Context.MODE_PRIVATE);
        txtWordSearch.setText(prefs.getString("word", ""));
        btnSearch = findViewById(R.id.btnSearch);
        adt = new MyWordAdapter(arrayWords);
        wordList.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
        wordList.setAdapter(adt);


        btnSearch.setOnClickListener((click) -> {

            if (!txtWordSearch.getText().equals("")) {
                arrayWords.add(txtWordSearch.getText().toString());
                adt = new MyWordAdapter(arrayWords);
                adt.notifyItemInserted(arrayWords.size() - 1);
                Toast.makeText(getApplicationContext(), "Word Added Successfully",
                        Toast.LENGTH_SHORT).show();

                SharedPreferences prefs = getSharedPreferences("MyData", Context.MODE_PRIVATE);

                SharedPreferences.Editor editor = prefs.edit();
                editor.putString("word", txtWordSearch.getText().toString());
                editor.apply();

                txtWordSearch.setText("");
            } else {
                Toast.makeText(getApplicationContext(), "Enter the valid text",
                        Toast.LENGTH_SHORT).show();
            }
        });
    }

    private class MyRowViews extends RecyclerView.ViewHolder {
        TextView wordDetails;

        public MyRowViews(View itemView) {
            super(itemView);
            itemView.setOnClickListener(click -> {

                        int position = getAdapterPosition();
                        String removedWord = arrayWords.get(position);
                        MyRowViews newRow = adt.onCreateViewHolder(null, adt.getItemViewType(position));

                        AlertDialog.Builder builder = new AlertDialog.Builder(OwlBotActivity.this);
                        builder.setMessage("Do you want to delete this? ")
                                .setTitle("Word Detail")
                                .setNegativeButton("No", (dialog, cl) -> { })

                                .setPositiveButton("Yes", (dialog, cl) -> {

                                    arrayWords.remove(position);
                                    adt.notifyItemRemoved (arrayWords.size() - 1);
                                    Snackbar.make(wordDetails, " You removed: " + wordDetails.getText().toString(), Snackbar.LENGTH_LONG).setAction("Undo", clk -> {
                                        arrayWords.add(position, removedWord);
                                        adt.notifyItemInserted(position);

                                    })
                                            .show();
                                })
                                .create().show();
                    });

                wordDetails = itemView.findViewById(R.id.txtSingleWord);
            }
        }


        private class MyWordAdapter extends RecyclerView.Adapter<MyRowViews> {

            ArrayList<String> arrayWords;

            public MyWordAdapter(ArrayList<String> passedarrayWords) {
                this.arrayWords = passedarrayWords;
            }

            @Override
            public MyRowViews onCreateViewHolder(ViewGroup parent, int viewType) {
                //  return new words.MyRowViews(getLayoutInflater().inflate(R.layout.word_detail, parent, false));
                View itemView = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.activity_single_word, parent, false);

                MyRowViews holder = new MyRowViews(itemView);
                return holder;
            }

            @Override
            public void onBindViewHolder(MyRowViews holder, int position) {
                holder.wordDetails.setText(arrayWords.get(position));
            }

            @Override
            public int getItemCount() {
                return arrayWords.size();
            }
        }

    }

