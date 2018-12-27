package com.creativesourceapps.android.cupboard;

import android.content.Context;
import android.content.Intent;
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
        String myUrl = "https://d17h27t6h515a5.cloudfront.net/topher/2017/May/59121517_baking/baking.json";

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
                            String name;
                            ArrayList<String> ingredient = new ArrayList<>();
                            ArrayList<Integer> quantity = new ArrayList<>();
                            ArrayList<String> unit = new ArrayList<>();
                            ArrayList<String> shortDescription = new ArrayList<>();
                            ArrayList<String> description = new ArrayList<>();
                            ArrayList<String> media = new ArrayList<>();
                            JSONObject resultObject = json.getJSONObject(i);
                            jsonObjectArray.add(resultObject);
                            id = resultObject.getInt("id");
                            name = resultObject.getString("name");

                            JSONArray ingredients = resultObject.getJSONArray("ingredients");
                            for(int o = 0; o < ingredients.length(); o++) {
                                JSONObject ingredientsJSONObject = ingredients.getJSONObject(o);
                                ingredient.add(ingredientsJSONObject.getString("ingredient"));
                                quantity.add(ingredientsJSONObject.getInt("quantity"));
                                unit.add(ingredientsJSONObject.getString("measure"));
                            }


                            JSONArray steps = resultObject.getJSONArray("steps");
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
    public void onItemClickListener(int itemClicked, View view) {

        Recipe item_clicked = recipes.get(itemClicked);
        CupboardWidgetProvider.sendRefreshBroadcast(getContext(),item_clicked);

        Intent intent = new Intent(getContext(), RecipeActivity.class);
        intent.putExtra("parcel_data", item_clicked);
        startActivity(intent);

    }
}
