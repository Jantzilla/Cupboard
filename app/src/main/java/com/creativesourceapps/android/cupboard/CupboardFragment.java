package com.creativesourceapps.android.cupboard;

import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import java.util.ArrayList;

public class CupboardFragment extends Fragment implements CupboardAdapter.ItemClickListener {

    private RecyclerView recyclerView;
    private CupboardAdapter adapter;
    private ArrayList<String> categoryList;
    private GridLayoutManager layoutManager;

    public CupboardFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_cupboard, container, false);
        recyclerView = view.findViewById(R.id.cupboard_grid_view);
        categoryList = new ArrayList<>();
        layoutManager = new GridLayoutManager(getContext(), 2);
        getCategories();

        return view;
    }

    private void getCategories() {

        categoryList.add("Seasoning");
        categoryList.add("Marinade");
        categoryList.add("Produce");
        categoryList.add("Fruit");
        categoryList.add("Vegetables");

        adapter = new CupboardAdapter(categoryList, CupboardFragment.this);

        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(layoutManager);

    }

    @Override
    public void onItemClicked(int clickedItem, LinearLayout itemLayout) {
        DetailCupboardFragment fragment = new DetailCupboardFragment();

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getActivity().getSupportFragmentManager()
                    .beginTransaction()
                    .setReorderingAllowed(true)
                    .addSharedElement(itemLayout, itemLayout.getTransitionName())
                    .replace(R.id.fl_fragment, fragment)
                    .addToBackStack(null)
                    .commit();
        } else {
            getActivity().getSupportFragmentManager()
                    .beginTransaction()
                    .setReorderingAllowed(true)
                    .replace(R.id.fl_fragment, fragment)
                    .addToBackStack(null)
                    .commit();
        }
    }
}
