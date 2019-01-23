package com.creativesourceapps.android.cupboard;

import android.util.Log;

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

}
