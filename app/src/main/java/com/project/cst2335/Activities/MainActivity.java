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
import androidx.drawerlayout.widget.DrawerLayout;

import com.google.android.material.navigation.NavigationView;
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
        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        drawer = findViewById(R.id.drawer_layout);
        toggle = new ActionBarDrawerToggle(this,drawer,toolbar,R.string.navigation_drawer_open,R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();
        navigationView =findViewById(R.id.popout_menu);
        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                int id=item.getItemId();
                if (id == R.id.navpexelsProject){
                    // starting new activity when project is selected from the navigation drawer
                    Intent newIntent = new Intent(MainActivity.this, PexelsActivity.class);
                    startActivity(newIntent);
                   // drawer.closeDrawers();
                }
                return true;
            }
        });

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main_menu,menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()){
            case R.id.help:
                // showing help alert dialog
                Utilities.showAlertDialog(getResources().getString(R.string.msg_title_help),getResources().getString(R.string.msg_main_help), MainActivity.this);
                break;
            case R.id.pexelsProject:
                // starting new activity when project is selected from the roolbar icon
                Intent newIntent = new Intent(MainActivity.this, PexelsActivity.class);
                startActivity(newIntent);
                break;
        }
        return super.onOptionsItemSelected(item);
    }



}
