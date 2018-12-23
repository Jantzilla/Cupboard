package com.creativesourceapps.android.cupboard;

import android.provider.BaseColumns;

public class CupboardContract {

    private CupboardContract() {}

    public static class Ingredients implements BaseColumns {
        public static final String TABLE_NAME = "ingredients";
        public static final String COLUMN_NAME = "name";
        public static final String COLUMN_QUANTITY = "quantity";
        public static final String COLUMN_UNIT = "unit";
    }
}
