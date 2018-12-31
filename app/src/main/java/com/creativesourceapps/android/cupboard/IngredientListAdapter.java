package com.creativesourceapps.android.cupboard;

import android.content.Context;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.TextView;

import java.util.ArrayList;

public class IngredientListAdapter extends BaseExpandableListAdapter {

    private Context context;
    private String expandableListTitle;
    private ArrayList<String> ingredient;
    private ArrayList<String> quantity;
    private ArrayList<String> unit;

    public IngredientListAdapter(Context context, String expandableListTitle, ArrayList<String> quantity,
                                 ArrayList<String> unit, ArrayList<String> ingredient) {
        this.context = context;
        this.expandableListTitle = expandableListTitle;
        this.ingredient = ingredient;
        this.quantity = quantity;
        this.unit = unit;
    }

    @Override
    public Object getChild(int listPosition, int expandedListPosition) {
        return this.ingredient
                .get(expandedListPosition);
    }

    @Override
    public long getChildId(int listPosition, int expandedListPosition) {
        return expandedListPosition;
    }

    @Override
    public View getChildView(int listPosition, final int expandedListPosition,
                             boolean isLastChild, View convertView, ViewGroup parent) {
        String ingredientListText = this.ingredient.get(expandedListPosition);
        String unitListText = this.unit.get(expandedListPosition);
        String quantityListText = String.valueOf(this.quantity.get(expandedListPosition));

        if (convertView == null) {
            LayoutInflater layoutInflater = (LayoutInflater) this.context
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = layoutInflater.inflate(R.layout.list_item, null);
        }
        TextView ingredientTextView = (TextView) convertView.findViewById(R.id.ingredientsListItem);
        TextView quantityTextView = (TextView) convertView.findViewById(R.id.quantityListItem);
        TextView unitTextView = (TextView) convertView.findViewById(R.id.unitListItem);
        ingredientTextView.setText(ingredientListText);
        quantityTextView.setText(quantityListText);
        unitTextView.setText(unitListText);
        return convertView;
    }

    @Override
    public int getChildrenCount(int listPosition) {
        return this.ingredient
                .size();
    }

    @Override
    public Object getGroup(int listPosition) {
        return this.expandableListTitle;
    }

    @Override
    public int getGroupCount() {
        return 1;
    }

    @Override
    public long getGroupId(int listPosition) {
        return listPosition;
    }

    @Override
    public View getGroupView(int listPosition, boolean isExpanded,
                             View convertView, ViewGroup parent) {
        String listTitle = (String) getGroup(listPosition);
        if (convertView == null) {
            LayoutInflater layoutInflater = (LayoutInflater) this.context.
                    getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = layoutInflater.inflate(R.layout.list_group, null);
        }
        TextView listTitleTextView = (TextView) convertView
                .findViewById(R.id.listTitle);
        listTitleTextView.setTypeface(null, Typeface.BOLD);
        listTitleTextView.setText(listTitle);
        return convertView;
    }

    @Override
    public boolean hasStableIds() {
        return false;
    }

    @Override
    public boolean isChildSelectable(int listPosition, int expandedListPosition) {
        return true;
    }
}
