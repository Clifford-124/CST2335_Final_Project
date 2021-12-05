package com.project.cst2335.Activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;

import com.project.cst2335.Fragments.CarModelDetailFragment;
import com.project.cst2335.Fragments.WordDetailFragment;
import com.project.cst2335.Models.OwlWord;
import com.project.cst2335.R;
import com.project.cst2335.Utils.Constants;

public class OwlDetailActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_owl_detail);

        //Setting Toolbar
        Toolbar myToolbar = findViewById(R.id.toolbar_word_detail);

        String activity = getResources().getString(R.string.owl_bot_activity_main);
        String author = getResources().getString(R.string.owl_bot_author_name);
        String version = getResources().getString(R.string.owl_bot_version);

        myToolbar.setTitle(activity);
        myToolbar.setSubtitle(author + " - " + version);

        setSupportActionBar(myToolbar);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }
        //Fetch Data
        OwlWord word = (OwlWord) getIntent().getSerializableExtra("word");
        String display = getIntent().getStringExtra("display");

        //Load Fragment and pass data
        WordDetailFragment detailFragment =
                WordDetailFragment.newInstance(word,display);

        FragmentManager fMgr = getSupportFragmentManager();
        FragmentTransaction tx = fMgr.beginTransaction();
        tx.add(R.id.wordDetail, detailFragment);
        tx.commit();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // handle arrow click here
        if (item.getItemId() == android.R.id.home) {
            finish(); // close this activity and return to preview activity (if there is any)
        }

        return super.onOptionsItemSelected(item);
    }
}