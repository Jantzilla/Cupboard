package com.creativesourceapps.android.cupboard;

import android.app.Dialog;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Build;
import android.os.Bundle;
import android.support.constraint.ConstraintLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;

import java.util.ArrayList;


public class DetailCupboardFragment extends Fragment implements MainActivity.SearchChangeListener {
    ConstraintLayout constraintLayout;
    RecyclerView recyclerView;
    CupboardDetailAdapter adapter;
    ImageView imageView;
    private FloatingActionButton fab;
    private ArrayList<Ingredient> ingredientsList;
    private Ingredient ingredient;
    private LinearLayoutManager linearLayoutManager;
    private Dialog dialog;
    private ArrayAdapter<CharSequence> unitSpinnerAdapter, categorySpinnerAdapter;
    private Spinner unitSpinner, categorySpinner;
    private Button saveButton;
    private EditText ingredientEditText, quantityEditText;
    private TextView categoryTextView;
    private String selectedUnit, selectedCategory, category, selection, sortOrder;
    private CupboardDbHelper dbHelper;
    private SQLiteDatabase db;
    private String[] projection, selectionArgs;
    private Cursor cursor;
    private boolean savedIngredient;

    public DetailCupboardFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_detail_cupboard, container, false);

        // Inflate the layout for this fragment
        constraintLayout = view.findViewById(R.id.cl_cupboard_detail);

        Bundle bundle = getArguments();

        category = bundle.getString("Shared Element");

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            constraintLayout.setTransitionName(category);
        }

        categoryTextView = view.findViewById(R.id.tv_category);
        imageView = view.findViewById(R.id.iv_collapse);
        recyclerView = view.findViewById(R.id.rv_cupboard_detail);
        fab = view.findViewById(R.id.fab_add);
        linearLayoutManager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(linearLayoutManager);
        ingredientsList = new ArrayList<>();
        categoryTextView.setText(category);
        dbHelper = new CupboardDbHelper(getContext());
        db = dbHelper.getReadableDatabase();

        ((MainActivity)getActivity()).updateSearchListener(DetailCupboardFragment.this);

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog = new Dialog(getContext());
                dialog.setContentView(R.layout.dialog_ingredient_add);
                categorySpinner = dialog.findViewById(R.id.spin_group);
                unitSpinner = dialog.findViewById(R.id.spin_unit);
                saveButton = dialog.findViewById(R.id.btn_save);
                ingredientEditText = dialog.findViewById(R.id.tv_ingredient);
                quantityEditText = dialog.findViewById(R.id.tv_quantity);

                dialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
                    @Override
                    public void onCancel(DialogInterface dialog) {
                        ingredientEditText.setError(null);
                        quantityEditText.setError(null);
                    }
                });

                unitSpinnerAdapter = ArrayAdapter.createFromResource(getContext(),R.array.units_array, R.layout.dropdown_item);
                unitSpinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                unitSpinner.setAdapter(unitSpinnerAdapter);

                categorySpinnerAdapter = ArrayAdapter.createFromResource(getContext(),R.array.categories_array, R.layout.dropdown_item);
                categorySpinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                categorySpinner.setAdapter(categorySpinnerAdapter);

                selectedUnit = "g";              //Reset default ingredient selectedUnit

                if(!category.equals("All Ingredients")) {
                    categorySpinner.setVisibility(View.INVISIBLE);
                    selectedCategory = category;  //Reset default ingredient selectedCategory
                } else {
                    selectedCategory = "Seasoning";  //Reset default ingredient selectedCategory

                    categorySpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                        @Override
                        public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                            selectedCategory = String.valueOf(parent.getItemAtPosition(position));
                        }

                        @Override
                        public void onNothingSelected(AdapterView<?> parent) {

                        }
                    });


                }

                saveButton.setOnClickListener(new View.OnClickListener() {
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
                            ingredientsList.add(ingredient);

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

                                adapter.addIngredient(ingredient);
                                adapter.notifyItemInserted(ingredientsList.size());
                                dialog.cancel();
                            }

                            savedIngredient = false;

                        }
                    }
                });

                unitSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                        selectedUnit = String.valueOf(parent.getItemAtPosition(position));
                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> parent) {

                    }
                });

                dialog.show();
            }
        });

        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getActivity().onBackPressed();
            }
        });

        return view;
    }

    public void getIngredientData(String query) {
        projection = new String[]{
                CupboardContract.Ingredients.COLUMN_NAME,
                CupboardContract.Ingredients.COLUMN_QUANTITY,
                CupboardContract.Ingredients.COLUMN_UNIT,
                CupboardContract.Ingredients.COLUMN_CATEGORY
        };

        sortOrder = CupboardContract.Ingredients.COLUMN_NAME;

        if(category.equals("All Ingredients")) {
            selection = null;
            selectionArgs = null;
            if(!query.equals("")) {
                selection = CupboardContract.Ingredients.COLUMN_NAME + " LIKE ?";
                selectionArgs = new String[]{"%" + query + "%"};
            }
        } else {
            selection = CupboardContract.Ingredients.COLUMN_CATEGORY + " = ?";
            selectionArgs = new String[]{category};
            if(!query.equals("")) {
                selection = CupboardContract.Ingredients.COLUMN_CATEGORY + " = ? AND " +
                        CupboardContract.Ingredients.COLUMN_NAME + " LIKE ?";
                selectionArgs = new String[]{category, "%" + query + "%"};
            }
        }

        cursor = db.query(
                CupboardContract.Ingredients.TABLE_NAME,
                projection,
                selection,
                selectionArgs,
                null,
                null,
                sortOrder
        );

        while(cursor.moveToNext()) {
            ingredient = new Ingredient();
            ingredient.name = cursor.getString(cursor.getColumnIndex(CupboardContract.Ingredients.COLUMN_NAME));
            ingredient.quantity = cursor.getInt(cursor.getColumnIndex(CupboardContract.Ingredients.COLUMN_QUANTITY));
            ingredient.unit = cursor.getString(cursor.getColumnIndex(CupboardContract.Ingredients.COLUMN_UNIT));
            ingredient.category = cursor.getString(cursor.getColumnIndex(CupboardContract.Ingredients.COLUMN_CATEGORY));
            ingredientsList.add(ingredient);
        }
        cursor.close();

        adapter = new CupboardDetailAdapter(category, ingredientsList);
        recyclerView.setAdapter(adapter);
    }

    @Override
    public void onSearch(String query) {

    }
}
