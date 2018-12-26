package com.creativesourceapps.android.cupboard;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import java.util.ArrayList;

public class CookbookFragment extends Fragment implements RecipeAdapter.ListItemClickListener {

    private RecyclerView recyclerView;
    private SQLiteDatabase db;
    private CupboardDbHelper dbHelper;
    private Cursor cursor;
    private String[] projection;

    public CookbookFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_cookbook, container, false);
        projection = new String[]{
                CupboardContract.Recipes.COLUMN_TITLE,
                CupboardContract.Recipes.COLUMN_STEPS,
                CupboardContract.Recipes.COLUMN_DIRECTIONS,
                CupboardContract.Recipes.COLUMN_MEDIA,
                CupboardContract.Recipes.COLUMN_INGREDIENTS
        };

        recyclerView = view.findViewById(R.id.cookbook_grid_view);
        dbHelper = new CupboardDbHelper(getContext());
        db = dbHelper.getReadableDatabase();
        requestRecipeData();

        return view;
    }

    private void requestRecipeData() {

        final ArrayList<Recipe> recipes = new ArrayList<>();
        recipes.clear();

        cursor = db.query(CupboardContract.Recipes.TABLE_NAME,
                projection,
                null,
                null,
                null,
                null,
                null);

        while(cursor.moveToNext()) {
            // TODO: CREATE THE RECIPE OBJECTS
        }

        RecipeAdapter recipeAdapter = new RecipeAdapter(recipes);

        recyclerView.setAdapter(recipeAdapter);

    }

    @Override
    public void onItemClickListener(int itemClicked) {

        Recipe item_clicked = recipes.get(itemClicked);
        CupboardWidgetProvider.sendRefreshBroadcast(getContext(),item_clicked);

        Intent intent = new Intent(getContext(), RecipeActivity.class);
        intent.putExtra("parcel_data", item_clicked);
        startActivity(intent);

    }
}
