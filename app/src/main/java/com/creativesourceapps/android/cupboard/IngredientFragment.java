package com.creativesourceapps.android.cupboard;

import android.os.Bundle;
import androidx.annotation.NonNull;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import androidx.transition.Slide;
import androidx.transition.Transition;
import androidx.transition.TransitionManager;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

public class IngredientFragment extends Fragment implements IngredientListAdapter.ItemClickListener {
    @BindView(R.id.rv_ingredients) RecyclerView recyclerView;
    private Recipe recipe;
    @BindView(R.id.fab_close) private FloatingActionButton fab;
    private IngredientListAdapter adapter;
    private GridLayoutManager gridLayoutManager;
    private ArrayList<Ingredient> ingredients;
    private IngredientAddFragment fragment;
    private FragmentManager fragmentManager;
    private Transition transition;
    @BindView(R.id.btn_use) private Button useButton;
    private boolean isUsed;

    public IngredientFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, final ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        final View view = inflater.inflate(R.layout.fragment_ingredient, container, false);
        ButterKnife.bind(this, view);

        ingredients = new ArrayList<>();
        fragmentManager = getActivity().getSupportFragmentManager();
        transition = new Slide(Gravity.START);

        if(getArguments() == null)
            fab.hide();

        useButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                for (Integer index : IngredientAddFragment.useAllIngredients(getContext(), ingredients)) {
                    ingredients.get(index).used = true;
                }

                isUsed = true;
                TransitionManager.beginDelayedTransition(container, transition);
                useButton.setVisibility(View.GONE);
                adapter.notifyDataSetChanged();
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

        isUsed = recipe.ingredientsUsed == 1;

        for(int i = 0; i < recipe.ingredients.size(); i++) {
            Ingredient ingredient = new Ingredient();
            ingredient.name = recipe.ingredients.get(i);
            ingredient.category = getString(R.string.ingredients);
            ingredient.unit = recipe.unit.get(i);
            ingredient.quantity = recipe.quantity.get(i);
            ingredient.used = recipe.used.get(i);
            ingredient.available = recipe.available.get(i);
            ingredients.add(ingredient);
        }
        adapter = new IngredientListAdapter(getContext(), true, ingredients, IngredientFragment.this);

        gridLayoutManager = new GridLayoutManager(getContext(), getResources().getInteger(R.integer.cupboard_column_count));

        recyclerView.setLayoutManager(gridLayoutManager);
        recyclerView.setAdapter(adapter);

        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                transition.addTarget(useButton);

                if(!isUsed) {

                    if (gridLayoutManager.findLastVisibleItemPosition() == ingredients.size() - 1 && MainActivity.availableCount != 0) {
                        ((MainActivity) getActivity()).setScrimVisibility(true);
                        TransitionManager.beginDelayedTransition(recyclerView, transition);
                        useButton.setVisibility(View.VISIBLE);
                    } else {
                        ((MainActivity) getActivity()).setScrimVisibility(false);
                        TransitionManager.beginDelayedTransition(recyclerView, transition);
                        useButton.setVisibility(View.GONE);
                    }
                }
            }
        });

        return view;
    }

    @Override
    public void onItemClickListener(int index, String name, String quantity, String unit, String category, boolean availability) {
        Bundle bundle = new Bundle();
        bundle.putString("type", "detail");
        bundle.putBoolean("availability", availability);
        bundle.putBoolean("used", ingredients.get(index).used);
        bundle.putString("name", name);
        bundle.putString("quantity", quantity);
        bundle.putString("unit", unit);
        bundle.putInt("index", index);

        fragment = new IngredientAddFragment();
        MainActivity.restoreFragment = fragment;
        fragment.setArguments(bundle);
        fragmentManager = getActivity().getSupportFragmentManager();
        fragmentManager.beginTransaction().addToBackStack(null)
                .replace(R.id.fl_fragment, fragment).commit();
    }
}
