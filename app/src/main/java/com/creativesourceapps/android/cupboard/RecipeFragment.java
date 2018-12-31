package com.creativesourceapps.android.cupboard;

import android.app.AlertDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
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

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class RecipeFragment extends Fragment implements RecipeAdapter.ListItemClickListener{

    private RecyclerView recylcerView;
    private ArrayList<JSONObject> jsonObjectArray;
    private ArrayList<Recipe> recipes = new ArrayList<>();
    private GridLayoutManager layoutManager;
    private SQLiteDatabase db;
    private CupboardDbHelper dbHelper;
    private ContentValues values;
    private Cursor cursor;
    private String[] projection;
    private boolean savedRecipe;

    public RecipeFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_recipe, container, false);
        recylcerView = view.findViewById(R.id.recipes_grid_view);
        jsonObjectArray = new ArrayList<>();
        layoutManager = new GridLayoutManager(getContext(), 1);
        dbHelper = new CupboardDbHelper(getContext());
        db = dbHelper.getWritableDatabase();
        values = new ContentValues();
        projection = new String[] {
                CupboardContract.Recipes.COLUMN_RECIPE
        };
        requestRecipeData();

        return view;
    }

    private boolean isOnline() {
        ConnectivityManager cm =
                (ConnectivityManager) getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        return netInfo != null && netInfo.isConnectedOrConnecting();
    }

    private void requestRecipeData() {

        //Default Recipe API endpoint
        String myUrl = "https://www.themealdb.com/api/json/v1/1/search.php?s=";

        recipes.clear();


        if (isOnline()) {

            OkHttpClient client = new OkHttpClient();

            Request request = new Request.Builder()
                    .url(myUrl)
                    .build();

            client.newCall(request).enqueue(new Callback() {
                @Override
                public void onFailure(Call call, IOException e) {
                    e.printStackTrace();
                }

                @Override
                public void onResponse(Call call, final Response response) throws IOException {
                    if (!response.isSuccessful()) {
                        throw new IOException("Unexpected code " + response);
                    }
                    try {

                        JSONArray json = new JSONArray(response.body().string());

                        for (int i = 0; i < json.length(); i++) {
                            int id;
                            String name, tempIngredient, tempUnit;
                            ArrayList<String> ingredient = new ArrayList<>();
                            ArrayList<String> quantity = new ArrayList<>();
                            ArrayList<String> unit = new ArrayList<>();
                            ArrayList<String> shortDescription = new ArrayList<>();
                            ArrayList<String> description = new ArrayList<>();
                            ArrayList<String> media = new ArrayList<>();
                            JSONObject resultObject = json.getJSONObject(i);
                            jsonObjectArray.add(resultObject);
                            id = resultObject.getInt("idMeal");
                            name = resultObject.getString("strMeal");

                            for(int o = 1; o < 50; o++) {
                                if(!resultObject.getString("strIngredient" + o).isEmpty()) {
                                    tempIngredient = resultObject.getString("strIngredient" + o);
                                    ingredient.add(tempIngredient);
                                } else
                                    break;
                            }

                            for(int o = 1; o < 50; o++) {
                                if(!resultObject.getString("strMeasure" + o).isEmpty()) {
                                    tempUnit = resultObject.getString("strMeasure" + o);
                                    if(tempUnit.split("\\w+").length>1){

                                        quantity.add(tempUnit.substring(tempUnit.lastIndexOf(" ")+1));
                                        unit.add(tempUnit.substring(0, tempUnit.lastIndexOf(' ')));
                                    }
                                    else{
                                        quantity.add("1");
                                        unit.add(tempUnit);
                                    }
//                                    unit.add(tempUnit);
                                } else
                                    break;
                            }

//                            JSONArray ingredients = resultObject.getJSONArray("ingredients");
//                            for(int o = 0; o < ingredients.length(); o++) {
//                                JSONObject ingredientsJSONObject = ingredients.getJSONObject(o);
//                                ingredient.add(ingredientsJSONObject.getString("ingredient"));
//                                quantity.add(ingredientsJSONObject.getInt("quantity"));
//                                unit.add(ingredientsJSONObject.getString("measure"));
//                            }

                            media.add(resultObject.getString("strYoutube"));

                            description = new ArrayList<>(Arrays.asList(resultObject.getString("strInstructions").split(".")));

                            for(int o = 1; o < description.size(); o++) {
                                shortDescription.add(("Step " + o));
                            }

//                            JSONArray steps = resultObject.getJSONArray("steps");
//                            for(int o = 0; o < steps.length(); o++) {
//                                JSONObject step = steps.getJSONObject(o);
//                                shortDescription.add(step.getString("shortDescription"));
//                            }
//                            for(int o = 0; o < steps.length(); o++) {
//                                JSONObject step = steps.getJSONObject(o);
//                                description.add(step.getString("description"));
//                            }

                            Recipe recipe = new Recipe(id,
                                    name, ingredient, quantity, unit, shortDescription, description, media);

                            recipes.add(recipe);
                        }

                    } catch (JSONException e) {
                        e.printStackTrace();
                    } catch (NullPointerException e) {
                        e.printStackTrace();
                    }

                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            //Handle UI here
                            RecipeAdapter recipeAdapter = new RecipeAdapter(recipes, RecipeFragment.this);

                            recylcerView.setLayoutManager(layoutManager);

                            recylcerView.setAdapter(recipeAdapter);

                        }
                    });
                }
            });

        } else {
            Toast.makeText(getContext(), R.string.no_network_connection, Toast.LENGTH_LONG).show();
        }

    }

    @Override
    public void onItemClickListener(final int itemClicked, View view) {

        switch (view.getId()) {
            case R.id.iv_button:
                cursor = db.query(
                        CupboardContract.Recipes.TABLE_NAME,
                        projection,
                        null,
                        null,
                        null,
                        null,
                        null);

                while(cursor.moveToNext()) {

                    if(cursor.getString(cursor.getColumnIndex(CupboardContract.Recipes.COLUMN_RECIPE))
                            .equals(jsonObjectArray.get(itemClicked).toString())) {
                        Toast.makeText(getContext(), "This Recipe is already saved.", Toast.LENGTH_LONG).show();
                        savedRecipe = true;
                    }
                }

                if(!savedRecipe) {
                    AlertDialog.Builder alertDialog = new AlertDialog.Builder(getContext());
                    alertDialog.setTitle("Save Recipe?");
                    alertDialog.setCancelable(true);
                    alertDialog.setPositiveButton("No", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.cancel();
                        }
                    });
                    alertDialog.setNegativeButton("Yes", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            values.put(CupboardContract.Recipes.COLUMN_TITLE, recipes.get(itemClicked).title);
                            values.put(CupboardContract.Recipes.COLUMN_RECIPE, jsonObjectArray.get(itemClicked).toString());
                            db.insert(CupboardContract.Recipes.TABLE_NAME, null, values);
                        }
                    });
                    alertDialog.show();
                }

                savedRecipe = false;
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
