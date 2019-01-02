package com.creativesourceapps.android.cupboard;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public class IngredientFragment extends Fragment {
    RecyclerView recyclerView;
    private Recipe recipe;
    private IngredientListAdapter adapter;
    private GridLayoutManager gridLayoutManager;

    public IngredientFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_ingredient, container, false);

        recyclerView = view.findViewById(R.id.rv_ingredients);
        recipe = ((MainActivity)getActivity()).getRecipe();
        adapter = new IngredientListAdapter(getContext(), "Ingredients", recipe.quantity, recipe.unit, recipe.ingredients);

        gridLayoutManager = new GridLayoutManager(getContext(), 3);

        recyclerView.setLayoutManager(gridLayoutManager);
        recyclerView.setAdapter(adapter);

        return view;
    }

}
