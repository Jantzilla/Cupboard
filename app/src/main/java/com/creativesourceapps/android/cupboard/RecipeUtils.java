package com.creativesourceapps.android.cupboard;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.BreakIterator;
import java.util.ArrayList;
import java.util.Locale;
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

        if(result.matches("\\d+-\\d+"))
            result = result.replaceAll("-\\d+","");

        //Reform Quantity
        result = result.replaceAll("1\\/2|\u00BD", ".50").replaceAll("1\\/4|\u00BC", ".25")
                .replaceAll("1\\/3|\u2153|3rd", ".33").replaceAll("3\\/4|\u00BE", ".75")
                .replaceAll("1\\/8|\u215B", ".13").replaceAll(" - |-", "");

        result = result.replaceAll("\\/\\d+\\D+.*", "");

        //Consistent Unit
        result = result.replaceAll("TSP|Tsp|Teaspoons|teaspoons|Teaspoon|teaspoon", "tsp");
        result = result.replaceAll("Tbsp|TBS|Tbs|tbs\\w*|TBLSP|Tblsp|tblsp|Tbls|tbls|Tablespoons|tablespoons|Tablespoon|tablespoon", "tbsp");
        result = result.replaceAll("lb|LB|LBS|Pounds|Pound|pound|pounds", "lbs");
        result = result.replaceAll("OZ|Ounce|Ounces|OUNCES|OUNCE|ounce", "oz");
        result = result.replaceAll("mL|ML|Ml", "ml");
        result = result.replaceAll("L|Litres|litres|Litre|litre|Liters|liters|Liter|liter", "l");
        result = result.replaceAll("grams|Grams|G", "g");
        result = result.replaceAll("KGS|Kgs|kgs|KG|Kg|kg|kilograms|Kilograms", "kg");
        result = result.replaceAll("QTS|Qts|qts|QT|Qt|qt|Quarts|quarts|Quart|quart", "qt");
        result = result.replaceAll("CUPS|Cups|CUP|Cup|cups|cup", "cup");
        result = result.replaceAll("Splash|splash|Dash|Pinches|pinches|Pinch|pinch|Sprinkling|sprinkling|sprinking|spinkling|Topping|drizzle|garnish|dusting|Dusting", "1 dash"); // (subtract "1 tsp")
        result = result.replaceAll("to serve", "1 pcs");

        result = result.trim();

        //Can, Stick, to serve, for frying, To glaze, garnish, Packet, Fry, Bottle, Handful/handful

        // - Remove dimensions (something x something)
        // - Remove string after "/"
        // - Remove string between "()" inclusive
        // - Replace string containing isolated " x " with "1 pcs"
        // - Lastly Remove first number if there are two ex.(3 400 g)
        // - Fix error 650g/1lb 8oz equals 650  oz

        //Add Spacing
        result = Pattern.compile("\\d*\\.*\\d+lbs").matcher(result).find() ? result.replaceAll("lbs", " lbs") : result;
        result = Pattern.compile("\\d*\\.*\\d+g").matcher(result).find() ? result.replaceAll("g", " g") : result;
        result = Pattern.compile("\\d*\\.*\\d+ml").matcher(result).find() ? result.replaceAll("ml", " ml") : result;
        result = Pattern.compile("\\d*\\.*\\d+l").matcher(result).find() ? result.replaceAll("l", " l") : result;
        result = Pattern.compile("\\d*\\.*\\d+oz").matcher(result).find() ? result.replaceAll("oz", " oz") : result;
        result = Pattern.compile("\\d*\\.*\\d+tsp").matcher(result).find() ? result.replaceAll("tsp", " tsp") : result;
        result = Pattern.compile("\\d*\\.*\\d+tbsp").matcher(result).find() ? result.replaceAll("tbsp", " tbsp") : result;
        result = Pattern.compile("\\d*\\.*\\d+kg").matcher(result).find() ? result.replaceAll("kg", " kg") : result;
        result = Pattern.compile("\\d*\\.*\\d+qt").matcher(result).find() ? result.replaceAll("qt", " qt") : result;
        result = Pattern.compile("\\d*\\.*\\d+cup").matcher(result).find() ? result.replaceAll("cup", " cup") : result;

        //Match words([Aa-Zz]) not equal to above unit keywords & remove
        result = result.replaceAll("\\b(?!\\d+\\b|(\\.)|pcs|tsp|tbsp|lbs\\b|oz|qt|dash|g\\b|kg|ml|l\\b|cup\\b)\\w+","");

        result = result.replaceAll(",", "");

        if(result.split("\\s+").length > 2) {
            result = result.replaceAll("^\\d+\\w*", "");
        }

        result = result.trim();

        if(result.equals(""))
            result = "1 pcs";

        if(result.matches("\\d+|\\d*\\.\\d+"))
            result += " pcs";

        Log.d("Debug", result + " - ( " + s + " ) " );

        if(result.matches("\\D+")) {
            String tempResult = "1 " + result;
            result = tempResult;
        }

        String[] quantityUnit = result.split("\\s+");

        return quantityUnit;
    }

    public static ArrayList<Integer> checkAvailable(Context context, ArrayList<String> ingredients, ArrayList<String> quantities, ArrayList<String> units) {
        ArrayList<Integer> indices = new ArrayList<>();
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
            selection = CupboardContract.AllIngredients.COLUMN_NAME + " = ? COLLATE NOCASE";
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
                double available = Double.valueOf(cursor.getString(cursor.getColumnIndex(CupboardContract.Ingredients.COLUMN_QUANTITY)));
                double quantity = Double.valueOf(quantities.get(i));
                String startUnit = units.get(i);
                String endUnit = cursor.getString(cursor.getColumnIndex(CupboardContract.Ingredients.COLUMN_UNIT));

                if(available >= getConversion(ingredients.get(i),quantity,startUnit,endUnit)) {
                    indices.add(i);
                }
            }

            cursor.close();

        }

        dbHelper.close();
        db.close();

        return indices;
    }

    public static ArrayList<String> parseSteps(JSONObject instructions) {
        ArrayList<String> descriptions = new ArrayList<>();

        BreakIterator iterator = BreakIterator.getSentenceInstance(Locale.US);
        String source = null;
        try {
            source = instructions.getString("strInstructions").replaceAll("\r|\n", "").replaceAll("\\.", ". ");
            iterator.setText(source);
            int start = iterator.first();
            for (int end = iterator.next();
                 end != BreakIterator.DONE;
                 start = end, end = iterator.next()) {
                if(source.substring(start,end).length() > 4)
                    descriptions.add(source.substring(start,end));
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }

        return descriptions;
    }

    public static double getConversion(String name, double quantity, String startUnit, String endUnit) {

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

        // VOLUME TO MASS CONVERSIONS (WATER)
        final double G_TO_FL_OZ = 0.035;
        final double KG_TO_GAL = 7.5;
        final double KG_TO_FL_OZ = 35;
        final double G_TO_GAL = 0.0075;
        final double OZ_TO_FL_OZ = 1;
        final double OZ_TO_GAL = 0.0078;
        final double FL_OZ_TO_LBS = 0.0625;
        final double TBSP_TO_LBS = 0.0326;
        final double TBSP_TO_OZ = 0.522;
        final double TSP_TO_LBS = 0.0109;
        final double TSP_TO_OZ = 0.166;
        final double L_TO_LBS = 2.205;
        final double L_TO_OZ = 33.81;
        final double ML_TO_LBS = 0.002205;
        final double ML_TO_OZ = 0.0353;
        final double CUP_TO_OZ = 8;
        final double CUP_TO_LBS = 0.5;
        final double QT_TO_OZ = 32;
        final double QT_TO_LBS = 2;
        final double PT_TO_OZ = 16;
        final double PT_TO_LBS = 1;

        // PCS CONVERSIONS
        final double CUP_TO_PCS = 1;
        final double GAL_TO_PCS = 16;
        final double QT_TO_PCS = 4;
        final double PT_TO_PCS = 2;
        final double FL_OZ_TO_PCS = 0.125;
        final double TBSP_TO_PCS = 0.0625;
        final double TSP_TO_PCS = 0.020;
        final double L_TO_PCS = 4.23;
        final double ML_TO_PCS = 0.004;
        final double G_TO_PCS = 0.004;
        final double KG_TO_PCS = 4.23;
        final double OZ_TO_PCS = 0.125;
        final double LBS_TO_PCS = 2;

        if(startUnit.equals(endUnit))
            return quantity;

        switch (startUnit) {
            case "kg":
                switch (endUnit) {
                    case "lbs":
                        return quantity * KG_TO_LBS;
                    case "oz":
                        return quantity * KG_TO_OZ;
                    case "fl oz":
                        return quantity * KG_TO_FL_OZ;
                    case "gal":
                        return quantity * KG_TO_GAL;
                    case "pcs":
                        return quantity * KG_TO_PCS;
                }
            case "g":
                switch (endUnit) {
                    case "lbs":
                        return quantity * G_TO_LBS;
                    case "oz":
                        return quantity * G_TO_OZ;
                    case "fl oz":
                        return quantity * G_TO_FL_OZ;
                    case "gal":
                        return quantity * G_TO_GAL;
                    case "pcs":
                        return quantity * G_TO_PCS;
                }
            case "lbs":
                switch (endUnit) {
                    case "oz":
                        return quantity / OZ_TO_LBS;
                    case "pcs":
                        return quantity * LBS_TO_PCS;
                }
            case "oz":
                switch (endUnit) {
                    case "lbs":
                        return quantity * OZ_TO_LBS;
                    case "fl oz":
                        return quantity * OZ_TO_FL_OZ;
                    case "gal":
                        return quantity * OZ_TO_GAL;
                    case "pcs":
                        return quantity * OZ_TO_PCS;
                }
            case "cup":
                switch (endUnit) {
                    case "fl oz":
                        return quantity * CUP_TO_FL_OZ;
                    case "oz":
                        return quantity * CUP_TO_OZ;
                    case "gal":
                        return quantity * CUP_TO_GAL;
                    case "lbs":
                        return quantity * CUP_TO_LBS;
                    case "pcs":
                        return quantity * CUP_TO_PCS;
                }
            case "qt":
                switch (endUnit) {
                    case "fl oz":
                        return quantity * QT_TO_FL_OZ;
                    case "oz":
                        return quantity * QT_TO_OZ;
                    case "gal":
                        return quantity * QT_TO_GAL;
                    case "lbs":
                        return quantity * QT_TO_LBS;
                    case "pcs":
                        return quantity * QT_TO_PCS;
                }
            case "pt":
                switch (endUnit) {
                    case "fl oz":
                        return quantity * PT_TO_FL_OZ;
                    case "oz":
                        return quantity * PT_TO_OZ;
                    case "gal":
                        return quantity * PT_TO_GAL;
                    case "lbs":
                        return quantity * PT_TO_LBS;
                    case "pcs":
                        return quantity * PT_TO_PCS;
                }
            case "tbsp":
                switch (endUnit) {
                    case "fl oz":
                        return quantity * TBSP_TO_FL_OZ;
                    case "oz":
                        return quantity * TBSP_TO_OZ;
                    case "gal":
                        return quantity * TBSP_TO_GAL;
                    case "lbs":
                        return quantity * TBSP_TO_LBS;
                    case "pcs":
                        return quantity * TBSP_TO_PCS;
                }
            case "tsp":
                switch (endUnit) {
                    case "fl oz":
                        return quantity * TSP_TO_FL_OZ;
                    case "oz":
                        return quantity * TSP_TO_OZ;
                    case "gal":
                        return quantity * TSP_TO_GAL;
                    case "lbs":
                        return quantity * TSP_TO_LBS;
                    case "pcs":
                        return quantity * TSP_TO_PCS;
                }
            case "l":
                switch (endUnit) {
                    case "fl oz":
                        return quantity * L_TO_FL_OZ;
                    case "oz":
                        return quantity * L_TO_OZ;
                    case "gal":
                        return quantity * L_TO_GAL;
                    case "lbs":
                        return quantity * L_TO_LBS;
                    case "pcs":
                        return quantity * L_TO_PCS;
                }
            case "ml":
                switch (endUnit) {
                    case "fl oz":
                        return quantity * ML_TO_FL_OZ;
                    case "oz":
                        return quantity * ML_TO_OZ;
                    case "gal":
                        return quantity * ML_TO_GAL;
                    case "lbs":
                        return quantity * ML_TO_LBS;
                    case "pcs":
                        return quantity * ML_TO_PCS;
                }
            case "fl oz":
                switch (endUnit) {
                    case "gal":
                        return quantity * FL_OZ_TO_GAL;
                    case "lbs":
                        return quantity * FL_OZ_TO_LBS;
                    case "pcs":
                        return quantity * FL_OZ_TO_PCS;
                }
            case "gal":
                switch (endUnit) {
                    case "pcs":
                        return quantity * GAL_TO_PCS;
                    case "fl oz":
                        return quantity / FL_OZ_TO_GAL;
                }
            case "pcs":
                switch (endUnit) {
                    case "gal":
                        return quantity * GAL_TO_PCS;
                    case "fl oz":
                        return quantity / FL_OZ_TO_PCS;
                    case "oz":
                        return quantity / OZ_TO_PCS;
                    case "lbs":
                        return quantity / LBS_TO_PCS;
                }
        }

        return quantity;
    }

}
