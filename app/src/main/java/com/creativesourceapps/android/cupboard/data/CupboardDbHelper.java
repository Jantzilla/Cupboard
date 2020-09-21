package com.creativesourceapps.android.cupboard.data;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;

public class CupboardDbHelper extends SQLiteOpenHelper {
    public final Context context;
    public static final int DATABASE_VERSION = 1;
    public static final String DATABASE_NAME = "Cupboard.db";

    private static final String SQL_CREATE_ALL_INGREDIENTS =
            "CREATE TABLE " + CupboardContract.AllIngredients.TABLE_NAME + " (" +
                    CupboardContract.AllIngredients._ID + " INTEGER PRIMARY KEY," +
                    CupboardContract.AllIngredients.COLUMN_NAME + " TEXT," +
                    CupboardContract.AllIngredients.COLUMN_UNIT + " TEXT," +
                    CupboardContract.AllIngredients.COLUMN_SHOPPING + " INTEGER DEFAULT 0," +
                    CupboardContract.AllIngredients.COLUMN_CATEGORY + " TEXT)";

    private static final String SQL_CREATE_INGREDIENTS =
            "CREATE TABLE " + CupboardContract.Ingredients.TABLE_NAME + " (" +
                    CupboardContract.Ingredients._ID + " INTEGER PRIMARY KEY," +
                    CupboardContract.Ingredients.COLUMN_NAME + " TEXT," +
                    CupboardContract.Ingredients.COLUMN_CATEGORY + " TEXT," +
                    CupboardContract.Ingredients.COLUMN_QUANTITY + " INTEGER," +
                    CupboardContract.Ingredients.COLUMN_UNIT + " TEXT)";

    private static final String SQL_CREATE_RECIPES =
            "CREATE TABLE " + CupboardContract.Recipes.TABLE_NAME + " (" +
                    CupboardContract.Recipes._ID + " INTEGER PRIMARY KEY," +
                    CupboardContract.Recipes.COLUMN_TITLE + " TEXT," +
                    CupboardContract.Recipes.COLUMN_RECIPE + " TEXT)";

    private static final String SQL_DELETE_ALL_INGREDIENTS =
            "DROP TABLE IF EXISTS " + CupboardContract.AllIngredients.TABLE_NAME;

    private static final String SQL_DELETE_INGREDIENTS =
            "DROP TABLE IF EXISTS " + CupboardContract.Ingredients.TABLE_NAME;

    private static final String SQL_DELETE_RECIPES =
            "DROP TABLE IF EXISTS " + CupboardContract.Recipes.TABLE_NAME;

    public CupboardDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        this.context = context;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(SQL_CREATE_ALL_INGREDIENTS);
        buildIngredientsTable(db);
        db.execSQL(SQL_CREATE_INGREDIENTS);
        db.execSQL(SQL_CREATE_RECIPES);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL(SQL_DELETE_ALL_INGREDIENTS);
        db.execSQL(SQL_DELETE_INGREDIENTS);
        db.execSQL(SQL_DELETE_RECIPES);
        onCreate(db);
    }

    public String retrieveJson(Context context) {
        String json = null;
        try {
            InputStream is = context.getAssets().open("data.json");

            int size = is.available();

            byte[] buffer = new byte[size];

            is.read(buffer);

            is.close();

            json = new String(buffer, "UTF-8");

        } catch (IOException ex) {
            ex.printStackTrace();
        }

        return json;
    }

    public ArrayList<Ingredient> parseJson(String json) {
        ArrayList<Ingredient> ingredients = new ArrayList<>();
        JSONObject jsonObject;
        JSONArray jsonArray;

        try {
            jsonObject = new JSONObject(json);
            jsonArray = jsonObject.getJSONArray("ingredients");

            for(int i = 0; i < jsonArray.length(); i++) {
                Ingredient ingredient = new Ingredient();

                JSONObject jsonItem = jsonArray.getJSONObject(i);

                ingredient.name = jsonItem.getString("strIngredient");
                ingredient.category = jsonItem.getString("strDescription");
                ingredient.unit = jsonItem.optString("strType", "null");

                ingredients.add(ingredient);
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }

        return ingredients;
    }

    public void buildIngredientsTable(SQLiteDatabase db) {
        ArrayList<Ingredient> ingredients = new ArrayList<>();
        ingredients.addAll(parseJson(retrieveJson(context)));

        for(int i = 0; i < ingredients.size(); i++) {
            ContentValues values = new ContentValues();

            values.put(CupboardContract.AllIngredients.COLUMN_NAME, ingredients.get(i).name);
            values.put(CupboardContract.AllIngredients.COLUMN_UNIT, ingredients.get(i).unit);
            values.put(CupboardContract.AllIngredients.COLUMN_CATEGORY, ingredients.get(i).category);

            db.insert(
                    CupboardContract.AllIngredients.TABLE_NAME,
                    null,
                    values);
        }
    }
}
