package com.project.cst2335;

import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

public class CovidMainActivity extends AppCompatActivity {

    //EditText covid_editText;
    TextView covidTracker;


    Covid_Dates_Fragment covidFragment;


    /**
     * @param menu
     * @return
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.covid_activity_actions, menu);
        return true;
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.covid_empty_layout);

        Toolbar myToolbar = findViewById(R.id.covid_toolbar);
        setSupportActionBar(myToolbar);//causes onCreateOptionsMenu() to be called
        covidTracker = findViewById(R.id.textView2);

        covidFragment = new Covid_Dates_Fragment();
        FragmentManager fMgr = getSupportFragmentManager(); // return fragment object

        // FragmentTransaction
        FragmentTransaction tran = fMgr.beginTransaction();   // return transaction object
        tran.add(R.id.fragmentSpace, covidFragment);

        // loading the fragment into FrameLayout
        tran.commit();

    }

    /**
     *
     * @param wordMessage
     * @param position
     */
    public void userClickedMessage(Covid_Dates_Fragment.CovidInformation wordMessage, int position) {
        Covid_Details_Fragment mdFragment = new Covid_Details_Fragment(wordMessage, position);

        getSupportFragmentManager().beginTransaction().replace(R.id.fragmentSpace, mdFragment).commit();
    }

    public void notifyMessageSaved(Covid_Dates_Fragment.CovidInformation selectedDateInformation, int chosenPosition) {
        covidFragment.notifyMessageSaved(selectedDateInformation, chosenPosition);
    }
}