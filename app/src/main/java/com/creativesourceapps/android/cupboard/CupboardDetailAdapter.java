package com.creativesourceapps.android.cupboard;

import android.app.Dialog;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import java.util.ArrayList;

public class CupboardDetailAdapter extends RecyclerView.Adapter<CupboardDetailAdapter.DetailViewHolder> {
    private ArrayList<Ingredient> ingredientsList = new ArrayList<>();

    private Context context;
    private LayoutInflater layoutInflator;
    private Dialog dialog;
    private String unit, category;

    public CupboardDetailAdapter(String category, ArrayList<Ingredient> ingredientsList) {
        this.ingredientsList.addAll(ingredientsList);
        this.category = category;
    }

    @NonNull
    @Override
    public DetailViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        context = viewGroup.getContext();
        layoutInflator = LayoutInflater.from(context);
        View view = layoutInflator.inflate(R.layout.list_item_cupboard_detail, viewGroup, false);
        DetailViewHolder detailViewHolder = new DetailViewHolder(view);

        return detailViewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull DetailViewHolder detailViewHolder, int i) {
        detailViewHolder.ingredientTextView.setText(ingredientsList.get(i).name);
        detailViewHolder.quantityTextView.setText(String.valueOf(ingredientsList.get(i).quantity));
        detailViewHolder.unitTextView.setText(ingredientsList.get(i).unit);
    }

    @Override
    public int getItemCount() {
        return ingredientsList.size();
    }

    public void addIngredient(Ingredient ingredient) {
        ingredientsList.add(ingredient);
    }

    class DetailViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private Button saveButton, deleteButton;
        ArrayAdapter<CharSequence> unitSpinnerAdapter, categorySpinnerAdapter;
        TextView ingredientTextView, quantityTextView, unitTextView;
        EditText dialogIngredientEditText, dialogQuantityEditText;
        Spinner categorySpinner, unitSpinner;

        public DetailViewHolder(@NonNull View itemView) {
            super(itemView);

            dialog = new Dialog(context);
            dialog.setContentView(R.layout.dialog_ingredient_edit);
            dialogIngredientEditText = dialog.findViewById(R.id.tv_ingredient);
            dialogQuantityEditText = dialog.findViewById(R.id.tv_quantity);
            saveButton = dialog.findViewById(R.id.btn_save);
            deleteButton = dialog.findViewById(R.id.btn_delete);

            categorySpinner = dialog.findViewById(R.id.spin_group);
            unitSpinner = dialog.findViewById(R.id.spin_unit);

            unitSpinnerAdapter = ArrayAdapter.createFromResource(context,R.array.units_array, R.layout.dropdown_item);
            unitSpinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            unitSpinner.setAdapter(unitSpinnerAdapter);

            categorySpinnerAdapter = ArrayAdapter.createFromResource(context,R.array.categories_array, R.layout.dropdown_item);
            categorySpinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            categorySpinner.setAdapter(categorySpinnerAdapter);

            saveButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    ingredientsList.get(getAdapterPosition()).name = dialogIngredientEditText.getText().toString();
                    ingredientsList.get(getAdapterPosition()).quantity = Integer.valueOf(dialogQuantityEditText.getText().toString());
                    ingredientsList.get(getAdapterPosition()).unit = unit;
                    dialog.cancel();
                    notifyItemChanged(getAdapterPosition());
                }
            });

            deleteButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    ingredientsList.remove(getAdapterPosition());
                    dialog.cancel();
                    notifyItemRemoved(getAdapterPosition());
                }
            });

            ingredientTextView = itemView.findViewById(R.id.tv_ingredient);
            quantityTextView = itemView.findViewById(R.id.tv_quantity);
            unitTextView = itemView.findViewById(R.id.tv_unit);

            unitSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                    unit = String.valueOf(parent.getItemAtPosition(position));
                }

                @Override
                public void onNothingSelected(AdapterView<?> parent) {

                }
            });

            categorySpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                }

                @Override
                public void onNothingSelected(AdapterView<?> parent) {

                }
            });

            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {

            if(!category.equals("All Ingredients"))
                categorySpinner.setVisibility(View.INVISIBLE);
            else {
                categorySpinner.setSelection(categorySpinnerAdapter.getPosition(ingredientsList.get(getAdapterPosition()).category));
            }

            dialogIngredientEditText.setText(ingredientTextView.getText());
            dialogQuantityEditText.setText(quantityTextView.getText());
            unit = ingredientsList.get(getAdapterPosition()).unit;
            unitSpinner.setSelection(unitSpinnerAdapter.getPosition(ingredientsList.get(getAdapterPosition()).unit));
            dialog.show();
        }
    }
}
