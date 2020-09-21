package com.creativesourceapps.android.cupboard.ui.view;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;

import com.creativesourceapps.android.cupboard.data.CupboardContract;
import com.creativesourceapps.android.cupboard.data.CupboardDbHelper;
import com.creativesourceapps.android.cupboard.R;
import com.creativesourceapps.android.cupboard.data.model.Ingredient;
import com.creativesourceapps.android.cupboard.ui.adapter.IngredientListAdapter;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;


public class DetailCupboardFragment extends Fragment implements MainActivity.SearchChangeListener, IngredientListAdapter.ItemClickListener {
    @BindView(R.id.rv_cupboard_detail) RecyclerView recyclerView;
    IngredientListAdapter adapter;
    @BindView(R.id.iv_collapse) ImageView imageView;
    @BindView(R.id.fab_add) FloatingActionButton fab;
    private ArrayList<Ingredient> ingredientsList;
    private Ingredient ingredient;
    private GridLayoutManager gridLayoutManager;
    @BindView(R.id.tv_category) TextView categoryTextView;
    @BindView(R.id.tv_empty_message) TextView emptyTextView;
    private String category, selection, sortOrder, message, isEmpty;
    private CupboardDbHelper dbHelper;
    private SQLiteDatabase db;
    private String[] projection, selectionArgs;
    private Cursor cursor;
    private IngredientAddFragment fragment;
    private FragmentManager fragmentManager;
    @BindView(R.id.pb) ProgressBar pb;

    public DetailCupboardFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_detail_cupboard, container, false);
        ButterKnife.bind(this, view);

        Bundle bundle = getArguments();

        category = bundle.getString("Shared Element");

        gridLayoutManager = new GridLayoutManager(getContext(), getResources().getInteger(R.integer.ingredient_column_count));
        recyclerView.setLayoutManager(gridLayoutManager);
        ingredientsList = new ArrayList<>();
        categoryTextView.setText(category);
        dbHelper = new CupboardDbHelper(getContext());
        db = dbHelper.getReadableDatabase();
        isEmpty = getString(R.string.is_empty);

        ((MainActivity)getActivity()).updateSearchListener(DetailCupboardFragment.this);

        getIngredientData("");

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                fragment = new IngredientAddFragment();
                MainActivity.restoreFragment = fragment;
                fragmentManager = getActivity().getSupportFragmentManager();
                fragmentManager.beginTransaction().addToBackStack(null)
                        .replace(R.id.fl_fragment, fragment).commit();
            }
        });

        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((MainActivity)getActivity()).clearSearchFocus();
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

        if(category.equals(getString(R.string.all_ingredients))) {
            message = getString(R.string.app_name) + " " + isEmpty;
            emptyTextView.setText(message);
            selection = null;
            selectionArgs = null;
            if(!query.equals("")) {
                selection = CupboardContract.Ingredients.COLUMN_NAME + " LIKE ?";
                selectionArgs = new String[]{"%" + query + "%"};
            }
        } else {
            message = category + "\n" + isEmpty;
            emptyTextView.setText(message);
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
            ingredient.quantity = String.format(Locale.US, "%.2f",cursor.getDouble(cursor.getColumnIndex(CupboardContract.Ingredients.COLUMN_QUANTITY)));
            ingredient.unit = cursor.getString(cursor.getColumnIndex(CupboardContract.Ingredients.COLUMN_UNIT));
            ingredient.category = cursor.getString(cursor.getColumnIndex(CupboardContract.Ingredients.COLUMN_CATEGORY));
            ingredientsList.add(ingredient);
        }
        cursor.close();

        if(ingredientsList.size() == 0)
            pb.setVisibility(View.GONE);
        else {
            emptyTextView.setVisibility(View.GONE);
            pb.setVisibility(View.GONE);
            adapter = new IngredientListAdapter(getContext(), false, ingredientsList, DetailCupboardFragment.this);
            recyclerView.setAdapter(adapter);
        }
    }

    @Override
    public void onSearch(String query) {
        getIngredientData(query);
    }

    @Override
    public void onItemClickListener(int index, String name, String quantity, String unit, String category, boolean availability) {
        Bundle bundle = new Bundle();
        bundle.putString("type", "edit");
        bundle.putString("name", name);
        bundle.putString("quantity", quantity);
        bundle.putString("unit", unit);

        fragment = new IngredientAddFragment();
        MainActivity.restoreFragment = fragment;
        fragment.setArguments(bundle);
        fragmentManager = getActivity().getSupportFragmentManager();
        fragmentManager.beginTransaction().addToBackStack(null)
                .replace(R.id.fl_fragment, fragment).commit();
    }

    @Override
    public void onResume() {
        super.onResume();
        MainActivity.restoreFragment = this;
    }
}
