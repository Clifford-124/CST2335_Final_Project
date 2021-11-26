package com.project.cst2335.Fragments;

import static android.content.Context.INPUT_METHOD_SERVICE;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.snackbar.Snackbar;
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

public class PhotoListFragment extends Fragment {

    private RecyclerView recyclerView;
    private EditText searchItem;
    private AlertDialog alert;
    private Button search_button;
    private SharedPreferences prefs;
    private String search_word;
    private ProgressDialog progressDialog;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {

        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_list, container, false);


        // Add the following lines to create RecyclerView
        recyclerView = view.findViewById(R.id.recyclerview);
        searchItem =  (EditText) view.findViewById(R.id.searchItem);
        search_button = (Button)  view.findViewById(R.id.search_button);

       // get search term from shared prefs
        search_word = Utilities.getSavedSearchTerm(getContext());
        searchItem.setText(search_word);

        //when the search button is clicked
        search_button.setOnClickListener((vw)->{

            validateInput();
        });



        recyclerView.setHasFixedSize(true);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false);
        recyclerView.setLayoutManager(layoutManager);


        return view;
    }

    /**
     * This function checks if the search box is empty,
     * if empty we pop up a Toast as a message to the user
     */
    public void validateInput() {

        String userInput = searchItem.getText().toString();
        if (userInput.equals("")) {
            Toast.makeText(getContext(), R.string.msg_enter_to_search, Toast.LENGTH_LONG).show();
            searchItem.requestFocus(); //select the search item for user to enter input

        }else {

             Utilities.setSearchTerm(getContext(),userInput);
            if(Utilities.checkConnectivity(getContext())){
                Utilities.closeKeyboard(getContext(),search_button);
                getDataFromApiInBackground(userInput);
            }
            else{
                Snackbar.make(getView(), getResources().getString(R.string.msg_no_internet), Snackbar.LENGTH_LONG).show();
            }
        }
    }



    private void getDataFromApiInBackground(String searchTerm){
        progressDialog= Utilities.showProgressDialog(getContext(),progressDialog);

        Executor newThread = Executors.newSingleThreadExecutor();
        newThread.execute(()-> {

            try{
                URL url =  new URL(Constants.URL_GET_PHOTOS+searchTerm);
                HttpURLConnection urlConnection = (HttpURLConnection)url.openConnection();
                urlConnection.setRequestProperty (Constants.PEXELS_HEADER, Constants.PEXELS_API_KEY);
                if(urlConnection.getResponseCode()==HttpURLConnection.HTTP_OK){
                    InputStream in = new BufferedInputStream(urlConnection.getInputStream());
                    String text =(new BufferedReader(new InputStreamReader(in, StandardCharsets.UTF_8)))
                            .lines()
                            .collect(Collectors.joining("\n"));
                    if(text!=null && text!=""){
                        List<PhotoModel> photos= parseDate(text);
                        getActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Utilities.closeProgressDialog(progressDialog);
                                if(photos!=null &&photos.size()>0){
                                    recyclerView.setAdapter(new PhotosAdapter(photos,getActivity()));
                                }
                              else{
                                  Snackbar.make(getView(), getResources().getString(R.string.err_processing), Snackbar.LENGTH_LONG).show();
                              }
                            }
                        });
                    }
                }
            }
            catch (Exception e){
                Utilities.closeProgressDialog(progressDialog);
                Snackbar.make(getView(), getResources().getString(R.string.err_processing), Snackbar.LENGTH_LONG).show();
            }
        });
    }

    private List<PhotoModel> parseDate(String data){
        List<PhotoModel> list= new ArrayList<>();
        try{
            JSONObject obj1 = new JSONObject(data);
            JSONArray arr1 =obj1.getJSONArray(Constants.KEY_PHOTOS);
            for(int i=0; i<arr1.length(); i++){
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

                list.add(photo);
            }
        }
        catch (Exception e){ }
        return list;
    }
}
