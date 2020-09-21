package com.creativesourceapps.android.cupboard.ui;

import android.content.Context;
import android.os.Bundle;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.creativesourceapps.android.cupboard.R;
import com.creativesourceapps.android.cupboard.data.Recipe;

import butterknife.BindView;
import butterknife.ButterKnife;


public class DetailStepFragment extends Fragment {
    int position;
    @BindView(R.id.text_description) TextView textView;
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
        ButterKnife.bind(this, rootView);
        recipe = ((MainActivity)getActivity()).getRecipe();


        position = getArguments().getInt("position", 0);
        textView.setText(recipe.instructions.get(position));

        // Return the root view
        return rootView;
    }

}
