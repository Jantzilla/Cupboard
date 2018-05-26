package com.creativesourceapps.android.cupboard;

import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

public class DetailStepActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail_step);

        Recipe recipe = getIntent().getParcelableExtra("parcel_data");
        setTitle(recipe.title);

        // Only create new fragments when there is no previously saved state
        if(savedInstanceState == null) {

            DetailStepFragment detailStepFragment = new DetailStepFragment();

            // Add the fragment to its container using a FragmentManager and a Transaction
            FragmentManager fragmentManager = getSupportFragmentManager();

            fragmentManager.beginTransaction()
                    .add(R.id.detail_container, detailStepFragment)
                    .commit();

        }

    }
}
