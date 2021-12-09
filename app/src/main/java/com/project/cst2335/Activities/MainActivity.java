package com.project.cst2335.Activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import com.google.android.material.navigation.NavigationView;
import com.project.cst2335.Activities.CarbonActivity;
import com.project.cst2335.CovidMainActivity;
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
                Utilities.startActivity(MainActivity.this,item.getItemId());
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
        Utilities.startActivity(MainActivity.this,item.getItemId());
        return super.onOptionsItemSelected(item);
    }
}
