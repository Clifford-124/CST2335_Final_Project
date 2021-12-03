package com.project.cst2335.Activities;

import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.project.cst2335.Fragments.PhotoDetailFragment;
import com.project.cst2335.Fragments.PhotoListFragment;
import com.project.cst2335.Models.PhotoModel;
import com.project.cst2335.R;
import com.project.cst2335.Utils.Utilities;

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
        setContentView(R.layout.pexels_activity);
        Toolbar myToolbar = findViewById(R.id.toolbar);
        String activity = getResources().getString(R.string.activity_name);
        String author = getResources().getString(R.string.author_name);
        String version = getResources().getString(R.string.version);
        myToolbar.setTitle(activity + " - " + author + " - " + version);
        setSupportActionBar(myToolbar);
        isTablet = findViewById(R.id.framePhotoDetails) != null;// this is null if on phone, true for tablet, false for phone


        PhotoListFragment photoListFragment = new PhotoListFragment();
        FragmentManager fMgr = getSupportFragmentManager();
        FragmentTransaction tx = fMgr.beginTransaction();
        tx.add(R.id.framePhotosList, photoListFragment);
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
            // addToBackStack -- so that we come back to the List Fragment when we click back button
            getSupportFragmentManager().beginTransaction().replace(R.id.framePhotoDetails, md).addToBackStack("PhotoListFragment").commit();
        } else {
            getSupportFragmentManager().beginTransaction().replace(R.id.framePhotosList, md).addToBackStack("PhotoListFragment").commit();
        }
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.pexels_menu,menu);
        return true;
    }
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()){
            case R.id.help:
                // showing help alert dialog
                Utilities.showAlertDialog(getResources().getString(R.string.msg_title_help),getResources().getString(R.string.msg_pexel_help), PexelsActivity.this);
                break;
        }
        return super.onOptionsItemSelected(item);
    }
}


