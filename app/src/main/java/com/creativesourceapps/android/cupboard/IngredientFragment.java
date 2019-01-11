package com.creativesourceapps.android.cupboard;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;

public class IngredientFragment extends Fragment {
    RecyclerView recyclerView;
    private Recipe recipe;
    private FloatingActionButton fab;
    private IngredientListAdapter adapter;
    private GridLayoutManager gridLayoutManager;
    private ArrayList<Ingredient> ingredients;

    public IngredientFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_ingredient, container, false);

        recyclerView = view.findViewById(R.id.rv_ingredients);
        fab = view.findViewById(R.id.fab_close);
        ingredients = new ArrayList<>();

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getActivity().onBackPressed();
            }
        });

        recipe = ((MainActivity)getActivity()).getRecipe();

        for(int i = 0; i < recipe.ingredients.size(); i++) {
            Ingredient ingredient = new Ingredient();
            ingredient.name = recipe.ingredients.get(i);
            ingredient.category = "Ingredients";
            ingredient.unit = recipe.unit.get(i);
            ingredient.quantity = recipe.quantity.get(i);
            ingredients.add(ingredient);
        }
        adapter = new IngredientListAdapter(getContext(), "Ingredients", ingredients);

        gridLayoutManager = new GridLayoutManager(getContext(), 2);

        recyclerView.setLayoutManager(gridLayoutManager);
        recyclerView.setAdapter(adapter);

        return view;
    }

}
