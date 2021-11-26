package com.project.cst2335.Activities;

import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.project.cst2335.Fragments.PhotoDetailFragment;
import com.project.cst2335.Fragments.PhotoListFragment;
import com.project.cst2335.Models.PhotoModel;
import com.project.cst2335.R;

public class PexelsActivity extends AppCompatActivity {

    boolean isTablet;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.pexels_activit);
        Toolbar myToolbar = findViewById(R.id.toolbar);
        String activity = getResources().getString(R.string.activity_name);
        String author = getResources().getString(R.string.author_name);
        String version = getResources().getString(R.string.version);
        myToolbar.setTitle(activity + " - " + " - " + author + " - " + version);
        setSupportActionBar(myToolbar);
        isTablet = findViewById(R.id.detailsRoom) != null;


        PhotoListFragment chatFragment = new PhotoListFragment();
        FragmentManager fMgr = getSupportFragmentManager();
        FragmentTransaction tx = fMgr.beginTransaction();
        tx.add(R.id.fragmentRoom, chatFragment);
        tx.commit();
    }


    public void userClickMessage(PhotoModel photo, int position) {
        PhotoDetailFragment md = new PhotoDetailFragment(photo, position);
        if (isTablet) {
            getSupportFragmentManager().beginTransaction().replace(R.id.detailsRoom, md).addToBackStack("PhotoListFragment").commit();
        } else {
            getSupportFragmentManager().beginTransaction().replace(R.id.fragmentRoom, md).addToBackStack("PhotoListFragment").commit();
        }
    }
}


