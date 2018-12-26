package com.creativesourceapps.android.cupboard;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;

public class RecipeListAdapter extends ArrayAdapter<Recipe> {

    /**
     * Constructor method
     * @param recipes The list of recipes to display
     */
    public RecipeListAdapter(Context context, ArrayList<Recipe> recipes) {
        super(context,0,recipes);
    }

    /**
     * Creates a new TextView for each item referenced by the adapter
     */
    public View getView(final int position, View convertView, ViewGroup parent) {
        Recipe recipes = getItem(position);
        TextView textView;
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.grid_recipe_layout, parent, false);
        }

        textView = convertView.findViewById(R.id.grid_item_recipe);

        // Set the text resource and return the newly created TextView
        textView.setText(recipes.title);
        return convertView;
    }

}
