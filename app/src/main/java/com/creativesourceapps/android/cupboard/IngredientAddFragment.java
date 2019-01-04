package com.creativesourceapps.android.cupboard;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public class IngredientAddFragment extends Fragment {

    public IngredientAddFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_ingredient_add, container, false);

        ((MainActivity)getActivity()).setScrimVisibility(true);

        return view;
    }

    @Override
    public void onPause() {
        super.onPause();
        ((MainActivity)getActivity()).setScrimVisibility(false);
    }
}
