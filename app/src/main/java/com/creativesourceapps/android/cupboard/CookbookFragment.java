package com.creativesourceapps.android.cupboard;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class CookbookFragment extends Fragment implements RecipeAdapter.ListItemClickListener {

    private RecyclerView recyclerView;
    private SQLiteDatabase db;
    private CupboardDbHelper dbHelper;
    private Cursor cursor;
    private String[] projection, selectionArgs;
    private ArrayList<Recipe> recipes = new ArrayList<>();
    private GridLayoutManager layoutManager;
    private JSONObject jsonObject;
    private String selection;
    private RecipeAdapter recipeAdapter;
    private ArrayList<Integer> recipeIds = new ArrayList<>();

    public CookbookFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_cookbook, container, false);
        selection = CupboardContract.Recipes._ID + " LIKE ?";
        projection = new String[]{
                CupboardContract.Recipes._ID,
                CupboardContract.Recipes.COLUMN_RECIPE
        };

        recyclerView = view.findViewById(R.id.cookbook_grid_view);
        dbHelper = new CupboardDbHelper(getContext());
        db = dbHelper.getReadableDatabase();
        layoutManager = new GridLayoutManager(getContext(), 1);

        try {
            requestRecipeData();
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return view;
    }

    private void requestRecipeData() throws JSONException {

        recipes.clear();

        cursor = db.query(CupboardContract.Recipes.TABLE_NAME,
                projection,
                null,
                null,
                null,
                null,
                null);

        while(cursor.moveToNext()) {

            String json = cursor.getString(cursor.getColumnIndex(CupboardContract.Recipes.COLUMN_RECIPE));
            jsonObject = new JSONObject(json);

            int id;
            String name;
            ArrayList<String> ingredient = new ArrayList<>();
            ArrayList<Integer> quantity = new ArrayList<>();
            ArrayList<String> unit = new ArrayList<>();
            ArrayList<String> shortDescription = new ArrayList<>();
            ArrayList<String> description = new ArrayList<>();
            ArrayList<String> media = new ArrayList<>();
            id = jsonObject.getInt("id");
            name = jsonObject.getString("name");

            JSONArray ingredients = jsonObject.getJSONArray("ingredients");
            for(int o = 0; o < ingredients.length(); o++) {
                JSONObject ingredientsJSONObject = ingredients.getJSONObject(o);
                ingredient.add(ingredientsJSONObject.getString("ingredient"));
                quantity.add(ingredientsJSONObject.getInt("quantity"));
                unit.add(ingredientsJSONObject.getString("measure"));
            }


            JSONArray steps = jsonObject.getJSONArray("steps");
            for(int o = 0; o < steps.length(); o++) {
                JSONObject step = steps.getJSONObject(o);
                shortDescription.add(step.getString("shortDescription"));
            }
            for(int o = 0; o < steps.length(); o++) {
                JSONObject step = steps.getJSONObject(o);
                description.add(step.getString("description"));
            }
            for(int o = 0; o < steps.length(); o++) {
                JSONObject step = steps.getJSONObject(o);
                String tempUrl = step.getString("videoURL");
                if(tempUrl.equals(""))
                    media.add(step.getString("thumbnailURL"));
                else
                    media.add(step.getString("videoURL"));
            }
            Recipe recipe = new Recipe(id,
                    name, ingredient, quantity, unit, shortDescription, description, media);

            recipes.add(recipe);
            recipeIds.add(cursor.getInt(cursor.getColumnIndex(CupboardContract.Recipes._ID)));
        }

        recipeAdapter = new RecipeAdapter(recipes, CookbookFragment.this);

        recyclerView.setLayoutManager(layoutManager);

        recyclerView.setAdapter(recipeAdapter);

    }

    @Override
    public void onItemClickListener(final int itemClicked, View view) {

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

                Toast.makeText(getContext(),"Image button pressed!", Toast.LENGTH_LONG).show();
                break;
            case R.id.grid_item_recipe:
                Recipe item_clicked = recipes.get(itemClicked);
                CupboardWidgetProvider.sendRefreshBroadcast(getContext(),item_clicked);

                Intent intent = new Intent(getContext(), RecipeActivity.class);
                intent.putExtra("parcel_data", item_clicked);
                startActivity(intent);
        }
    }
}
