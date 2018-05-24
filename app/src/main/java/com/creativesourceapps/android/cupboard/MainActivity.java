package com.creativesourceapps.android.cupboard;

import android.content.Context;
import android.content.Intent;
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

import java.io.IOException;
import java.util.ArrayList;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class MainActivity extends AppCompatActivity {

    private boolean isOnline() {
        ConnectivityManager cm =
                (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        return netInfo != null && netInfo.isConnectedOrConnecting();
    }

    private void requestRecipeData() {

        //Default Recipe API endpoint
        String myUrl = "https://d17h27t6h515a5.cloudfront.net/topher/2017/May/59121517_baking/baking.json";


        final ArrayList<Recipe> recipes = new ArrayList<>();
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
                                JSONObject resultObject = json.getJSONObject(i);
                                Recipe recipe = new Recipe(resultObject.getInt("id"),
                                        resultObject.getString("name"));
                                recipes.add(recipe);
                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                        } catch (NullPointerException e) {
                            e.printStackTrace();
                        }

                        MainActivity.this.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                //Handle UI here
                                RecipeListAdapter moviesAdapter = new RecipeListAdapter(getApplicationContext(), recipes);

                                GridView gridView = findViewById(R.id.recipes_grid_view);
                                gridView.setAdapter(moviesAdapter);

                                gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

                                    @Override
                                    public void onItemClick(AdapterView<?> parent, View view,
                                                            int position, long id) {

                                        Recipe item_clicked = recipes.get(position);

                                        Intent intent = new Intent(getApplicationContext(), RecipeActivity.class);
                                        intent.putExtra("parcel_data", item_clicked);
                                        startActivity(intent);

                                    }
                                });

                            }
                        });
                    }
                });

            } else {
                Toast.makeText(this, "No Network Connection. Please reconnect and reload page.", Toast.LENGTH_LONG).show();
            }

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        requestRecipeData();
    }
}
