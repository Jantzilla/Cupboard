package com.creativesourceapps.android.cupboard;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

public class RecipeActivity extends AppCompatActivity implements StepListFragment.OnStepClickListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recipe);
        Recipe recipe = getIntent().getParcelableExtra("parcel_data");
        setTitle(recipe.title);

    }

    @Override
    public void onStepSelected(int position, Recipe recipe) {
        Bundle bundle = new Bundle();
        bundle.putInt("position",position);
        bundle.putParcelable("parcel_data", recipe);
        final Intent intent = new Intent(this, DetailStepActivity.class);
        intent.putExtras(bundle);
        startActivity(intent);
    }
}
