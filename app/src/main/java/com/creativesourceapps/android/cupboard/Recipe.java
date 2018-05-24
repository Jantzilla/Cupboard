package com.creativesourceapps.android.cupboard;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;

public class Recipe implements Parcelable {
    final int id;
    final String title;
    ArrayList<String> steps;

    public Recipe(int id, String title, ArrayList<String> description)
    {
        this.id = id;
        this.title = title;
        this.steps = description;
    }

    private Recipe(Parcel in) {
        id = in.readInt();
        title = in.readString();
        steps = new ArrayList<String>();
        in.readList(steps, getClass().getClassLoader());
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
        parcel.writeString(title);
        parcel.writeList(steps);
    }
}