package com.creativesourceapps.android.cupboard;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.TextWatcher;
import android.text.style.ForegroundColorSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;


public class IngredientAddFragment extends Fragment {
    FloatingActionButton fab, addFab;
    private SQLiteDatabase db;
    private CupboardDbHelper dbHelper;
    private Cursor cursor;
    private EditText ingredientEditText, hintEditText, quantityEditText;
    private TextView unitTextView;
    private ImageView ingredientImageView;
    private String selectedUnit, baseImageUrl;
    private boolean savedIngredient, availableIngredient;
    private String selection, selectedCategory;
    private String[] projection, selectionArgs;
    private int entryEnd;

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

        baseImageUrl = "https://www.themealdb.com/images/ingredients/";

        ingredientEditText = view.findViewById(R.id.et_title);
        hintEditText = view.findViewById(R.id.tv_hint);
        quantityEditText = view.findViewById(R.id.tv_quantity);
        unitTextView = view.findViewById(R.id.tv_unit);
        ingredientImageView = view.findViewById(R.id.iv_ingredient);

        if(getArguments() != null) {
            String name = getArguments().getString("name");
            String quantity = getArguments().getString("quantity");
            String category = getArguments().getString("category");
        }

        Glide.with(getContext()).load(baseImageUrl + "Allspice" + ".png").into(ingredientImageView);

        dbHelper = new CupboardDbHelper(getContext());

        db = dbHelper.getWritableDatabase();

        selectedUnit = "g";              //Reset default ingredient selectedUnit
        savedIngredient = false;
        availableIngredient = true;

        ingredientEditText.requestFocus();

        ingredientEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                entryEnd = count;

                Ingredient ingredient = searchIngredients(CupboardContract.AllIngredients.TABLE_NAME, String.valueOf(s));

                Spannable wordToSpan = new SpannableString(ingredient.name);  //TODO: ADD REAL DATA

                if(wordToSpan.length() > 0) {
                    wordToSpan.setSpan(new ForegroundColorSpan(Color.GRAY), entryEnd, wordToSpan.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                    hintEditText.setText(wordToSpan);
                    Glide.with(getContext()).load(baseImageUrl + wordToSpan + ".png").into(ingredientImageView);
                    unitTextView.setText(ingredient.unit);
                } if(count == 0 || wordToSpan.length() == 0)
                    hintEditText.setText("");

            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

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
                    ingredient.quantity = quantityEditText.getText().toString();
                    ingredient.unit = selectedUnit;
                    ingredient.category = selectedCategory;

                    if(!searchIngredients(CupboardContract.AllIngredients.TABLE_NAME, ingredient.name).name.equals(ingredient.name)) {
                        ingredientEditText.setError("Invalid ingredient.");
                        availableIngredient = false;
                    }

                    if(searchIngredients(CupboardContract.Ingredients.TABLE_NAME, ingredient.name).name.equals(ingredient.name)) {
                        ingredientEditText.setError("Ingredient already exists.");
                        savedIngredient = true;
                    }

                    if(!savedIngredient && availableIngredient) {
                        ContentValues values = new ContentValues();
                        values.put(CupboardContract.Ingredients.COLUMN_NAME, ingredient.name);
                        values.put(CupboardContract.Ingredients.COLUMN_QUANTITY, ingredient.quantity);
                        values.put(CupboardContract.Ingredients.COLUMN_UNIT, ingredient.unit);
                        values.put(CupboardContract.Ingredients.COLUMN_CATEGORY, ingredient.category);
                        db.insert(CupboardContract.Ingredients.TABLE_NAME, null, values);

                        ingredientEditText.setText("");
                        hintEditText.setText("");
                        quantityEditText.setText("");

                        Toast.makeText(getContext(), "New Ingredient Added!", Toast.LENGTH_LONG).show();
                    }

                    savedIngredient = false;
                    availableIngredient = true;

                }
            }
        });

        return view;
    }

    private Ingredient searchIngredients(String table, String query) {
        projection = new String[] {CupboardContract.AllIngredients.COLUMN_NAME, CupboardContract.AllIngredients.COLUMN_UNIT};
        selection = CupboardContract.AllIngredients.COLUMN_NAME + " LIKE ?";
        selectionArgs = new String[] {query + "%"};
        String tableName = table;
        Ingredient ingredient = new Ingredient();
        ingredient.name = "";
        cursor = db.query(
                tableName,
                projection,
                selection,
                selectionArgs,
                null,
                null,
                null
        );

        if(cursor.moveToFirst()) {
            ingredient.name = cursor.getString(cursor.getColumnIndex(CupboardContract.AllIngredients.COLUMN_NAME));
            ingredient.unit = cursor.getString(cursor.getColumnIndex(CupboardContract.AllIngredients.COLUMN_UNIT));
        }

        return ingredient;
    }

    @Override
    public void onPause() {
        super.onPause();
        ((MainActivity)getActivity()).setScrimVisibility(false);
    }
}
