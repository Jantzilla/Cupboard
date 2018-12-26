package com.creativesourceapps.android.cupboard;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;
import java.util.ArrayList;

public class CookbookFragment extends Fragment {

    private GridView gridView;

    public CookbookFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_cookbook, container, false);
        gridView = view.findViewById(R.id.cookbook_grid_view);
        requestRecipeData();

        return view;
    }

    private void requestRecipeData() {

        final ArrayList<Recipe> recipes = new ArrayList<>();
        recipes.clear();


        RecipeListAdapter recipeAdapter = new RecipeListAdapter(getContext(), recipes);

        gridView.setAdapter(recipeAdapter);

        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                Recipe item_clicked = recipes.get(position);
                CupboardWidgetProvider.sendRefreshBroadcast(getContext(),item_clicked);

                Intent intent = new Intent(getContext(), RecipeActivity.class);
                intent.putExtra("parcel_data", item_clicked);
                startActivity(intent);
            }
        });

    }

}
