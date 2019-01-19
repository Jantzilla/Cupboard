package com.creativesourceapps.android.cupboard;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;


public class DetailCupboardFragment extends Fragment implements MainActivity.SearchChangeListener, IngredientListAdapter.ItemClickListener {
    RecyclerView recyclerView;
    IngredientListAdapter adapter;
    ImageView imageView;
    private FloatingActionButton fab;
    private ArrayList<Ingredient> ingredientsList;
    private Ingredient ingredient;
    private GridLayoutManager gridLayoutManager;
    private TextView categoryTextView;
    private String category, selection, sortOrder;
    private CupboardDbHelper dbHelper;
    private SQLiteDatabase db;
    private String[] projection, selectionArgs;
    private Cursor cursor;
    private IngredientAddFragment fragment;
    private FragmentManager fragmentManager;

    public DetailCupboardFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_detail_cupboard, container, false);

        Bundle bundle = getArguments();

        category = bundle.getString("Shared Element");

        categoryTextView = view.findViewById(R.id.tv_category);
        imageView = view.findViewById(R.id.iv_collapse);
        recyclerView = view.findViewById(R.id.rv_cupboard_detail);
        fab = view.findViewById(R.id.fab_add);
        gridLayoutManager = new GridLayoutManager(getContext(), 3);
        recyclerView.setLayoutManager(gridLayoutManager);
        ingredientsList = new ArrayList<>();
        categoryTextView.setText(category);
        dbHelper = new CupboardDbHelper(getContext());
        db = dbHelper.getReadableDatabase();

        ((MainActivity)getActivity()).updateSearchListener(DetailCupboardFragment.this);

        getIngredientData("");

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                fragment = new IngredientAddFragment();
                fragmentManager = getActivity().getSupportFragmentManager();
                fragmentManager.beginTransaction().addToBackStack(null)
                        .replace(R.id.fl_fragment, fragment).commit();
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
            ingredient.quantity = String.valueOf(cursor.getInt(cursor.getColumnIndex(CupboardContract.Ingredients.COLUMN_QUANTITY)));
            ingredient.unit = cursor.getString(cursor.getColumnIndex(CupboardContract.Ingredients.COLUMN_UNIT));
            ingredient.category = cursor.getString(cursor.getColumnIndex(CupboardContract.Ingredients.COLUMN_CATEGORY));
            ingredientsList.add(ingredient);
        }
        cursor.close();

        adapter = new IngredientListAdapter(getContext(), false, ingredientsList, DetailCupboardFragment.this);
        recyclerView.setAdapter(adapter);
    }

    @Override
    public void onSearch(String query) {
        getIngredientData(query);
    }

    @Override
    public void onItemClickListener(int index, String name, String quantity, String unit, boolean availability) {
        Bundle bundle = new Bundle();
        bundle.putString("type", "edit");
        bundle.putString("name", name);
        bundle.putString("quantity", quantity);
        bundle.putString("unit", unit);

        fragment = new IngredientAddFragment();
        fragment.setArguments(bundle);
        fragmentManager = getActivity().getSupportFragmentManager();
        fragmentManager.beginTransaction().addToBackStack(null)
                .replace(R.id.fl_fragment, fragment).commit();
    }
}
