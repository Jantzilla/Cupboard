package com.creativesourceapps.android.cupboard;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;

public class StepListAdapter extends ArrayAdapter<String> {

    /**
     * Constructor method
     * @param steps The list of recipes to display
     */
    public StepListAdapter(Context context, ArrayList<String> steps) {
        super(context,0,steps);
    }

    /**
     * Creates a new TextView for each item referenced by the adapter
     */
    public View getView(final int position, View convertView, ViewGroup parent) {
        String step = getItem(position);
        TextView textView;
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.list_step_layout, parent, false);
        }

        textView = convertView.findViewById(R.id.list_item_step);

        // Set the text resource and return the newly created TextView
        textView.setText(step);
        return textView;
    }

}
