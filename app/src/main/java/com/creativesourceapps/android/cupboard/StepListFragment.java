package com.creativesourceapps.android.cupboard;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ExpandableListView;
import android.widget.GridView;
import android.widget.ListView;

public class StepListFragment extends Fragment {
    // Define a new interface OnStepClickListener that triggers a callback in the host activity
    OnStepClickListener mCallback;

    // OnStepClickListener interface, calls a method in the host activity named OnStepSelected
    public interface OnStepClickListener {
        void onStepSelected(int position, Recipe recipe);
    }

    // Override onAttach to make sure that the container activity has implemented the callback
    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        // This makes sure that the host activity has implemented the callback interface
        // If not, it throws an exception
        try {
            mCallback = (OnStepClickListener) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString()
                    + " must implement OnStepClickListener");
        }
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
        ListView listView = (ListView) rootView.findViewById(R.id.steps_list_view);
        ExpandableListView expandableListView = (ExpandableListView) rootView.findViewById(R.id.recipe_list_view);

        // Create the adapter
        // This adapter takes in the context and an ArrayList of ALL the image resources to display
        StepListAdapter mAdapter = new StepListAdapter(getContext(), recipe.steps);
        IngredientListAdapter ingredientListAdapter = new IngredientListAdapter(getContext(), "Ingredients", null);

        // Set the adapter on the ListView
        listView.setAdapter(mAdapter);
        expandableListView.setAdapter(ingredientListAdapter);

        // Set a click listener on the listView and trigger the callback onStepSelected when an item is clicked
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                // Trigger the callback method and pass in the position that was clicked
                mCallback.onStepSelected(position, recipe);
            }
        });

        // Return the root view
        return rootView;
    }
}
