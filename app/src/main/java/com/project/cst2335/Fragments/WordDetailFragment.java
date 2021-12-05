package com.project.cst2335.Fragments;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;

import com.project.cst2335.Database.DatabaseHelper;
import com.project.cst2335.Models.Estimates;
import com.project.cst2335.Models.OwlWord;
import com.project.cst2335.R;
import com.project.cst2335.Utils.Constants;
import com.squareup.picasso.Picasso;

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


public class WordDetailFragment extends Fragment {

    //Database helper to perform database operations
    private DatabaseHelper db;

    //Views
    private Button deleteButton;
    private Button saveButton;
    private Button closeButton;

    private ImageView imageview;
    private TextView wordView;
    private TextView typeView;
    private TextView definitionsView;
    private TextView exampleView;
    private TextView pronunciationView;

    //owlWord ect having full definition of word
    public OwlWord word;
    public String display;

    public WordDetailFragment() {
    }

    public static WordDetailFragment newInstance(OwlWord word,String display) {
        WordDetailFragment fragment = new WordDetailFragment();
        Bundle args = new Bundle();

        args.putSerializable("word",word);
        args.putString("display",display);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            word = (OwlWord) getArguments().getSerializable("word");
            display = getArguments().getString("display");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_word_detail, container, false);

        //Set views
        saveButton = rootView.findViewById(R.id.save);
        closeButton = rootView.findViewById(R.id.close);
        deleteButton = rootView.findViewById(R.id.delete);

        imageview = rootView.findViewById(R.id.image_url);
        wordView = rootView.findViewById(R.id.word);
        definitionsView = rootView.findViewById(R.id.definiton);
        exampleView = rootView.findViewById(R.id.example);
        typeView = rootView.findViewById(R.id.type);
        pronunciationView = rootView.findViewById(R.id.pronunciation);

        if (display.contentEquals("fromDB")) {
            //Show Delete button & hide Save Button when data is displayed from db
            deleteButton.setVisibility(View.VISIBLE);
            saveButton.setVisibility(View.GONE);
        } else {
            //Hide Delete button & show save Button when data is displayed from api
            deleteButton.setVisibility(View.GONE);
            saveButton.setVisibility(View.VISIBLE);
        }

        //This will close activity
        closeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getActivity().finish();
            }
        });

        //This will take confirmation from user before deleting word details from database
        deleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Confirmation Before Deletion
                AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                builder.setMessage(getResources().getString(R.string.owl_word_delete_confirmation));
                builder.setTitle(R.string.msg_title_confirm_delete);
                builder.setNegativeButton(R.string.no, (dialog, cl)->{
                    //if  user clicks no, do nothing
                });

                builder.setPositiveButton(R.string.yes, (dialog, cl)->{
                    // of user confirms then delete the word from db
                    onDeleteWordFromDB();
                });

                //make the Alert window appear
                builder.create().show();
                //DB Operations
            }
        });

        //This will perform database operations to save data
        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //DB Operations
                doBackgroundProcessing();
            }
        });

        db = new DatabaseHelper(getActivity());

        setValues();

        return rootView;
    }


    /**
     *
     * This function will show values in Textview
     *
     */
    public void setValues() {
        if (!word.getImageURL().contentEquals(""))
            Picasso.get().load(word.getImageURL()).into(imageview);

        wordView.setText(getResources().getString(R.string.owl_word,word.getWord()));
        typeView.setText(getResources().getString(R.string.owl_type,word.getType()));
        definitionsView.setText(getResources().getString(R.string.owl_definitions,word.getDefinition()));
        exampleView.setText(getResources().getString(R.string.owl_example,word.getExample()));
        pronunciationView.setText((getResources().getString(R.string.pronunciation,word.getPronunciation())));
    }

    /**
     *
     * This function is responsible to save data in database
     *
     */
    private void doBackgroundProcessing() {

        AlertDialog dialog = new AlertDialog.Builder(getContext())
                .setTitle(getResources().getString(R.string.owl_word_saving))
                .setView(new ProgressBar(getContext()))
                .setCancelable(false)
                .show();

        Executor newThread = Executors.newSingleThreadExecutor();
        newThread.execute(() -> { // start the executor
            db.saveWordDefinitions(word);
            getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    dialog.dismiss();
                    Toast.makeText(getContext(),getResources().getString(R.string.owl_word_saved),Toast.LENGTH_LONG).show();
                }
            });
        });
    }

    /**
     *
     * this function will delete word definition and other details from database
     *
     */
    public void onDeleteWordFromDB() {
        AlertDialog dialog = new AlertDialog.Builder(getContext())
                .setTitle(getResources().getString(R.string.owl_word_deleting))
                .setView(new ProgressBar(getContext()))
                .setCancelable(false)
                .show();

        Executor newThread = Executors.newSingleThreadExecutor();
        newThread.execute( () -> {
            boolean deleted = db.deleteWordByID(word.getId());
            getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    dialog.dismiss();
                    if (deleted) {
                        Toast.makeText(getContext(),getResources().getString(R.string.owl_word_delete_success), Toast.LENGTH_SHORT).show();
                        getActivity().finish();
                    } else {
                        Toast.makeText(getContext(),getResources().getString(R.string.owl_word_delete_error), Toast.LENGTH_SHORT).show();
                    }
                }
            });
        });
    }
}