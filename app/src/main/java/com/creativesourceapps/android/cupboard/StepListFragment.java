package com.creativesourceapps.android.cupboard;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ExpandableListView;

public class StepListFragment extends Fragment {

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
    }


    // Mandatory empty constructor
    public StepListFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        final Recipe recipe = getActivity().getIntent().getParcelableExtra("parcel_data");

        final View rootView = inflater.inflate(R.layout.fragment_step_list, container, false);

        // Get a reference to the ListView in the fragment_step_list xml layout file
        RecyclerView recyclerView = (RecyclerView) rootView.findViewById(R.id.steps_list_view);
        ExpandableListView expandableListView = (ExpandableListView) rootView.findViewById(R.id.recipe_list_view);

        // Create the adapter
        // This adapter takes in the context and an ArrayList of ALL the image resources to display
        StepListAdapter mAdapter = new StepListAdapter(getContext(), recipe.steps, recipe);
        IngredientListAdapter ingredientListAdapter = new IngredientListAdapter(getContext(), "Ingredients", recipe.ingredients);

        // Set the adapter on the ListView
        recyclerView.setAdapter(mAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        expandableListView.setAdapter(ingredientListAdapter);

        // Return the root view
        return rootView;
    }
}
