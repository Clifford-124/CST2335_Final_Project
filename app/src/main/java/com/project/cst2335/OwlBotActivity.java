package com.project.cst2335;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
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

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.snackbar.Snackbar;


import java.util.ArrayList;


public class OwlBotActivity extends AppCompatActivity {

    /*RecyclerView wordList;
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
                        .setNegativeButton("No", (dialog, cl) -> {})

                        .setPositiveButton("Yes", (dialog, cl) -> {

                            arrayWords.remove(position);
                            adt.notifyItemRemoved(arrayWords.size() - 1);
                            Snackbar.make(wordDetails, " You removed: " + wordDetails.getText().toString(), Snackbar.LENGTH_LONG)
                                    .setAction("Undo", clk -> {
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
        public MyWordAdapter(ArrayList<String> passedArrayWords) {
            this.arrayWords = passedArrayWords;
        }

        @Override
        public MyRowViews onCreateViewHolder(ViewGroup parent, int viewType) {
            LayoutInflater inflater = getLayoutInflater();
                View loadedRow = inflater.inflate(R.layout.activity_single_word, parent, false);
                return new MyRowViews(loadedRow);
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
}*/

    RecyclerView wordList;
    Button btnSearch;
    EditText textWordSearch;
    WordTyped thisWord;
    MyWordAdapter adt;
    ArrayList<WordTyped> words= new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_owlbot_dictionary);

        textWordSearch = findViewById(R.id.textView);
        wordList = findViewById(R.id.myrecycler);
        btnSearch = findViewById(R.id.btnSearch);
        wordList.setAdapter(new MyWordAdapter());

        adt = new MyWordAdapter();
        wordList.setAdapter(adt);
        wordList.setLayoutManager(new LinearLayoutManager(this));

        SharedPreferences prefs = getSharedPreferences("MyData", Context.MODE_PRIVATE);
        prefs.getString("VariableName", "defaultValue");
        String Word = prefs.getString("Typed", "");
        SharedPreferences.Editor editor = prefs.edit();
        textWordSearch.setText(Word);

        editor.putString("Typed", Word);
        editor.apply();

        btnSearch.setOnClickListener((click) -> {
            String typed = textWordSearch.getText().toString();

            Toast.makeText(getApplicationContext(),  "Word Added Successfully", Toast.LENGTH_LONG).show();
            WordTyped thisWord = new WordTyped(typed, 1);
            words.add(thisWord);
            textWordSearch.setText("");
            adt.notifyItemInserted(words.size()-1);
            //clear the edittext and notify adapter of insertion
        });


    }


    private class WordTyped {
        String word;
        int btnSearch;


        public WordTyped(String word, int btnSearch) {
            this.word = word;
            this.btnSearch = btnSearch;
        }

        public String getWord() {
            return word;
        }

        public int getBtnSearch() {
            return btnSearch;
        }

    }


    private class MyRowViews extends RecyclerView.ViewHolder {

        TextView wordDetails;

        public MyRowViews(View itemView) {
            super(itemView);

            itemView.setOnClickListener( click -> {

                int position = getAdapterPosition();
                WordTyped wordRemoved = words.get(position);
                MyRowViews newRow = adt.onCreateViewHolder( null, adt.getItemViewType(position));

                AlertDialog.Builder builder = new AlertDialog.Builder( OwlBotActivity.this );
                builder.setMessage("Do you want to delete the word: " + wordDetails.getText())
                        .setTitle("Word Detail")
                        .setNegativeButton("No", (dialog, cl) -> { })
                        .setPositiveButton("Yes", (dialog, cl) -> {

                            words.remove(position);
                            adt.notifyItemRemoved(position);
                            Snackbar.make(wordDetails, "You deleted word #" + position, Snackbar.LENGTH_LONG)
                                    .setAction("Undo", clk -> {
                                        words.add(position, wordRemoved);
                                        adt.notifyItemInserted(position);
                                    }).show();
                        })
                        .create().show();
            });

            wordDetails = itemView.findViewById(R.id.txtSingleWord);

        }
    }


    private class MyWordAdapter extends RecyclerView.Adapter<MyRowViews> {



        @Override
        public MyRowViews onCreateViewHolder( ViewGroup parent, int viewType) {
            LayoutInflater inflater = getLayoutInflater();
            int layoutID = 0;
            if(viewType == 1)
                layoutID = R.layout.activity_single_word;

        View loadedRow = inflater.inflate(layoutID, parent, false);
            return new  MyRowViews(loadedRow);
    }

        @Override
        public void onBindViewHolder( MyRowViews holder, int position) {
            WordTyped thisWord = words.get(position);
            holder.wordDetails.setText(thisWord.getWord() );

        }


    @Override
    public int getItemCount() {
        return words.size();
    }


    @Override
    public int getItemViewType(int position) {
        WordTyped thisRow = words.get(position);
        return  thisRow.getBtnSearch();
    }


}
    }



