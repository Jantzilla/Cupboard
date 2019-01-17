package com.creativesourceapps.android.cupboard;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.transition.Slide;
import android.support.transition.Transition;
import android.support.transition.TransitionManager;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import java.util.ArrayList;

public class IngredientFragment extends Fragment implements IngredientListAdapter.ItemClickListener {
    RecyclerView recyclerView;
    private Recipe recipe;
    private FloatingActionButton fab;
    private IngredientListAdapter adapter;
    private GridLayoutManager gridLayoutManager;
    private ArrayList<Ingredient> ingredients;
    private IngredientAddFragment fragment;
    private FragmentManager fragmentManager;
    private Transition transition;
    private Button useButton;

    public IngredientFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, final ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        final View view = inflater.inflate(R.layout.fragment_ingredient, container, false);

        recyclerView = view.findViewById(R.id.rv_ingredients);
        fab = view.findViewById(R.id.fab_close);
        useButton = view.findViewById(R.id.btn_use);
        ingredients = new ArrayList<>();
        fragmentManager = getActivity().getSupportFragmentManager();
        transition = new Slide(Gravity.START);

        useButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                IngredientAddFragment.useAllIngredients(getContext(), ingredients);
            }
        });

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((MainActivity)getActivity()).setScrimVisibility(false);
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
        adapter = new IngredientListAdapter(getContext(), true, ingredients, IngredientFragment.this);

        gridLayoutManager = new GridLayoutManager(getContext(), 2);

        recyclerView.setLayoutManager(gridLayoutManager);
        recyclerView.setAdapter(adapter);

        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                transition.addTarget(useButton);

                if(gridLayoutManager.findLastVisibleItemPosition() == ingredients.size() - 1) {
                    ((MainActivity)getActivity()).setScrimVisibility(true);
                    TransitionManager.beginDelayedTransition(container, transition);
                    useButton.setVisibility(View.VISIBLE);
                } else {
                    ((MainActivity)getActivity()).setScrimVisibility(false);
                    TransitionManager.beginDelayedTransition(container, transition);
                    useButton.setVisibility(View.GONE);
                }
            }
        });

        return view;
    }

    @Override
    public void onItemClickListener(String name, String quantity, String unit, boolean availability) {
        Bundle bundle = new Bundle();
        bundle.putString("type", "detail");
        bundle.putBoolean("availability", availability);
        bundle.putString("name", name);
        bundle.putString("quantity", quantity);
        bundle.putString("unit", unit);

        fragment = new IngredientAddFragment();
        fragment.setArguments(bundle);
        fragmentManager = getActivity().getSupportFragmentManager();
        fragmentManager.beginTransaction().addToBackStack(null)
                .replace(R.id.fl_fragment, fragment).commit();
    }
}
