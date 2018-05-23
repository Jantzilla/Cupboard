package com.creativesourceapps.android.cupboard;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.concurrent.ExecutionException;

public class MainActivity extends AppCompatActivity {

    private boolean isOnline() {
        ConnectivityManager cm =
                (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        return netInfo != null && netInfo.isConnectedOrConnecting();
    }

    private void requestRecipeData() {
        String response;

        //Default Recipe API endpoint
        String myUrl = "https://d17h27t6h515a5.cloudfront.net/topher/2017/May/59121517_baking/baking.json";


        final ArrayList<Recipe> recipes = new ArrayList<>();
        recipes.clear();

            HttpRequest getRequest = new HttpRequest();

            if (isOnline()) {
                try {
                    response = getRequest.execute(myUrl).get();

                    JSONObject json = new JSONObject(response);
                    JSONArray items = json.getJSONArray("results");

                    for (int i = 0; i < items.length(); i++) {
                        JSONObject resultObject = items.getJSONObject(i);
                        Recipe recipe = new Recipe(resultObject.getInt("id"),
                                resultObject.getString("title"));
                        recipes.add(recipe);
                    }

                } catch (InterruptedException e) {
                    e.printStackTrace();
                } catch (ExecutionException e) {
                    e.printStackTrace();
                } catch (JSONException e) {
                    e.printStackTrace();
                } catch (NullPointerException e) {
                    e.printStackTrace();
                }
            } else {
                Toast.makeText(this, "No Network Connection. Please reconnect and reload page.", Toast.LENGTH_LONG).show();
            }



        RecipeListAdapter moviesAdapter = new RecipeListAdapter(this, recipes);

        GridView gridView = findViewById(R.id.recipes_grid_view);
        gridView.setAdapter(moviesAdapter);

            gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

                @Override
                public void onItemClick(AdapterView<?> parent, View view,
                                        int position, long id) {

                    Recipe item_clicked = recipes.get(position);



                }
            });

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }
}
