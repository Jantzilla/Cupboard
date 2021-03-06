package com.creativesourceapps.android.cupboard.ui.view;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.os.Bundle;

import com.creativesourceapps.android.cupboard.data.CupboardContract;
import com.creativesourceapps.android.cupboard.data.CupboardDbHelper;
import com.creativesourceapps.android.cupboard.R;
import com.creativesourceapps.android.cupboard.util.RecipeUtils;
import com.creativesourceapps.android.cupboard.data.model.Ingredient;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import androidx.transition.Fade;
import androidx.transition.Slide;
import androidx.transition.TransitionSet;
import androidx.fragment.app.Fragment;
import android.text.Editable;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.TextWatcher;
import android.text.style.ForegroundColorSpan;
import androidx.transition.Transition;
import androidx.transition.TransitionManager;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;


public class IngredientAddFragment extends Fragment {
    @BindView(R.id.fab_back) FloatingActionButton fab;
    @BindView(R.id.fab_add) FloatingActionButton addFab;
    @BindView(R.id.fab_delete) FloatingActionButton deleteFab;
    private SQLiteDatabase db;
    private CupboardDbHelper dbHelper;
    @BindView(R.id.btn_use) Button useButton;
    @BindView(R.id.et_title) EditText ingredientEditText;
    @BindView(R.id.tv_hint) EditText hintEditText;
    @BindView(R.id.et_quantity) EditText quantityEditText;
    @BindView(R.id.tv_unit) TextView unitTextView;
    @BindView(R.id.tv_title) TextView titleTextView;
    @BindView(R.id.tv_quantity) TextView quantityTextView;
    @BindView(R.id.iv_ingredient) ImageView ingredientImageView;
    @BindView(R.id.iv_used) ImageView usedImageView;
    private String name, quantity, unit, selectedUnit, type, baseImageUrl;
    private boolean savedIngredient, availableIngredient, isUsed;
    private String selection;
    private static String selectedCategory;
    private String[] selectionArgs;
    private int entryEnd;
    private Transition transitionFade, transitionSlide;
    private ViewGroup viewGroup;
    private TransitionSet usedTransitionSet;

    public IngredientAddFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        final View view = inflater.inflate(R.layout.fragment_ingredient_add, container, false);
        ButterKnife.bind(this, view);

        type = "new";

        viewGroup = container;

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getActivity().onBackPressed();
            }
        });

        ((MainActivity)getActivity()).setScrimVisibility(true);

        baseImageUrl = "https://www.themealdb.com/images/ingredients/";

        selection = CupboardContract.AllIngredients.COLUMN_NAME + " LIKE ?";

        ingredientEditText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if(actionId == EditorInfo.IME_ACTION_DONE || actionId == EditorInfo.IME_ACTION_NEXT || actionId == EditorInfo.IME_ACTION_SEARCH || actionId == EditorInfo.IME_ACTION_SEND) {
                    ingredientEditText.setText(hintEditText.getText().toString());
                    ingredientEditText.setSelection(ingredientEditText.getText().length());
                    InputMethodManager imm = (InputMethodManager) getContext().getSystemService(Activity.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
                    return true;
                }
                return false;
            }
        });

        quantityEditText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if(actionId == EditorInfo.IME_ACTION_DONE || actionId == EditorInfo.IME_ACTION_NEXT || actionId == EditorInfo.IME_ACTION_SEARCH || actionId == EditorInfo.IME_ACTION_SEND) {
                    InputMethodManager imm = (InputMethodManager) getContext().getSystemService(Activity.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
                    return true;
                }
                return false;
            }
        });

        usedTransitionSet = new TransitionSet();
        transitionFade = new Fade();
        transitionSlide = new Slide(Gravity.BOTTOM);
        transitionFade.addTarget(usedImageView);
        transitionSlide.setStartDelay(300).addTarget(useButton);
        usedTransitionSet.addTransition(transitionFade).addTransition(transitionSlide);

        if(getArguments() != null && getArguments().getString("type") != null) {
            type = getArguments().getString("type");

            if(getArguments().getString("type").equals("edit")) {
                name = getArguments().getString("name");
                quantity = getArguments().getString("quantity");
                unit = getArguments().getString("unit");

                ingredientEditText.setVisibility(View.GONE);
                hintEditText.setEnabled(false);
                titleTextView.setVisibility(View.VISIBLE);
                titleTextView.setText(name);
                quantityEditText.setText(quantity);
                unitTextView.setText(unit);
                deleteFab.show();
                addFab.setImageResource(R.drawable.edit);

                deleteFab.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        selectionArgs = new String[] {titleTextView.getText().toString()};
                        db.delete(
                                CupboardContract.Ingredients.TABLE_NAME,
                                selection,
                                selectionArgs
                        );
                        getActivity().onBackPressed();
                    }
                });

                Glide.with(getContext()).load(baseImageUrl + name + ".png").into(ingredientImageView);

            } if(getArguments().getString("type").equals("shop")) {
                name = getArguments().getString("name");
                quantity = getArguments().getString("quantity");
                unit = getArguments().getString("unit");
                selectedCategory = getArguments().getString("category");

                ingredientEditText.setVisibility(View.GONE);
                hintEditText.setEnabled(false);
                titleTextView.setVisibility(View.VISIBLE);
                titleTextView.setText(name);
                quantityEditText.setText(quantity);
                unitTextView.setText(unit);
                deleteFab.show();
                deleteFab.setImageResource(R.drawable.remove_shopping_cart);

                deleteFab.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        ContentValues values = new ContentValues();
                        values.put(CupboardContract.AllIngredients.COLUMN_SHOPPING, Integer.toString(0));
                        selectionArgs = new String[] {titleTextView.getText().toString()};
                        db.update(
                                CupboardContract.AllIngredients.TABLE_NAME,
                                values,
                                selection,
                                selectionArgs
                        );
                        getActivity().onBackPressed();
                    }
                });

                Glide.with(getContext()).load(baseImageUrl + name + ".png").into(ingredientImageView);

            } if(getArguments().getString("type").equals("detail")) {
                boolean available = getArguments().getBoolean("availability");
                boolean used = getArguments().getBoolean("used", false);
                name = getArguments().getString("name");
                quantity = getArguments().getString("quantity");
                unit = getArguments().getString("unit");
                final int index = getArguments().getInt("index");

                ingredientEditText.setVisibility(View.GONE);
                quantityEditText.setVisibility(View.GONE);
                hintEditText.setEnabled(false);
                titleTextView.setVisibility(View.VISIBLE);
                quantityTextView.setVisibility(View.VISIBLE);
                titleTextView.setText(name);
                quantityTextView.setText(quantity);
                unitTextView.setText(unit);
                addFab.setImageResource(R.drawable.shopping_cart);

                if(available) {
                    useButton.setVisibility(View.VISIBLE);
                    useButton.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            useIngredient(index);
                        }
                    });
                }

                if(used) {
                    useButton.setVisibility(View.GONE);
                    usedImageView.setVisibility(View.VISIBLE);
                }

                Glide.with(getContext()).load(baseImageUrl + name + ".png").into(ingredientImageView);

            } if(getArguments().getString("type").equals("choose")) {
                addFab.setImageResource(R.drawable.shopping_cart);
                quantityEditText.setVisibility(View.INVISIBLE);
                unitTextView.setVisibility(View.INVISIBLE);

                Glide.with(getContext()).load(baseImageUrl + "Allspice" + ".png").into(ingredientImageView);
            }
        } else {

            Glide.with(getContext()).load(baseImageUrl + "Allspice" + ".png").into(ingredientImageView);

        }

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

                Ingredient ingredient = searchIngredients(getContext(), CupboardContract.AllIngredients.TABLE_NAME, String.valueOf(s));

                Spannable wordToSpan = new SpannableString(ingredient.name);

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
                selection = CupboardContract.AllIngredients.COLUMN_NAME + " LIKE ?";
                selectionArgs = new String[] {titleTextView.getText().toString()};
                if((type.equals("new") || type.equals("choose")) && ingredientEditText.getText().toString().isEmpty())
                    ingredientEditText.setError(getString(R.string.please_enter_name));
                if(!type.equals("detail") && !type.equals("choose") && quantityEditText.getText().toString().isEmpty())
                    quantityEditText.setError(getString(R.string.please_enter_quantity));
                else {
                    if(type.equals("edit")) {
                        ContentValues values = new ContentValues();
                        values.put(CupboardContract.Ingredients.COLUMN_QUANTITY, quantityEditText.getText().toString());
                        db.update(
                                CupboardContract.Ingredients.TABLE_NAME,
                                values,
                                selection,
                                selectionArgs
                        );
                        getActivity().onBackPressed();

                    } else if(type.equals("shop")) {
                        addGroceryItem();

                    } else if(type.equals("detail")) {
                        ContentValues values = new ContentValues();
                        values.put(CupboardContract.AllIngredients.COLUMN_SHOPPING, 1);
                        db.update(
                                CupboardContract.AllIngredients.TABLE_NAME,
                                values,
                                selection,
                                selectionArgs
                        );

                        Toast.makeText(getContext(), getString(R.string.grocery_item_added), Toast.LENGTH_LONG).show();

                    } else {
                        Ingredient ingredient = new Ingredient();
                        ingredient.name = ingredientEditText.getText().toString();
                        ingredient.quantity = quantityEditText.getText().toString();
                        ingredient.unit = unitTextView.getText().toString();
                        ingredient.category = selectedCategory;

                        if (!searchIngredients(getContext(), CupboardContract.AllIngredients.TABLE_NAME, ingredient.name).name.equals(ingredient.name)) {
                            ingredientEditText.setError(getString(R.string.invalid_ingredient));
                            availableIngredient = false;
                        }

                        if (type.equals("new") &&
                                searchIngredients(getContext(), CupboardContract.Ingredients.TABLE_NAME, ingredient.name).name.equals(ingredient.name)) {
                            ingredientEditText.setError(getString(R.string.ingredient_already_exists));
                            savedIngredient = true;
                        }

                        if (!savedIngredient && availableIngredient) {

                            if (type.equals("choose")) {
                                ContentValues values = new ContentValues();
                                values.put(CupboardContract.AllIngredients.COLUMN_SHOPPING, Integer.toString(1));
                                selectionArgs = new String[]{ingredient.name};
                                db.update(
                                        CupboardContract.AllIngredients.TABLE_NAME,
                                        values,
                                        selection,
                                        selectionArgs
                                );
                                getActivity().onBackPressed();

                            } else {
                                ContentValues values = new ContentValues();
                                values.put(CupboardContract.Ingredients.COLUMN_NAME, ingredient.name);
                                values.put(CupboardContract.Ingredients.COLUMN_QUANTITY, ingredient.quantity);
                                values.put(CupboardContract.Ingredients.COLUMN_UNIT, ingredient.unit);
                                values.put(CupboardContract.Ingredients.COLUMN_CATEGORY, ingredient.category);
                                db.insert(CupboardContract.Ingredients.TABLE_NAME, null, values);

                                ingredientEditText.setText("");
                                hintEditText.setText("");
                                quantityEditText.setText("");
                                ingredientEditText.setError(null);
                                quantityEditText.setError(null);

                                Toast.makeText(getContext(), getString(R.string.ingredient_added), Toast.LENGTH_LONG).show();
                            }
                        }

                        savedIngredient = false;
                        availableIngredient = true;
                    }
                }
            }
        });

        return view;
    }

    private void addGroceryItem() {
        name = titleTextView.getText().toString();
        quantity = quantityEditText.getText().toString();
        unit = unitTextView.getText().toString();
        selection = CupboardContract.Ingredients.COLUMN_NAME + " = ?";
        selectionArgs = new String[]{name};
        Ingredient ingredient;

        ingredient = searchIngredients(getContext(), CupboardContract.Ingredients.TABLE_NAME, name);

        if (ingredient.name.equals(name)) {
            savedIngredient = true;
        }

        if (!savedIngredient) {
            ContentValues values = new ContentValues();
            values.put(CupboardContract.Ingredients.COLUMN_NAME, name);
            values.put(CupboardContract.Ingredients.COLUMN_QUANTITY, quantity);
            values.put(CupboardContract.Ingredients.COLUMN_UNIT, unit);
            values.put(CupboardContract.Ingredients.COLUMN_CATEGORY, selectedCategory);
            db.insert(CupboardContract.Ingredients.TABLE_NAME, null, values);

        } else {
            ContentValues values = new ContentValues();
            values.put(CupboardContract.Ingredients.COLUMN_QUANTITY, Double.valueOf(quantity) + Double.valueOf(ingredient.quantity));
            db.update(
                    CupboardContract.Ingredients.TABLE_NAME,
                    values,
                    selection,
                    selectionArgs);
        }

        ContentValues values = new ContentValues();
        values.put(CupboardContract.AllIngredients.COLUMN_SHOPPING, Integer.toString(0));
        selectionArgs = new String[] {titleTextView.getText().toString()};
        db.update(
                CupboardContract.AllIngredients.TABLE_NAME,
                values,
                selection,
                selectionArgs
        );
        getActivity().onBackPressed();

        savedIngredient = false;
    }

    public static Ingredient searchIngredients(Context context, String table, String query) {
        SQLiteDatabase db;
        CupboardDbHelper dbHelper = new CupboardDbHelper(context);
        String[] projection, selectionArgs;
        String selection;
        Cursor cursor;
        db = dbHelper.getWritableDatabase();

        if(table.equals(CupboardContract.AllIngredients.TABLE_NAME)){
            projection = new String[] {CupboardContract.AllIngredients.COLUMN_NAME,
                    CupboardContract.AllIngredients.COLUMN_UNIT,
                    CupboardContract.AllIngredients.COLUMN_CATEGORY};
        } else {
            projection = new String[] {CupboardContract.Ingredients.COLUMN_NAME,
                    CupboardContract.Ingredients.COLUMN_UNIT,
                    CupboardContract.Ingredients.COLUMN_QUANTITY,
                    CupboardContract.Ingredients.COLUMN_CATEGORY};
        }
        selectionArgs = new String[] {query + "%"};
        selection = CupboardContract.AllIngredients.COLUMN_NAME + " LIKE ?";
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
            if(table.equals(CupboardContract.Ingredients.TABLE_NAME))
                ingredient.quantity = cursor.getString(cursor.getColumnIndex(CupboardContract.Ingredients.COLUMN_QUANTITY));

            ingredient.name = cursor.getString(cursor.getColumnIndex(CupboardContract.AllIngredients.COLUMN_NAME));
            ingredient.unit = cursor.getString(cursor.getColumnIndex(CupboardContract.AllIngredients.COLUMN_UNIT));
            selectedCategory = cursor.getString(cursor.getColumnIndex(CupboardContract.AllIngredients.COLUMN_CATEGORY));
        }

        return ingredient;
    }

    private void useIngredient(int index) {
        name = titleTextView.getText().toString();
        quantity = quantityTextView.getText().toString();
        unit = unitTextView.getText().toString();
        selection = CupboardContract.Ingredients.COLUMN_NAME + " = ? COLLATE NOCASE";
        selectionArgs = new String[]{name};

        Ingredient ingredient;
        ingredient = searchIngredients(getContext(), CupboardContract.Ingredients.TABLE_NAME, name);
        double finalQuantity = RecipeUtils.getConversion(name, Double.valueOf(quantity),unit,ingredient.unit);

        if(!ingredient.name.equals(""))
            ((MainActivity)getActivity()).useIngredients(index);

        if(!isUsed) {
            TransitionManager.beginDelayedTransition(viewGroup, usedTransitionSet);
            usedImageView.setVisibility(View.VISIBLE);
            useButton.setVisibility(View.GONE);
        }

        isUsed = true;

        ContentValues values = new ContentValues();
        values.put(CupboardContract.Ingredients.COLUMN_QUANTITY, String.valueOf(Double.valueOf(ingredient.quantity) - finalQuantity));
        db.update(
                CupboardContract.Ingredients.TABLE_NAME,
                values,
                selection,
                selectionArgs);

        getActivity().onBackPressed();

    }

    public static ArrayList<Integer> useAllIngredients(Context context, ArrayList<Ingredient> ingredients) {
        ArrayList<Integer> usedIndices = new ArrayList<>();
        SQLiteDatabase db;
        CupboardDbHelper dbHelper = new CupboardDbHelper(context);
        String[] selectionArgs;
        String selection;
        selection = CupboardContract.Ingredients.COLUMN_NAME + " = ? COLLATE NOCASE";
        db = dbHelper.getWritableDatabase();
        MainActivity.recipe.ingredientsUsed = 1;

        for(int i = 0; i < ingredients.size(); i++) {

            selectionArgs = new String[]{ingredients.get(i).name};

            Ingredient ingredient;
            ingredient = searchIngredients(context, CupboardContract.Ingredients.TABLE_NAME, ingredients.get(i).name);

            if(!ingredient.name.equals("")) {
                ((MainActivity) context).useIngredients(i);
                usedIndices.add(i);
                double finalQuantity = RecipeUtils.getConversion(ingredients.get(i).name,
                        Double.valueOf(ingredients.get(i).quantity),ingredients.get(i).unit,ingredient.unit);

                ContentValues values = new ContentValues();
                values.put(CupboardContract.Ingredients.COLUMN_QUANTITY, String.valueOf(Double.valueOf(ingredient.quantity) - finalQuantity));
                db.update(
                        CupboardContract.Ingredients.TABLE_NAME,
                        values,
                        selection,
                        selectionArgs);
            }
        }

        return usedIndices;
    }

    @Override
    public void onPause() {
        super.onPause();
        ((MainActivity)getActivity()).setScrimVisibility(false);
    }

    @Override
    public void onResume() {
        super.onResume();
        if(!titleTextView.getText().toString().isEmpty()) {
            Glide.with(getContext()).load(baseImageUrl + name + ".png").into(ingredientImageView);
            unitTextView.setText(unit);
        }
    }
}
