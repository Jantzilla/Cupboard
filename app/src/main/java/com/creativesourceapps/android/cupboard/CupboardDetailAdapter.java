package com.creativesourceapps.android.cupboard;

import android.app.Dialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
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
    private String category;
    private String[] projection = new String[]{CupboardContract.Ingredients.COLUMN_NAME};

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
        Dialog dialog;
        String selectedUnit, selectedCategory;
        SQLiteDatabase db;
        CupboardDbHelper dbHelper = new CupboardDbHelper(context);
        String selection = CupboardContract.Ingredients.COLUMN_NAME + " Like ?";
        String[] selectionArgs;
        Cursor cursor;
        ContentValues values = new ContentValues();
        private boolean savedIngredient;

        public DetailViewHolder(@NonNull View itemView) {
            super(itemView);

            dialog = new Dialog(context);
            dialog.setContentView(R.layout.dialog_ingredient_edit);

            dialogIngredientEditText = dialog.findViewById(R.id.tv_ingredient);
            dialogQuantityEditText = dialog.findViewById(R.id.et_quantity);

            dialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
                @Override
                public void onCancel(DialogInterface dialog) {
                    dialogIngredientEditText.setError(null);
                    dialogQuantityEditText.setError(null);
                }
            });

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

            db = dbHelper.getWritableDatabase();

            saveButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(dialogIngredientEditText.getText().toString().isEmpty())
                        dialogIngredientEditText.setError("Please enter a name.");
                    if(dialogQuantityEditText.getText().toString().isEmpty())
                        dialogQuantityEditText.setError("Please enter a quantity.");
                    else {
                        cursor = db.query(
                                CupboardContract.Ingredients.TABLE_NAME,
                                projection,
                                null,
                                null,
                                null,
                                null,
                                null
                        );

                        while(cursor.moveToNext()) {
                            if(cursor.getString(cursor.getColumnIndex(CupboardContract.Ingredients.COLUMN_NAME))
                                    .equals(dialogIngredientEditText.getText().toString())) {
                                dialog.cancel();
                                savedIngredient = true;
                            }
                        }

                        if(!savedIngredient) {
                            values.put(CupboardContract.Ingredients.COLUMN_NAME, dialogIngredientEditText.getText().toString());
                            values.put(CupboardContract.Ingredients.COLUMN_UNIT, selectedUnit);
                            values.put(CupboardContract.Ingredients.COLUMN_QUANTITY, dialogQuantityEditText.getText().toString());
                            values.put(CupboardContract.Ingredients.COLUMN_CATEGORY, selectedCategory);

                            db.update(CupboardContract.Ingredients.TABLE_NAME,
                                    values,
                                    selection,
                                    selectionArgs);

                            ingredientsList.get(getAdapterPosition()).name = dialogIngredientEditText.getText().toString();
                            ingredientsList.get(getAdapterPosition()).quantity = dialogQuantityEditText.getText().toString();
                            ingredientsList.get(getAdapterPosition()).unit = selectedUnit;
                            ingredientsList.get(getAdapterPosition()).category = selectedCategory;

                            dialog.cancel();
                            notifyItemChanged(getAdapterPosition());
                        }
                        savedIngredient = false;
                    }
                }
            });

            deleteButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    db.delete(CupboardContract.Ingredients.TABLE_NAME, selection, selectionArgs);
                    ingredientsList.remove(getAdapterPosition());
                    dialog.cancel();
                    notifyItemRemoved(getAdapterPosition());
                }
            });

            ingredientTextView = itemView.findViewById(R.id.tv_ingredient);
            quantityTextView = itemView.findViewById(R.id.et_quantity);
            unitTextView = itemView.findViewById(R.id.tv_unit);

            unitSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                    selectedUnit = String.valueOf(parent.getItemAtPosition(position));
                }

                @Override
                public void onNothingSelected(AdapterView<?> parent) {

                }
            });

            categorySpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                    selectedCategory = String.valueOf(parent.getItemAtPosition(position));
                }

                @Override
                public void onNothingSelected(AdapterView<?> parent) {

                }
            });

            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            selectionArgs = new String[]{ingredientsList.get(getAdapterPosition()).name};

            if(!category.equals("All Ingredients"))
                categorySpinner.setVisibility(View.INVISIBLE);

            dialogIngredientEditText.setText(ingredientTextView.getText());
            dialogQuantityEditText.setText(quantityTextView.getText());
            unitSpinner.setSelection(unitSpinnerAdapter.getPosition(ingredientsList.get(getAdapterPosition()).unit));
            categorySpinner.setSelection(categorySpinnerAdapter.getPosition(ingredientsList.get(getAdapterPosition()).category));
            dialog.show();
        }
    }
}
