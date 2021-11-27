package com.project.cst2335.Fragments;

import android.app.ProgressDialog;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.snackbar.Snackbar;
import com.project.cst2335.Database.DatabaseHelper;
import com.project.cst2335.Utils.Constants;
import com.project.cst2335.Models.PhotoModel;
import com.project.cst2335.R;
import com.project.cst2335.Adapters.PhotosAdapter;
import com.project.cst2335.Utils.Utilities;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.stream.Collectors;
/**
 *
 * fragment displying the list of photos from api or internal storage
 *
 * @author Seema Thapa Gurung
 * @version 1.0
 */
public class PhotoListFragment extends Fragment {

    private RecyclerView recyclerView;
    private EditText searchItem;
    private AlertDialog alert;
    private Button search_button;
    private Button btnSavedPhotos;
    private SharedPreferences prefs;
    private String search_word;
    private ProgressDialog progressDialog;
    DatabaseHelper db;
    String userInput;
    List<PhotoModel> photos;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {

        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_list, container, false);


        // Add the following lines to create RecyclerView
        recyclerView = view.findViewById(R.id.recyclerview);
        searchItem =  (EditText) view.findViewById(R.id.edtxtSearch);
        search_button = (Button)  view.findViewById(R.id.btnSearch);
        btnSavedPhotos = (Button)  view.findViewById(R.id.btnSavedPhotos);

        db= new DatabaseHelper(getContext());

       // get search term from shared prefs
        search_word = Utilities.getSavedSearchTerm(getContext());
        searchItem.setText(search_word);

        //when the search button is clicked
        search_button.setOnClickListener((vw)->{

            // validate the input
            validateInput();
        });

         btnSavedPhotos.setOnClickListener((vw)->{

             // get the photos from database
             doBackgroundProcessing(Constants.TASK_LOAD_OFFLINE);

        });



        recyclerView.setHasFixedSize(true);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false);
        recyclerView.setLayoutManager(layoutManager);


        return view;
    }

    /**
     * This function checks if the search box is empty,
     * if empty we pop up a Toast as a message to the user
     * else we get the results from api
     */
    public void validateInput() {

         userInput = searchItem.getText().toString();
        if (userInput.equals("")) {
            Toast.makeText(getContext(), R.string.msg_enter_to_search, Toast.LENGTH_LONG).show();
            searchItem.requestFocus(); //select the search item for user to enter input

        }else {


            Utilities.setSearchTerm(getContext(),userInput); // save the search term to shared prefs
            if(Utilities.checkConnectivity(getContext())){// check if internet is available them continue
                Utilities.closeKeyboard(getContext(),search_button); // close the keyboard
               doBackgroundProcessing(Constants.TASK_LOAD); // load the photos from api in background
            }
            else{ // if not internter then display a message to the user
                Snackbar.make(getView(), getResources().getString(R.string.msg_no_internet), Snackbar.LENGTH_LONG).show();
            }
        }
    }

    /**
     * does the background processing of the task passed
     *
     * @param taskId the task id of the task to perform in background
     */
    private void doBackgroundProcessing(int taskId){

        progressDialog= Utilities.showProgressDialog(getContext(),progressDialog); // show the progress bar dialog
        Executor newThread = Executors.newSingleThreadExecutor();
        newThread.execute(()-> { // start the executor

            if(taskId==Constants.TASK_LOAD){ // if user wants to see results from api
               photos= getDataFromApi(); // load photos from api
            }
            else if(taskId==Constants.TASK_LOAD_OFFLINE){ // of user wants to see offline photos
                photos= getDataFromDatabase(); // load photos from the database
            }



            ((AppCompatActivity) getContext()).runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Utilities.closeProgressDialog(progressDialog);
                    // display the results according to the task
                    if(taskId==Constants.TASK_LOAD){
                        if(photos!=null &&photos.size()>0){ // if photos have returned then display those in recycler view
                            recyclerView.setAdapter(new PhotosAdapter(photos,getActivity(),false));
                        }
                        else{
                            Snackbar.make(getView(), getResources().getString(R.string.err_processing), Snackbar.LENGTH_LONG).show();
                        }
                    }
                    else if(taskId==Constants.TASK_LOAD_OFFLINE){
                        if(photos!=null &&photos.size()>0){ // photos form database
                            recyclerView.setAdapter(new PhotosAdapter(photos,getActivity(),true));
                        }
                        else{
                            Snackbar.make(getView(), getResources().getString(R.string.err_processing), Snackbar.LENGTH_LONG).show();
                        }
                    }


                }
            });
        });
    }

    /**
     * gets the json from the api
     *
     * @return the list of photos
     */
    private List<PhotoModel> getDataFromApi(){
        try{
            // getting json from api
            URL url =  new URL(Constants.URL_GET_PHOTOS+userInput);
            HttpURLConnection urlConnection = (HttpURLConnection)url.openConnection();
            urlConnection.setRequestProperty (Constants.PEXELS_HEADER, Constants.PEXELS_API_KEY);
            if(urlConnection.getResponseCode()==HttpURLConnection.HTTP_OK){
                InputStream in = new BufferedInputStream(urlConnection.getInputStream());
                String text =(new BufferedReader(new InputStreamReader(in, StandardCharsets.UTF_8))).lines().collect(Collectors.joining("\n"));
                if(text!=null && text!=""){ // make sure json has returned
                    return parseDate(text); // parse the data to get the photo urls
                }
            }
        }
        catch (Exception e){}
        return null;
    }

    /**
     * gets the list of photos from the database for offline viewing
     *
     * @return list of photos
     */
    private List<PhotoModel> getDataFromDatabase(){
        return db.getAllPhotos();
    }

    /**
     * parses the json and converts it into photo model
     *
     * @param data json data to parse
     * @return list of photos
     */
    private List<PhotoModel> parseDate(String data){
        List<PhotoModel> list= new ArrayList<>();
        try{
            JSONObject obj1 = new JSONObject(data);
            JSONArray arr1 =obj1.getJSONArray(Constants.KEY_PHOTOS);
            for(int i=0; i<arr1.length(); i++){
                // parse json to get the urls
                JSONObject obj2= arr1.getJSONObject(i);
                int id = obj2.getInt(Constants.KEY_ID);
                JSONObject obj3 = obj2.getJSONObject(Constants.KEY_SRC);

                String tinyUrl = obj3.getString(Constants.KEY_TINY);
                String largeUrl = obj3.getString(Constants.KEY_LARGE);
                String large2xUrl = obj3.getString(Constants.KEY_LARGE2X);
                String mediumUrl = obj3.getString(Constants.KEY_MEDIUM);
                String originalUrl = obj3.getString(Constants.KEY_ORIGINAL);

                PhotoModel photo = new PhotoModel();
                photo.setId(id);
                photo.setLargeUrl(largeUrl);
                photo.setLarge2xUrl(large2xUrl);
                photo.setOriginalUrl(originalUrl);
                photo.setMediumUrl(mediumUrl);
                photo.setTinyUrl(tinyUrl);

                // add the photos to the list
                list.add(photo);
            }
        }
        catch (Exception e){ }
        return list;
    }
}
