package com.creativesourceapps.android.cupboard;

import android.app.AlertDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
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
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;

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
    private Call call;
    private ProgressBar pb;
    private TextView emptyTextView;
    private RecipeAdapter recipeAdapter;
    private String savedQuery, step;

    public RecipeFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_recipe, container, false);
        recylcerView = view.findViewById(R.id.recipes_grid_view);
        pb = view.findViewById(R.id.pb);
        emptyTextView = view.findViewById(R.id.tv_no_results_message);
        layoutManager = new GridLayoutManager(getContext(), getResources().getInteger(R.integer.recipe_column_count));
        dbHelper = new CupboardDbHelper(getContext());
        db = dbHelper.getWritableDatabase();
        values = new ContentValues();
        step = getString(R.string.step);
        projection = new String[] {
                CupboardContract.Recipes.COLUMN_RECIPE
        };

        recipeAdapter = new RecipeAdapter(getContext(), recipes, RecipeFragment.this, getString(R.string.recipes));

        recylcerView.setLayoutManager(layoutManager);

        recylcerView.setAdapter(recipeAdapter);

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

        this.savedQuery = query;

        recipeAdapter.clear();
        jsonObjectArray = new ArrayList<>();
        recylcerView.removeAllViews();
        recylcerView.getRecycledViewPool().clear();

        pb.setVisibility(View.VISIBLE);

        if(call != null) {
            call.cancel();
        }

        //Default Recipe API endpoint
        String baseUrl = "https://www.themealdb.com/api/json/v1/1/search.php?s=";

        String searchUrl = baseUrl + query;

        if (isOnline()) {

            Request request = new Request.Builder()
                    .url(searchUrl)
                    .build();

            call = new OkHttpClient().newCall(request);


            call.enqueue(new Callback() {
                @Override
                public void onFailure(Call call, IOException e) {
                    e.printStackTrace();
                }

                @Override
                public void onResponse(Call call, final Response response) throws IOException {
                    if (response.isSuccessful()) {

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
                                JSONObject resultObject = json.getJSONObject(i);
                                id = resultObject.getInt("idMeal");
                                name = resultObject.getString("strMeal");

                                for(int o = 1; o < 50; o++) {
                                    if(resultObject.has("strIngredient" + o)) {
                                        if (!resultObject.getString("strIngredient" + o).isEmpty()) {
                                            tempIngredient = resultObject.getString("strIngredient" + o);
                                            if(!RecipeUtils.duplicateIngredient(ingredient, tempIngredient)) {
                                                ingredient.add(tempIngredient);
                                                if(resultObject.has("strMeasure" + o)) {
                                                    if (!resultObject.getString("strMeasure" + o).isEmpty()) {
                                                        tempUnit = resultObject.getString("strMeasure" + o);
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

                                ArrayList<String> description = new ArrayList<>(RecipeUtils.parseSteps(resultObject));

                                for(int o = 1; o < description.size(); o++) {
                                    shortDescription.add(step + " " + o);
                                }

                                media = resultObject.getString("strMealThumb");

                                Recipe recipe = new Recipe(id,
                                        name, ingredient, quantity, unit, shortDescription, description, media);

                                if(ingredient.size() == quantity.size() && name.toLowerCase().contains(savedQuery.toLowerCase())) {
                                    recipeAdapter.add(recipe);
                                    jsonObjectArray.add(resultObject);
                                }
                            }

                            getActivity().runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    //Handle UI here
                                    if(recipeAdapter.getItemCount() == 0)
                                        pb.setVisibility(View.GONE);
                                    else {
                                        pb.setVisibility(View.GONE);
                                        emptyTextView.setVisibility(View.GONE);

                                        recipeAdapter.notifyDataSetChanged();
                                    }
                                }
                            });

                        } catch (JSONException e) {
                            e.printStackTrace();
                        } catch (NullPointerException e) {
                            e.printStackTrace();
                        }

                        response.close();
                    } else
                        response.close();
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
                        Toast.makeText(getContext(), getString(R.string.recipe_already_saved), Toast.LENGTH_LONG).show();
                        savedRecipe = true;
                    }
                }

                cursor.close();

                if(!savedRecipe) {
                    AlertDialog.Builder alertDialog = new AlertDialog.Builder(getContext());
                    alertDialog.setTitle(getString(R.string.save_recipe_question));
                    alertDialog.setCancelable(true);
                    alertDialog.setPositiveButton(getString(R.string.no), new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.cancel();
                        }
                    });
                    alertDialog.setNegativeButton(getString(R.string.yes), new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            values.put(CupboardContract.Recipes.COLUMN_TITLE, recipeAdapter.get(itemClicked).title);
                            values.put(CupboardContract.Recipes.COLUMN_RECIPE, jsonObjectArray.get(itemClicked).toString());
                            db.insert(CupboardContract.Recipes.TABLE_NAME, null, values);
                        }
                    });
                    alertDialog.show();
                }

                savedRecipe = false;
                break;
            default:
                Recipe item_clicked = recipeAdapter.get(itemClicked);
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
        requestRecipeData(query);
    }

    @Override
    public void onResume() {
        super.onResume();
        ((MainActivity)getActivity()).setFloatingSearchView(getString(R.string.recipes));
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
