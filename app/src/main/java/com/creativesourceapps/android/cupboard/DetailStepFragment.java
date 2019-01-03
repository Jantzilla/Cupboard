package com.creativesourceapps.android.cupboard;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;


public class DetailStepFragment extends Fragment {
    int position;
    private static final String TAG = DetailStepFragment.class.getSimpleName();
    private TextView stepTextView, numberTextView;
    private TextView textView;
    private Recipe recipe;

    // Override onAttach to make sure that the container activity has implemented the callback
    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

    }

    // Mandatory empty constructor
    public DetailStepFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        final View rootView = inflater.inflate(R.layout.fragment_detail_step, container, false);
        textView = (TextView) rootView.findViewById(R.id.text_description);
        stepTextView = rootView.findViewById(R.id.list_item_step);
        numberTextView = rootView.findViewById(R.id.tv_step_number);
        recipe = ((MainActivity)getActivity()).getRecipe();


        position = getArguments().getInt("position", 0);
        numberTextView.setText(String.valueOf(position));
        textView.setText(recipe.instructions.get(position));
        stepTextView.setText(recipe.steps.get(position));

        // Return the root view
        return rootView;
    }

}
