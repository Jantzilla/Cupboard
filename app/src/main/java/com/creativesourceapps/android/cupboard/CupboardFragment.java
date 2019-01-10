package com.creativesourceapps.android.cupboard;

import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v7.widget.CardView;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.transition.Transition;
import android.transition.TransitionInflater;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;

public class CupboardFragment extends Fragment implements CupboardAdapter.ItemClickListener, MainActivity.SearchChangeListener {

    private RecyclerView recyclerView;
    private FloatingActionButton fab;
    private CupboardAdapter adapter;
    private ArrayList<String> categoryList;
    private GridLayoutManager layoutManager;
    private Fragment fragment;

    public CupboardFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_cupboard, container, false);
        recyclerView = view.findViewById(R.id.cupboard_grid_view);
        fab = view.findViewById(R.id.fab_ingredient_list);
        fragment = new DetailCupboardFragment();
        categoryList = new ArrayList<>();
        layoutManager = new GridLayoutManager(getContext(), 2);

        layoutManager.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
            @Override
            public int getSpanSize(int position) {
                if (position == 0) {
                    return 2;
                } else {
                    return 1;
                }
            }
        });

        getCategories();

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            fab.setTransitionName("Add Ingredient");
            setExitTransition(TransitionInflater.from(getContext())
                    .inflateTransition(R.transition.grid_exit_transition));
        }

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                startFragmentTransaction(-1, fab, null);

            }
        });

        ((MainActivity)getActivity()).updateSearchListener(CupboardFragment.this);

        return view;
    }

    private void getCategories() {

        categoryList.add("All Ingredients");
        categoryList.add("Meat & Seafood");
        categoryList.add("Pasta & Rice");
        categoryList.add("Fruits & Vegetables");
        categoryList.add("Dairy & Eggs");
        categoryList.add("Snacks & Candy");
        categoryList.add("Bread & Grains");
        categoryList.add("Spices & Baking");
        categoryList.add("Spirits & Beverages");
        categoryList.add("Sauces & Vinegar");
        categoryList.add("Soups & Canned");

        adapter = new CupboardAdapter(categoryList, CupboardFragment.this);

        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(layoutManager);

    }

    public void startFragmentTransaction(int clickedItem, View view, Object tag) {

            Bundle bundle = new Bundle();
            bundle.putInt("Image Id", (Integer) tag);
            if(clickedItem == -1) {
                bundle.putString("Shared Element", "Add Ingredient");
                fragment = new IngredientAddFragment();
                fragment.setSharedElementReturnTransition(null);
            }
            else
                bundle.putString("Shared Element", categoryList.get(clickedItem));
            fragment.setArguments(bundle);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Transition transition = TransitionInflater.from(getContext())
                    .inflateTransition(R.transition.cupboard_shared_element_transition);

            fragment.setSharedElementEnterTransition(transition);
            getActivity().getSupportFragmentManager()
                    .beginTransaction()
                    .addSharedElement(view, view.getTransitionName())
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

    @Override
    public void onItemClicked(int clickedItem, CardView itemLayout, Object tag) {

        startFragmentTransaction(clickedItem, itemLayout, tag);
    }

    @Override
    public void onSearch(String query) {
        startFragmentTransaction(0, recyclerView.findViewHolderForAdapterPosition(0).itemView, R.drawable.ingredients);
    }
}
