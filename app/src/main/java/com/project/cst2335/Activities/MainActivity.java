package com.project.cst2335.Activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import com.google.android.material.navigation.NavigationView;
import com.project.cst2335.Activities.CarbonActivity;
import com.project.cst2335.R;
import com.project.cst2335.Utils.Utilities;

/**
 *
 * Main activity responsible for loading navigation drawer and toolbar and the first activity of the project
 *
 * @author Seema Thapa Gurung
 * @version 1.0
 */
public class MainActivity extends AppCompatActivity {
    Toolbar toolbar;
    DrawerLayout drawer;
    ActionBarDrawerToggle toggle;
    NavigationView navigationView;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // setting all the controls
        toolbar = findViewById(R.id.toolbar);//find the toolbar
        setSupportActionBar(toolbar); // load the toolbar

        drawer = findViewById(R.id.drawer_layout);
        toggle = new ActionBarDrawerToggle(this,drawer,toolbar,R.string.navigation_drawer_open,R.string.navigation_drawer_close); //create Hamburger button
        drawer.addDrawerListener(toggle); //make the button popout
        toggle.syncState();

        navigationView =findViewById(R.id.popout_menu);

        //when user clicks on the popout menu
        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                int id=item.getItemId();
                if (id == R.id.navpexelsProject){
                    // starting new activity when project is selected from the navigation drawer
                    Intent newIntent = new Intent(MainActivity.this, PexelsActivity.class);
                    startActivity(newIntent);
                   // drawer.closeDrawers();
                } else if (id == R.id.carbonInterface){
                    Intent newIntent = new Intent(MainActivity.this, CarbonActivity.class);
                    startActivity(newIntent);
                    // drawer.closeDrawers();
                }

                drawer.closeDrawer(GravityCompat.START);//close the drawer
                return true;
            }
        });

    }

    /**
     * initialize the toolbar , by loading layout main_menu
     * @param menu Menu object
     * @return true
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main_menu,menu);//load layout file main_menu to menu object passed
        return true;
    }

    /**
     * This function responds when the user clicks on the MenuItem
     * @param item , the MenuItem object that is selected by user
     * @return
     */
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()){ //get the id of the item
            //check if the id matches with one of the id from menu/main_menu.xml
            case R.id.help:
                // showing help alert dialog
                Utilities.showAlertDialog(getResources().getString(R.string.msg_title_help),getResources().getString(R.string.msg_main_help), MainActivity.this);
                break;
            case R.id.pexelsProject:
                // starting Pexel activity when project is selected from the toolbar icon
                Intent newIntent = new Intent(MainActivity.this, PexelsActivity.class);
                startActivity(newIntent);
                break;
            case R.id.carbonInterfaceMenu:
                // starting Carbon activity when project is selected from the toolbar icon
                Intent carbonIntent = new Intent(MainActivity.this, CarbonActivity.class);
                startActivity(carbonIntent);
                break;
        }
        return super.onOptionsItemSelected(item);
    }



}
