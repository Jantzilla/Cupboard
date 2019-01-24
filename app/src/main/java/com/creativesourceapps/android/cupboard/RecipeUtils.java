package com.creativesourceapps.android.cupboard;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class RecipeUtils {

    public static String[] parseMeasure(String s){
        String result;

        result = s.replaceAll("\\(.*\\)", "");

        Pattern p = Pattern.compile("\\bx\\b");
        Matcher m = p.matcher(result);
        if (m.find())
            result = "";

        //Reform Quantity
        result = result.replaceAll("1\\/2|\u00BD", ".50").replaceAll("1\\/4|\u00BC", ".25")
                .replaceAll("1\\/3|\u2153|3rd", ".33").replaceAll("3\\/4|\u00BE", ".75")
                .replaceAll("1\\/8|\u215B", ".13").replaceAll("-", "");

        result = result.replaceAll("\\/\\d+\\D+", "");

        //Consistent Unit
        result = result.replaceAll("TSP|Tsp|Teaspoon|teaspoon", "tsp");
        result = result.replaceAll("TBS|Tbs|Tbsp|tbs\\w*|TBLSP|Tblsp|tblsp|Tbls|tbls|Tablespoons|tablespoons|Tablespoon|tablespoon", "tbsp");
        result = result.replaceAll("lb|LB|LBS|Pounds|Pound|pound|pounds", "lbs");
        result = result.replaceAll("OZ|Ounce|Ounces|OUNCES|OUNCE|ounce", "oz");
        result = result.replaceAll("mL|ML|Ml", "ml");
        result = result.replaceAll("grams|Grams|G", "g");
        result = result.replaceAll("KGS|Kgs|kgs|KG|Kg|kg|kilograms|Kilograms", "kg");
        result = result.replaceAll("QTS|Qts|qts|QT|Qt|qt|Quarts|quarts|Quart|quart", "qt");
        result = result.replaceAll("CUPS|Cups|CUP|Cup|cups|cup", "cup");
        result = result.replaceAll("Dash|Pinch|Sprinkling|Topping|drizzle|garnish|dusting|Dusting", "1 dash"); // (subtract "1 tsp")
        result = result.replaceAll("to serve", "1 pcs");

        result = result.trim();

        //Can, Stick, to serve, for frying, To glaze, garnish, Packet, Fry, Bottle, Handful/handful

        // - Remove dimensions (something x something)
        // - Remove string after "/"
        // - Remove string between "()" inclusive
        // - Replace string containing isolated " x " with "1 pcs"

        //Add Spacing
        result = Pattern.compile("\\d*\\.*\\d+lbs").matcher(result).find() ? result.replaceAll("lbs", " lbs") : result;
        result = Pattern.compile("\\d*\\.*\\d+g").matcher(result).find() ? result.replaceAll("g", " g") : result;
        result = Pattern.compile("\\d*\\.*\\d+ml").matcher(result).find() ? result.replaceAll("ml", " ml") : result;
        result = Pattern.compile("\\d*\\.*\\d+oz").matcher(result).find() ? result.replaceAll("oz", " oz") : result;
        result = Pattern.compile("\\d*\\.*\\d+tsp").matcher(result).find() ? result.replaceAll("tsp", " tsp") : result;
        result = Pattern.compile("\\d*\\.*\\d+tbsp").matcher(result).find() ? result.replaceAll("tbsp", " tbsp") : result;
        result = Pattern.compile("\\d*\\.*\\d+kg").matcher(result).find() ? result.replaceAll("kg", " kg") : result;
        result = Pattern.compile("\\d*\\.*\\d+qt").matcher(result).find() ? result.replaceAll("qt", " qt") : result;
        result = Pattern.compile("\\d*\\.*\\d+cup").matcher(result).find() ? result.replaceAll("cup", " cup") : result;

        //Match words([Aa-Zz]) not equal to above unit keywords & remove
        result = result.replaceAll("\\b(?!\\d|(\\.)|pcs|tsp|tbsp|lbs|oz|qt|dash|g\\b|kg|ml|cup\\b)\\w+","");

        result = result.trim();

        if(result.equals(""))
            result = "1 pcs";

        if(result.matches("\\d+|\\d*\\.\\d+"))
            result += " pcs";

//        Log.d("Debug", result + " - ( " + s + " ) " ); TODO: REMOVE AFTER TESTING IS COMPLETE

        if(result.matches("\\D+")) {
            String tempResult = "1 " + result;
            result = tempResult;
        }

        String[] quantityUnit = result.split("\\s+");

        return quantityUnit;
    }

    public static int checkAvailable(Context context, ArrayList<String> ingredients) {
        SQLiteDatabase db;
        CupboardDbHelper dbHelper = new CupboardDbHelper(context);
        String[] projection, selectionArgs;
        String selection;
        Cursor cursor;
        int count = 0;
        db = dbHelper.getWritableDatabase();
        projection = new String[] {CupboardContract.Ingredients.COLUMN_NAME,
                CupboardContract.Ingredients.COLUMN_UNIT,
                CupboardContract.Ingredients.COLUMN_QUANTITY,
                CupboardContract.Ingredients.COLUMN_CATEGORY};

        for(int i = 0; i < ingredients.size(); i++) {

            selectionArgs = new String[]{ingredients.get(i)};
            selection = CupboardContract.AllIngredients.COLUMN_NAME + " = ?";
            cursor = db.query(
                    CupboardContract.Ingredients.TABLE_NAME,
                    projection,
                    selection,
                    selectionArgs,
                    null,
                    null,
                    null
            );

            if(cursor.moveToFirst()) {
                count++;
            }

            cursor.close();

        }

        return count;
    }

    public static double getConversion(String name, int quantity, String startUnit, String endUnit) {

        // MASS CONVERSIONS
        final double KG_TO_LBS = 2.20;
        final double KG_TO_OZ = 35.274;
        final double G_TO_LBS = 0.0022;
        final double G_TO_OZ = 0.035;
        final double OZ_TO_LBS = 0.0625;

        // VOLUME CONVERSIONS
        final double CUP_TO_FL_OZ = 8;
        final double CUP_TO_GAL = 0.0625;
        final double QT_TO_FL_OZ = 32;
        final double QT_TO_GAL = 0.25;
        final double PT_TO_FL_OZ = 16;
        final double PT_TO_GAL = 0.125;
        final double FL_OZ_TO_GAL = 0.0078;
        final double TBSP_TO_GAL = 0.0039;
        final double TBSP_TO_FL_OZ = 0.5;
        final double TSP_TO_GAL = 0.0013;
        final double TSP_TO_FL_OZ = 0.166;
        final double L_TO_GAL = 0.264;
        final double L_TO_FL_OZ = 33.81;
        final double ML_TO_GAL = 0.000264;
        final double ML_TO_FL_OZ = 0.0338;

        if(startUnit.equals(endUnit))
            return quantity;

        switch (startUnit) {
            case "kg":
                switch (endUnit) {
                    case "lbs":
                        return quantity * KG_TO_LBS;
                    case "oz":
                        return quantity * KG_TO_OZ;
                }
            case "g":
                switch (endUnit) {
                    case "lbs":
                        return quantity * G_TO_LBS;
                    case "oz":
                        return quantity * G_TO_OZ;
                }
            case "oz":
                switch (endUnit) {
                    case "lbs":
                        return quantity * OZ_TO_LBS;
                }
            case "cup":
                switch (endUnit) {
                    case "fl oz":
                        return quantity * CUP_TO_FL_OZ;
                    case "gal":
                        return quantity * CUP_TO_GAL;
                }
            case "qt":
                switch (endUnit) {
                    case "fl oz":
                        return quantity * QT_TO_FL_OZ;
                    case "gal":
                        return quantity * QT_TO_GAL;
                }
            case "pt":
                switch (endUnit) {
                    case "fl oz":
                        return quantity * PT_TO_FL_OZ;
                    case "gal":
                        return quantity * PT_TO_GAL;
                }
            case "tbsp":
                switch (endUnit) {
                    case "fl oz":
                        return quantity * TBSP_TO_FL_OZ;
                    case "gal":
                        return quantity * TBSP_TO_GAL;
                }
            case "tsp":
                switch (endUnit) {
                    case "fl oz":
                        return quantity * TSP_TO_FL_OZ;
                    case "gal":
                        return quantity * TSP_TO_GAL;
                }
            case "l":
                switch (endUnit) {
                    case "fl oz":
                        return quantity * L_TO_FL_OZ;
                    case "gal":
                        return quantity * L_TO_GAL;
                }
            case "ml":
                switch (endUnit) {
                    case "fl oz":
                        return quantity * ML_TO_FL_OZ;
                    case "gal":
                        return quantity * ML_TO_GAL;
                }
            case "fl oz":
                switch (endUnit) {
                    case "gal":
                        return quantity * FL_OZ_TO_GAL;
                }
        }

        return quantity;
    }

}
