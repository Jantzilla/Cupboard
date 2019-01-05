package com.creativesourceapps.android.cupboard;

import android.app.Dialog;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Build;
import android.os.Bundle;
import android.support.constraint.ConstraintLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;

import java.util.ArrayList;


public class DetailCupboardFragment extends Fragment implements MainActivity.SearchChangeListener {
    ConstraintLayout constraintLayout;
    RecyclerView recyclerView;
    CupboardDetailAdapter adapter;
    ImageView imageView;
    private FloatingActionButton fab;
    private ArrayList<Ingredient> ingredientsList;
    private Ingredient ingredient;
    private LinearLayoutManager linearLayoutManager;
    private TextView categoryTextView;
    private String category, selection, sortOrder;
    private CupboardDbHelper dbHelper;
    private SQLiteDatabase db;
    private String[] projection, selectionArgs;
    private Cursor cursor;

    public DetailCupboardFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_detail_cupboard, container, false);

        // Inflate the layout for this fragment
        constraintLayout = view.findViewById(R.id.cl_cupboard_detail);

        Bundle bundle = getArguments();

        category = bundle.getString("Shared Element");

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            constraintLayout.setTransitionName(category);
        }

        categoryTextView = view.findViewById(R.id.tv_category);
        imageView = view.findViewById(R.id.iv_collapse);
        recyclerView = view.findViewById(R.id.rv_cupboard_detail);
        fab = view.findViewById(R.id.fab_add);
        linearLayoutManager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(linearLayoutManager);
        ingredientsList = new ArrayList<>();
        categoryTextView.setText(category);
        dbHelper = new CupboardDbHelper(getContext());
        db = dbHelper.getReadableDatabase();

        ((MainActivity)getActivity()).updateSearchListener(DetailCupboardFragment.this);

        getIngredientData("");

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getActivity().onBackPressed();
            }
        });

        return view;
    }

    public void getIngredientData(String query) {
        ingredientsList.clear();

        projection = new String[]{
                CupboardContract.Ingredients.COLUMN_NAME,
                CupboardContract.Ingredients.COLUMN_QUANTITY,
                CupboardContract.Ingredients.COLUMN_UNIT,
                CupboardContract.Ingredients.COLUMN_CATEGORY
        };

        sortOrder = CupboardContract.Ingredients.COLUMN_NAME;

        if(category.equals("All Ingredients")) {
            selection = null;
            selectionArgs = null;
            if(!query.equals("")) {
                selection = CupboardContract.Ingredients.COLUMN_NAME + " LIKE ?";
                selectionArgs = new String[]{"%" + query + "%"};
            }
        } else {
            selection = CupboardContract.Ingredients.COLUMN_CATEGORY + " = ?";
            selectionArgs = new String[]{category};
            if(!query.equals("")) {
                selection = CupboardContract.Ingredients.COLUMN_CATEGORY + " = ? AND " +
                        CupboardContract.Ingredients.COLUMN_NAME + " LIKE ?";
                selectionArgs = new String[]{category, "%" + query + "%"};
            }
        }

        cursor = db.query(
                CupboardContract.Ingredients.TABLE_NAME,
                projection,
                selection,
                selectionArgs,
                null,
                null,
                sortOrder
        );

        while(cursor.moveToNext()) {
            ingredient = new Ingredient();
            ingredient.name = cursor.getString(cursor.getColumnIndex(CupboardContract.Ingredients.COLUMN_NAME));
            ingredient.quantity = cursor.getInt(cursor.getColumnIndex(CupboardContract.Ingredients.COLUMN_QUANTITY));
            ingredient.unit = cursor.getString(cursor.getColumnIndex(CupboardContract.Ingredients.COLUMN_UNIT));
            ingredient.category = cursor.getString(cursor.getColumnIndex(CupboardContract.Ingredients.COLUMN_CATEGORY));
            ingredientsList.add(ingredient);
        }
        cursor.close();

        adapter = new CupboardDetailAdapter(category, ingredientsList);
        recyclerView.setAdapter(adapter);
    }

    @Override
    public void onSearch(String query) {
        getIngredientData(query);
    }
}
