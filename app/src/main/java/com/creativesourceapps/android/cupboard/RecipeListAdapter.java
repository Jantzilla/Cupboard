package com.creativesourceapps.android.cupboard;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.List;

public class RecipeListAdapter extends BaseAdapter {
    // Keeps track of the context and list of recipes to display
    private Context mContext;
    private List<String> mRecipes;

    /**
     * Constructor method
     * @param recipes The list of recipes to display
     */
    public RecipeListAdapter(Context context, List<String> recipes) {
        mContext = context;
        mRecipes = recipes;
    }

    /**
     * Returns the number of items the adapter will display
     */
    @Override
    public int getCount() {
        return mRecipes.size();
    }

    @Override
    public Object getItem(int i) {
        return null;
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }

    /**
     * Creates a new TextView for each item referenced by the adapter
     */
    public View getView(final int position, View convertView, ViewGroup parent) {
        TextView textView;
        if (convertView == null) {
            // If the view is not recycled, this creates a new TextView to hold a string
            textView = new TextView(mContext);
            // Define the layout parameters
            textView.setPadding(8, 8, 8, 8);
        } else {
            textView = (TextView) convertView;
        }

        // Set the text resource and return the newly created TextView
        textView.setText(mRecipes.get(position));
        return textView;
    }

}
