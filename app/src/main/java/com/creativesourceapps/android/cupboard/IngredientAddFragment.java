package com.creativesourceapps.android.cupboard;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;


public class IngredientAddFragment extends Fragment {
    FloatingActionButton fab, addFab;
    private SQLiteDatabase db;
    private CupboardDbHelper dbHelper;
    private Cursor cursor;
    private EditText ingredientEditText, quantityEditText;
    private String selectedUnit;
    private boolean savedIngredient;
    private String selectedCategory;
    private String[] projection;

    public IngredientAddFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_ingredient_add, container, false);

        fab = view.findViewById(R.id.fab_back);
        addFab = view.findViewById(R.id.fab_add);

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getActivity().onBackPressed();
            }
        });

        ((MainActivity)getActivity()).setScrimVisibility(true);

        ingredientEditText = view.findViewById(R.id.tv_ingredient);
        quantityEditText = view.findViewById(R.id.tv_quantity);
        dbHelper = new CupboardDbHelper(getContext());

        selectedUnit = "g";              //Reset default ingredient selectedUnit

        addFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(ingredientEditText.getText().toString().isEmpty())
                    ingredientEditText.setError("Please enter a name.");
                if(quantityEditText.getText().toString().isEmpty())
                    quantityEditText.setError("Please enter a quantity.");
                else {

                    Ingredient ingredient = new Ingredient();
                    ingredient.name = ingredientEditText.getText().toString();
                    ingredient.quantity = Integer.valueOf(quantityEditText.getText().toString());
                    ingredient.unit = selectedUnit;
                    ingredient.category = selectedCategory;

                    db = dbHelper.getWritableDatabase();

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
                                .equals(ingredient.name)) {
                            ingredientEditText.setError("Ingredient already exists.");
                            savedIngredient = true;
                        }
                    }

                    if(!savedIngredient) {
                        ContentValues values = new ContentValues();
                        values.put(CupboardContract.Ingredients.COLUMN_NAME, ingredient.name);
                        values.put(CupboardContract.Ingredients.COLUMN_QUANTITY, ingredient.quantity);
                        values.put(CupboardContract.Ingredients.COLUMN_UNIT, ingredient.unit);
                        values.put(CupboardContract.Ingredients.COLUMN_CATEGORY, ingredient.category);
                        db.insert(CupboardContract.Ingredients.TABLE_NAME, null, values);

                    }

                    savedIngredient = false;

                }
            }
        });

        return view;
    }

    @Override
    public void onPause() {
        super.onPause();
        ((MainActivity)getActivity()).setScrimVisibility(false);
    }
}
