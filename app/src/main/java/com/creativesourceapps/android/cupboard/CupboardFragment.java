package com.creativesourceapps.android.cupboard;

import android.os.Build;
import android.os.Bundle;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import androidx.fragment.app.Fragment;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
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
        layoutManager = new GridLayoutManager(getContext(), getResources().getInteger(R.integer.cupboard_column_count));

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
            fab.setTransitionName(getString(R.string.add_ingredient));
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

        categoryList.add(getString(R.string.all_ingredients));
        categoryList.add(getString(R.string.meat_seafood));
        categoryList.add(getString(R.string.pasta_rice));
        categoryList.add(getString(R.string.fruits_vegetables));
        categoryList.add(getString(R.string.dairy_eggs));
        categoryList.add(getString(R.string.snacks_candy));
        categoryList.add(getString(R.string.bread_grains));
        categoryList.add(getString(R.string.spices_baking));
        categoryList.add(getString(R.string.spirits_beverages));
        categoryList.add(getString(R.string.sauces_vinegar));
        categoryList.add(getString(R.string.soups_canned));

        adapter = new CupboardAdapter(categoryList, CupboardFragment.this);

        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(layoutManager);

    }

    public void startFragmentTransaction(int clickedItem, View view, Object tag) {

        Bundle bundle = new Bundle();

        if (clickedItem == -1) {
            bundle.putString("Shared Element", getString(R.string.add_ingredient));
            fragment = new IngredientAddFragment();
            MainActivity.restoreFragment = fragment;
            fragment.setSharedElementReturnTransition(null);
        }
        else {
            bundle.putString("Shared Element", categoryList.get(clickedItem));
            bundle.putString("Image Id",(String) tag);
        }

        fragment.setArguments(bundle);
        MainActivity.restoreFragment = fragment;

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
        startFragmentTransaction(0, recyclerView.findViewHolderForAdapterPosition(1).itemView, "ingredients");
    }

    @Override
    public void onResume() {
        super.onResume();
        ((MainActivity)getActivity()).setFloatingSearchView(getString(R.string.app_name));
        MainActivity.restoreFragment = this;
    }
}
