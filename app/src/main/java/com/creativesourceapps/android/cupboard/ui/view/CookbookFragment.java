package com.creativesourceapps.android.cupboard.ui.view;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.creativesourceapps.android.cupboard.data.CupboardContract;
import com.creativesourceapps.android.cupboard.data.CupboardDbHelper;
import com.creativesourceapps.android.cupboard.R;
import com.creativesourceapps.android.cupboard.data.model.Recipe;
import com.creativesourceapps.android.cupboard.ui.CupboardWidgetProvider;
import com.creativesourceapps.android.cupboard.ui.adapter.RecipeAdapter;
import com.creativesourceapps.android.cupboard.util.RecipeUtils;

import org.json.JSONException;
import org.json.JSONObject;
import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

public class CookbookFragment extends Fragment implements RecipeAdapter.ListItemClickListener, MainActivity.SearchChangeListener {

    @BindView(R.id.cookbook_grid_view) RecyclerView recyclerView;
    @BindView(R.id.tv_empty_message) TextView emptyTextView;
    private ProgressBar pb;
    private SQLiteDatabase db;
    private CupboardDbHelper dbHelper;
    private Cursor cursor;
    private String[] projection, selectionArgs;
    private ArrayList<Recipe> recipes = new ArrayList<>();
    private GridLayoutManager layoutManager;
    private JSONObject jsonObject;
    private String selection, tempIngredient, tempUnit, step;
    private RecipeAdapter recipeAdapter;
    private ArrayList<Integer> recipeIds = new ArrayList<>();
    private StepPagerFragment fragment;
    private FragmentManager fragmentManager;

    public CookbookFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_cookbook, container, false);
        ButterKnife.bind(this, view);
        projection = new String[]{
                CupboardContract.Recipes._ID,
                CupboardContract.Recipes.COLUMN_RECIPE
        };

        pb = view.findViewById(R.id.pb);
        dbHelper = new CupboardDbHelper(getContext());
        db = dbHelper.getWritableDatabase();
        step = getString(R.string.step);
        layoutManager = new GridLayoutManager(getContext(), getResources().getInteger(R.integer.recipe_column_count));

        try {
            requestRecipeData("");
        } catch (JSONException e) {
            e.printStackTrace();
        }

        ((MainActivity)getActivity()).updateSearchListener(CookbookFragment.this);

        return view;
    }

    private void requestRecipeData(String query) throws JSONException {
        if(query.equals("")) {
            selectionArgs = null;
            selection = null;
        }
        else {
            selectionArgs = new String[]{"%" + query + "%"};
            selection = CupboardContract.Recipes.COLUMN_TITLE + " LIKE ?";
        }

        recipes.clear();

        cursor = db.query(CupboardContract.Recipes.TABLE_NAME,
                projection,
                selection,
                selectionArgs,
                null,
                null,
                null);

        while(cursor.moveToNext()) {

            String json = cursor.getString(cursor.getColumnIndex(CupboardContract.Recipes.COLUMN_RECIPE));
            jsonObject = new JSONObject(json);

            int id;
            String name, media;
            ArrayList<String> ingredient = new ArrayList<>();
            ArrayList<String> quantity = new ArrayList<>();
            ArrayList<String> unit = new ArrayList<>();
            ArrayList<String> shortDescription = new ArrayList<>();
            id = jsonObject.getInt("idMeal");
            name = jsonObject.getString("strMeal");

            for(int o = 1; o < 50; o++) {
                if(jsonObject.has("strIngredient" + o)) {
                    if (!jsonObject.getString("strIngredient" + o).isEmpty()) {
                        tempIngredient = jsonObject.getString("strIngredient" + o);
                        if(!RecipeUtils.duplicateIngredient(ingredient, tempIngredient)) {
                            ingredient.add(tempIngredient);
                            if(jsonObject.has("strMeasure" + o)) {
                                if (!jsonObject.getString("strMeasure" + o).isEmpty()) {
                                    tempUnit = jsonObject.getString("strMeasure" + o);
                                    String[] quantityUnit = RecipeUtils.parseMeasure(tempUnit);
                                    quantity.add(quantityUnit[0]);
                                    unit.add(quantityUnit[1]);
                                } else
                                    break;
                            }
                        }
                    } else
                        break;
                }
            }

            ArrayList<String> description = new ArrayList<>(RecipeUtils.parseSteps(jsonObject));

            for(int o = 1; o < description.size(); o++) {
                shortDescription.add(step + " " + o);
            }

            media = jsonObject.getString("strMealThumb");

            Recipe recipe = new Recipe(id,
                    name, ingredient, quantity, unit, shortDescription, description, media);

            if(ingredient.size() == quantity.size()) {
                recipes.add(recipe);
                recipeIds.add(cursor.getInt(cursor.getColumnIndex(CupboardContract.Recipes._ID)));
            }
        }

        cursor.close();

        if(recipes.size() == 0)
            pb.setVisibility(View.GONE);
        else {
            pb.setVisibility(View.GONE);
            emptyTextView.setVisibility(View.GONE);
            recipeAdapter = new RecipeAdapter(getContext(), recipes, CookbookFragment.this, "Cookbook");

            recyclerView.setLayoutManager(layoutManager);

            recyclerView.setAdapter(recipeAdapter);
        }

    }

    @Override
    public void onItemClickListener(final int itemClicked, View view) {
        selection = CupboardContract.Recipes._ID + " LIKE ?";

        switch (view.getId()) {
            case R.id.iv_button:
                selectionArgs = new String[]{String.valueOf(recipeIds.get(itemClicked))};

                AlertDialog.Builder alertDialog = new AlertDialog.Builder(getContext());
                alertDialog.setCancelable(true);
                alertDialog.setTitle(getString(R.string.delete_recipe) + "?");
                alertDialog.setPositiveButton(getString(R.string.no), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });
                alertDialog.setNegativeButton(getString(R.string.yes), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        db.delete(
                                CupboardContract.Recipes.TABLE_NAME,
                                selection,
                                selectionArgs
                        );

                        recipes.remove(itemClicked);
                        recipeAdapter.remove(itemClicked);

                        if(recipes.size() == 0)
                            emptyTextView.setVisibility(View.VISIBLE);
                    }
                });
                alertDialog.show();

                break;
            default:
                Recipe item_clicked = recipes.get(itemClicked);
                CupboardWidgetProvider.sendRefreshBroadcast(getContext(),item_clicked);

                ((MainActivity)getActivity()).setRecipe(item_clicked);

                fragment = new StepPagerFragment();
                MainActivity.restoreFragment = fragment;
                fragmentManager = getActivity().getSupportFragmentManager();
                fragmentManager.beginTransaction()
                        .addToBackStack(null)
                        .replace(R.id.fl_fragment, fragment)
                        .commit();

        }
    }

    @Override
    public void onSearch(String query) {
        try {
            requestRecipeData(query);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        ((MainActivity)getActivity()).setFloatingSearchView("Cookbook");
        MainActivity.restoreFragment = this;
    }

    @Override
    public void onDestroy() {
        if(cursor != null)
            cursor.close();
        if(dbHelper != null)
            dbHelper.close();
        if(db != null)
            db.close();
        super.onDestroy();
    }
}
