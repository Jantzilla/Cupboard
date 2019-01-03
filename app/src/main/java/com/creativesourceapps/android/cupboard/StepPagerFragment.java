package com.creativesourceapps.android.cupboard;

import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.transition.Transition;
import com.tbuonomo.viewpagerdotsindicator.WormDotsIndicator;

public class StepPagerFragment extends Fragment {
    StepPagerAdapter stepPagerAdapter;
    FloatingActionButton fab;
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
        fab = view.findViewById(R.id.fab_recipe_ingredients);
        Recipe recipe = ((MainActivity)getActivity()).getRecipe();
        Glide.with(getContext()).load(recipe.media).into(new SimpleTarget<Drawable>() {
            @Override
            public void onResourceReady(Drawable resource, Transition<? super Drawable> transition) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                    viewPager.setBackground(resource);
                }
            }
        });
        stepPagerAdapter = new StepPagerAdapter(getActivity().getSupportFragmentManager());
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

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                IngredientFragment fragment = new IngredientFragment();
                FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
                fragmentManager.beginTransaction().replace(R.id.fl_fragment, fragment).commit();
            }
        });

        return view;
    }
}
