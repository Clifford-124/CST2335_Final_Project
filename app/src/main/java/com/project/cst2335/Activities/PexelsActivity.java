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
/**
 *
 * Pexels activity responsible to displaying  list and detail fragments
 *
 * @author Seema Thapa Gurung
 * @version 1.0
 */
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
        isTablet = findViewById(R.id.framePhotoDetails) != null;


        PhotoListFragment chatFragment = new PhotoListFragment();
        FragmentManager fMgr = getSupportFragmentManager();
        FragmentTransaction tx = fMgr.beginTransaction();
        tx.add(R.id.framePhotosList, chatFragment);
        tx.commit();
    }

    /**
     * when user clicks on a photo from list
     *
     * @param photo photo that was clicked
     * @param offlineViewing if the user if viewing offline photos
     */
    public void photoClicked(PhotoModel photo, boolean offlineViewing) {
        // passing the photo to the detail fragment
        PhotoDetailFragment md = new PhotoDetailFragment(photo,offlineViewing);
        if (isTablet) { // if device is a tablet then display a different layout
            getSupportFragmentManager().beginTransaction().replace(R.id.framePhotoDetails, md).addToBackStack("PhotoListFragment").commit();
        } else {
            getSupportFragmentManager().beginTransaction().replace(R.id.framePhotosList, md).addToBackStack("PhotoListFragment").commit();
        }
    }
}


