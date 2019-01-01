package com.creativesourceapps.android.cupboard;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.tbuonomo.viewpagerdotsindicator.WormDotsIndicator;

public class StepPagerFragment extends Fragment {
    int position;
    StepPagerAdapter stepPagerAdapter;
    ViewPager viewPager;
    WormDotsIndicator wormDotsIndicator;
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
        position = getArguments().getInt("position", 0);
        wormDotsIndicator = view.findViewById(R.id.worm_dots_indicator);
        adapter = new IngredientListAdapter(getContext(), "Ingredients", recipe.quantity, recipe.unit, recipe.ingredients);

        for(int i = 0; i < recipe.instructions.size(); i ++) {
            Bundle bundle = new Bundle();
            bundle.putInt("position", i);
            DetailStepFragment detailStepFragment = new DetailStepFragment();
            detailStepFragment.setArguments(bundle);
            stepPagerAdapter.addFragment(detailStepFragment);
        }

        viewPager.setAdapter(stepPagerAdapter);
        wormDotsIndicator.setViewPager(viewPager);

        viewPager.setCurrentItem(position,true);

        return view;
    }
}
