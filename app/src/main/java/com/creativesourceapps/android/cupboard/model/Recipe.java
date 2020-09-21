package com.creativesourceapps.android.cupboard.model;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;

public class Recipe implements Parcelable {
    final int id;
    public int ingredientsUsed;
    public final String title, media;
    public ArrayList<String> unit;
    public ArrayList<String> quantity;
    public ArrayList<String> ingredients;
    public ArrayList<String> steps;
    public ArrayList<String> instructions;
    public ArrayList<Boolean> used, available;

    public Recipe(int id, String title, ArrayList<String> ingredients, ArrayList<String> quantity, ArrayList<String> unit,
                  ArrayList<String> shortDescription, ArrayList<String> description, String media)
    {
        this.id = id;
        this.ingredientsUsed = 0;
        this.title = title;
        this.ingredients = ingredients;
        this.quantity = quantity;
        this.unit = unit;
        this.steps = shortDescription;
        this.instructions = description;
        this.media = media;
        this.used = new ArrayList<>();
        this.available = new ArrayList<>();

        for(int i = 0; i < ingredients.size(); i++) {
            this.used.add(false);
            this.available.add(false);
        }
    }

    private Recipe(Parcel in) {
        id = in.readInt();
        ingredientsUsed = in.readInt();
        title = in.readString();
        media = in.readString();
        ingredients = new ArrayList<String>();
        quantity = new ArrayList<String>();
        unit = new ArrayList<String>();
        steps = new ArrayList<String>();
        instructions = new ArrayList<String>();
        used = new ArrayList<Boolean>();
        available = new ArrayList<Boolean>();
        in.readList(ingredients, getClass().getClassLoader());
        in.readList(quantity, getClass().getClassLoader());
        in.readList(unit, getClass().getClassLoader());
        in.readList(steps, getClass().getClassLoader());
        in.readList(instructions, getClass().getClassLoader());
        in.readList(used, getClass().getClassLoader());
        in.readList(available, getClass().getClassLoader());
    }

    public static final Creator<Recipe> CREATOR = new Creator<Recipe>() {
        @Override
        public Recipe createFromParcel(Parcel in) {
            return new Recipe(in);
        }

        @Override
        public Recipe[] newArray(int size) {
            return new Recipe[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeInt(id);
        parcel.writeInt(ingredientsUsed);
        parcel.writeString(title);
        parcel.writeString(media);
        parcel.writeList(ingredients);
        parcel.writeList(quantity);
        parcel.writeList(unit);
        parcel.writeList(steps);
        parcel.writeList(instructions);
        parcel.writeList(used);
        parcel.writeList(available);
    }
}