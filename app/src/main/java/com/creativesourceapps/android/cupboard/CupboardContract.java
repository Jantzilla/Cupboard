package com.creativesourceapps.android.cupboard;

import android.provider.BaseColumns;

public class CupboardContract {

    private CupboardContract() {}

    public static class AllIngredients implements BaseColumns {
        public static final String TABLE_NAME = "allIngredients";
        public static final String COLUMN_NAME = "name";
        public static final String COLUMN_UNIT = "unit";
        public static final String COLUMN_CATEGORY = "category";
        public static final String COLUMN_SHOPPING = "shopping";
    }

    public static class Ingredients implements BaseColumns {
        public static final String TABLE_NAME = "ingredients";
        public static final String COLUMN_NAME = "name";
        public static final String COLUMN_CATEGORY = "category";
        public static final String COLUMN_QUANTITY = "quantity";
        public static final String COLUMN_UNIT = "unit";
    }

    public static class Recipes implements BaseColumns {
        public static final String TABLE_NAME = "recipes";
        public static final String COLUMN_TITLE = "title";
        public static final String COLUMN_RECIPE = "recipe";
    }
}
