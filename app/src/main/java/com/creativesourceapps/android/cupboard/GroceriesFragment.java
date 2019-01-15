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

import com.makeramen.roundedimageview.RoundedImageView;

import java.util.ArrayList;


public class GroceriesFragment extends Fragment implements MainActivity.SearchChangeListener, IngredientListAdapter.ItemClickListener {
    RecyclerView recyclerView;
    IngredientListAdapter adapter;
    ImageView imageView;
    RoundedImageView roundedImageView;
    private FloatingActionButton fab;
    private ArrayList<Ingredient> ingredientsList;
    private Ingredient ingredient;
    private GridLayoutManager gridLayoutManager;
    private String sortOrder, selection;
    private CupboardDbHelper dbHelper;
    private SQLiteDatabase db;
    private String[] projection, selectionArgs;
    private Cursor cursor;
    private IngredientAddFragment fragment;
    private FragmentManager fragmentManager;

    public GroceriesFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_groceries, container, false);
        roundedImageView = view.findViewById(R.id.iv_category_background);
        recyclerView = view.findViewById(R.id.rv_groceries);
        fab = view.findViewById(R.id.fab_add);
        gridLayoutManager = new GridLayoutManager(getContext(), 3);
        recyclerView.setLayoutManager(gridLayoutManager);
        ingredientsList = new ArrayList<>();
        dbHelper = new CupboardDbHelper(getContext());
        db = dbHelper.getReadableDatabase();

        ((MainActivity)getActivity()).updateSearchListener(GroceriesFragment.this);

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

        return view;
    }

    public void getIngredientData(String query) {

    }

    @Override
    public void onSearch(String query) {
        getIngredientData(query);
    }

    @Override
    public void onItemClickListener(String name, String quantity, String unit) {
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
