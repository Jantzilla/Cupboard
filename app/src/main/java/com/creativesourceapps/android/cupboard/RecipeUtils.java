package com.creativesourceapps.android.cupboard;

public class RecipeUtils {

    public static String[] parseMeasure(String s){
        String result;

        //Reform Quantity
        result = s.replaceAll("1\\/2", ".50").replaceAll("1\\/4", ".25")
                .replaceAll("3\\/4", ".75").replaceAll("1\\/8", ".13").replaceAll("-", "");

        //Consistent Unit
        result = result.replaceAll("TSP|Tsp|Teaspoon|teaspoon", "tsp");
        result = result.replaceAll("TBS|Tbs|tbs|TBLSP|tblsp|Tbls|tbls|Teaspoon|teaspoon", "tbsp");
        result = result.replaceAll("lb|LB|LBS|Pounds|Pound|pound|pounds", "lbs");
        result = result.replaceAll("OZ|Ounce|Ounces|OUNCES|OUNCE|ounce", "oz");
        result = result.replaceAll("Dash|Pinch|Sprinkling|Topping|drizzle", "1 dash"); // (subtract "1 tsp")
        result = result.replaceAll("to serve", "1 pcs");

        //Can, Stick, to serve

        //Add Spacing
        result = result.replaceAll("g|grams|G|Grams", " g");
        result = result.replaceAll("ml|mL|ML|Ml", " ml");

        //Match words([Aa-Zz]) not equal to above unit keywords & remove
        result = result.replaceAll("\\b(?!\\d|tsp|tbsp|lbs|oz|dash|g|ml|cup|cups\\b)\\w+","");

        result = result.trim();

        if(result.matches("\\d"))
            result += " pcs";

        String[] quantityUnit = result.split("\\s+");

        return quantityUnit;
    }

}
