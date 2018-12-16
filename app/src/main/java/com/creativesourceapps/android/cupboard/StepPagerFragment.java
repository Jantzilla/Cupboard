package com.creativesourceapps.android.cupboard;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ExpandableListView;

public class StepPagerFragment extends Fragment {
    StepPagerAdapter stepPagerAdapter;
    ViewPager viewPager;
    private ExpandableListView expandableListView;
    private IngredientListAdapter adapter;

    public StepPagerFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_step_pager, container, false);
        viewPager = view.findViewById(R.id.vp_ingredient_steps);
        Recipe recipe = getActivity().getIntent().getParcelableExtra("parcel_data");
        stepPagerAdapter = new StepPagerAdapter(getActivity().getSupportFragmentManager());
        expandableListView = view.findViewById(R.id.step_list_view);
        adapter = new IngredientListAdapter(getContext(), "Ingredients", recipe.quantity, recipe.unit, recipe.ingredients);

        expandableListView.setAdapter(adapter);

        for(int i = 0; i < recipe.instructions.size(); i ++) {
            Bundle bundle = new Bundle();
            bundle.putInt("position", i);
            DetailStepFragment detailStepFragment = new DetailStepFragment();
            detailStepFragment.setArguments(bundle);
            stepPagerAdapter.addFragment(detailStepFragment);
        }

        viewPager.setAdapter(stepPagerAdapter);

        return view;
    }
}
