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
import android.support.v4.app.FragmentManager;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.text.BreakIterator;
import java.util.ArrayList;
import java.util.Locale;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class RecipeFragment extends Fragment implements RecipeAdapter.ListItemClickListener, MainActivity.SearchChangeListener {

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
    private StepPagerFragment fragment;
    private FragmentManager fragmentManager;

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
        layoutManager = new GridLayoutManager(getContext(), getResources().getInteger(R.integer.recipe_column_count));
        dbHelper = new CupboardDbHelper(getContext());
        db = dbHelper.getWritableDatabase();
        values = new ContentValues();
        projection = new String[] {
                CupboardContract.Recipes.COLUMN_RECIPE
        };
        requestRecipeData("");

        ((MainActivity)getActivity()).updateSearchListener(RecipeFragment.this);

        return view;
    }

    private boolean isOnline() {
        ConnectivityManager cm =
                (ConnectivityManager) getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        return netInfo != null && netInfo.isConnectedOrConnecting();
    }

    private void requestRecipeData(String query) {

        //Default Recipe API endpoint
        String baseUrl = "https://www.themealdb.com/api/json/v1/1/search.php?s=";

        String searchUrl = baseUrl + query;

        recipes.clear();


        if (isOnline()) {

            OkHttpClient client = new OkHttpClient();

            Request request = new Request.Builder()
                    .url(searchUrl)
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

                        JSONObject responseObject = new JSONObject(response.body().string());

                        JSONArray json = responseObject.getJSONArray("meals");

                        for (int i = 0; i < json.length(); i++) {
                            int id;
                            String name, media, tempIngredient, tempUnit;
                            ArrayList<String> ingredient = new ArrayList<>();
                            ArrayList<String> quantity = new ArrayList<>();
                            ArrayList<String> unit = new ArrayList<>();
                            ArrayList<String> shortDescription = new ArrayList<>();
                            ArrayList<String> description = new ArrayList<>();
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
                                    String[] quantityUnit = RecipeUtils.parseMeasure(tempUnit);
                                    quantity.add(quantityUnit[0]);
                                    unit.add(quantityUnit[1]);
                                } else
                                    break;
                            }

                            BreakIterator iterator = BreakIterator.getSentenceInstance(Locale.US);
                            String source = resultObject.getString("strInstructions");
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

                            media = resultObject.getString("strMealThumb");

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
                            RecipeAdapter recipeAdapter = new RecipeAdapter(recipes, RecipeFragment.this, "Recipes");

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
            default:
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
        requestRecipeData(query);
    }

    @Override
    public void onResume() {
        super.onResume();
        ((MainActivity)getActivity()).setFloatingSearchView("Recipes");
    }

}
