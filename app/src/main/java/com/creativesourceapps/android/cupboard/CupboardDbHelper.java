package com.creativesourceapps.android.cupboard;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class CupboardDbHelper extends SQLiteOpenHelper {
    public static final int DATABASE_VERSION = 1;
    public static final String DATABASE_NAME = "Cupboard.db";

    private static final String SQL_CREATE_INGREDIENTS =
            "CREATE TABLE " + CupboardContract.Ingredients.TABLE_NAME + " (" +
                    CupboardContract.Ingredients._ID + " INTEGER PRIMARY KEY," +
                    CupboardContract.Ingredients.COLUMN_NAME + " TEXT," +
                    CupboardContract.Ingredients.COLUMN_CATEGORY + " TEXT," +
                    CupboardContract.Ingredients.COLUMN_QUANTITY + " INTEGER," +
                    CupboardContract.Ingredients.COLUMN_UNIT + " TEXT)";

    private static final String SQL_DELETE_INGREDIENTS =
            "DROP TABLE IF EXISTS " + CupboardContract.Ingredients.TABLE_NAME;

    public CupboardDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(SQL_CREATE_INGREDIENTS);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL(SQL_DELETE_INGREDIENTS);
        onCreate(db);
    }
}
