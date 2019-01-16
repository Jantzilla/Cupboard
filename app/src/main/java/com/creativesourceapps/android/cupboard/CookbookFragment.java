package com.creativesourceapps.android.cupboard;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.BreakIterator;
import java.util.ArrayList;
import java.util.Locale;

public class CookbookFragment extends Fragment implements RecipeAdapter.ListItemClickListener, MainActivity.SearchChangeListener {

    private RecyclerView recyclerView;
    private SQLiteDatabase db;
    private CupboardDbHelper dbHelper;
    private Cursor cursor;
    private String[] projection, selectionArgs;
    private ArrayList<Recipe> recipes = new ArrayList<>();
    private GridLayoutManager layoutManager;
    private JSONObject jsonObject;
    private String selection, tempIngredient, tempUnit;
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
        projection = new String[]{
                CupboardContract.Recipes._ID,
                CupboardContract.Recipes.COLUMN_RECIPE
        };

        recyclerView = view.findViewById(R.id.cookbook_grid_view);
        dbHelper = new CupboardDbHelper(getContext());
        db = dbHelper.getReadableDatabase();
        layoutManager = new GridLayoutManager(getContext(), 1);

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
            ArrayList<String> description = new ArrayList<>();
            id = jsonObject.getInt("idMeal");
            name = jsonObject.getString("strMeal");

            for(int o = 1; o < 50; o++) {
                if(!jsonObject.getString("strIngredient" + o).isEmpty()) {
                    tempIngredient = jsonObject.getString("strIngredient" + o);
                    ingredient.add(tempIngredient);
                } else
                    break;
            }

            for(int o = 1; o < 50; o++) {
                if(!jsonObject.getString("strMeasure" + o).isEmpty()) {
                    tempUnit = jsonObject.getString("strMeasure" + o);
                    if(tempUnit.split(" ").length>1){

                        unit.add(tempUnit.substring(tempUnit.lastIndexOf(" ")+1));
                        quantity.add(tempUnit.substring(0, tempUnit.lastIndexOf(' ')));
                    }
                    else{
                        quantity.add("1");
                        unit.add(tempUnit);
                    }
                } else
                    break;
            }

            BreakIterator iterator = BreakIterator.getSentenceInstance(Locale.US);
            String source = jsonObject.getString("strInstructions");
            iterator.setText(source);
            int start = iterator.first();
            for (int end = iterator.next();
                 end != BreakIterator.DONE;
                 start = end, end = iterator.next()) {
                description.add(source.substring(start,end));
            }

            for(int o = 1; o < description.size(); o++) {
                shortDescription.add(("Step " + o));
            }

            media = jsonObject.getString("strMealThumb");

            Recipe recipe = new Recipe(id,
                    name, ingredient, quantity, unit, shortDescription, description, media);

            recipes.add(recipe);
            recipeIds.add(cursor.getInt(cursor.getColumnIndex(CupboardContract.Recipes._ID)));
        }

        recipeAdapter = new RecipeAdapter(recipes, CookbookFragment.this, "Cookbook");

        recyclerView.setLayoutManager(layoutManager);

        recyclerView.setAdapter(recipeAdapter);

    }

    @Override
    public void onItemClickListener(final int itemClicked, View view) {
        selection = CupboardContract.Recipes._ID + " LIKE ?";

        switch (view.getId()) {
            case R.id.iv_button:
                selectionArgs = new String[]{String.valueOf(recipeIds.get(itemClicked))};

                AlertDialog.Builder alertDialog = new AlertDialog.Builder(getContext());
                alertDialog.setCancelable(true);
                alertDialog.setTitle("Delete Recipe?");
                alertDialog.setPositiveButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });
                alertDialog.setNegativeButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        db.delete(
                                CupboardContract.Recipes.TABLE_NAME,
                                selection,
                                selectionArgs
                        );

                        recipes.remove(itemClicked);
                        recipeAdapter.remove(itemClicked);

                    }
                });
                alertDialog.show();

                break;
            default:
                                                                        //TODO: Determine if app is running on tablet device
                Recipe item_clicked = recipes.get(itemClicked);
                CupboardWidgetProvider.sendRefreshBroadcast(getContext(),item_clicked);

                ((MainActivity)getActivity()).setRecipe(item_clicked);

                fragment = new StepPagerFragment();
                fragmentManager = getActivity().getSupportFragmentManager();
                fragmentManager.beginTransaction()
                        .addToBackStack(null)
                        .replace(R.id.fl_fragment, fragment)
                        .commit();

//                Intent intent = new Intent(getContext(), RecipeActivity.class);
//                intent.putExtra("parcel_data", item_clicked);
//                startActivity(intent);
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
    }

}
