package com.creativesourceapps.android.cupboard;

import android.app.Dialog;
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
import android.widget.ImageView;

import java.util.ArrayList;


public class DetailCupboardFragment extends Fragment {
    ConstraintLayout constraintLayout;
    RecyclerView recyclerView;
    CupboardDetailAdapter adapter;
    ImageView imageView;
    private FloatingActionButton fab;
    private ArrayList<Ingredient> ingredientsList;
    private Ingredient ingredient;
    private LinearLayoutManager linearLayoutManager;
    private Dialog dialog;

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

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            constraintLayout.setTransitionName(bundle.getString("Shared Element"));
        }

        imageView = view.findViewById(R.id.iv_collapse);
        recyclerView = view.findViewById(R.id.rv_cupboard_detail);
        fab = view.findViewById(R.id.fab_add);
        linearLayoutManager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(linearLayoutManager);
        ingredientsList = new ArrayList<>();

        ingredient = new Ingredient(); // TODO: USE REAL INGREDIENT DATA
        ingredient.name = "Potatos";
        ingredient.quantity = 10;
        ingredientsList.add(ingredient);

        adapter = new CupboardDetailAdapter(ingredientsList);
        recyclerView.setAdapter(adapter);

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog = new Dialog(getContext());
                dialog.setContentView(R.layout.dialog_ingredient_edit);
                dialog.show();
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

}
