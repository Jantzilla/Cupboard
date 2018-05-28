package com.creativesourceapps.android.cupboard;

import android.content.Intent;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

public class RecipeActivity extends AppCompatActivity implements StepListFragment.OnStepClickListener {
    Boolean mTwoPane;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recipe);
        Recipe recipe = getIntent().getParcelableExtra("parcel_data");
        setTitle(recipe.title);

        if(findViewById(R.id.cupboard_linear_layout) != null) {
            // This LinearLayout will only initially exist in the two-pane tablet case
            mTwoPane = true;

            if(savedInstanceState == null) {
                // In two-pane mode
                FragmentManager fragmentManager = getSupportFragmentManager();

                DetailStepFragment detailStepFragment = new DetailStepFragment();
                // Add the fragment to its container using a transaction
                fragmentManager.beginTransaction()
                        .add(R.id.detail_container, detailStepFragment)
                        .commit();
            }
        } else {
            // We're in single-pane mode and displaying fragments on a phone in separate activities
            mTwoPane = false;
        }
    }

    @Override
    public void onStepSelected(int position, Recipe recipe) {
        Bundle bundle = new Bundle();
        bundle.putInt("position",position);
        bundle.putParcelable("parcel_data", recipe);

        if (mTwoPane) {
            // Create two=pane interaction

            DetailStepFragment newFragment = new DetailStepFragment();
            newFragment.setArguments(bundle);

            FragmentManager fragmentManager = getSupportFragmentManager();

            // Add the fragment to its container using a transaction
            fragmentManager.beginTransaction()
                    .replace(R.id.detail_container, newFragment)
                    .commit();

        } else {

            final Intent intent = new Intent(this, DetailStepActivity.class);
            intent.putExtras(bundle);
            startActivity(intent);
        }
            }

}
