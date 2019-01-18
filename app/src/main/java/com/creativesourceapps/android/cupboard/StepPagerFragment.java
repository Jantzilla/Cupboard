package com.creativesourceapps.android.cupboard;

import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.transition.Slide;
import android.support.transition.TransitionManager;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.ViewPager;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.transition.Transition;
import com.tbuonomo.viewpagerdotsindicator.WormDotsIndicator;

import java.util.ArrayList;

public class StepPagerFragment extends Fragment {
    StepPagerAdapter stepPagerAdapter;
    FloatingActionButton fab;
    ViewPager viewPager;
    ImageView imageView;
    android.support.transition.Transition transition;
    WormDotsIndicator wormDotsIndicator;
    TextView stepTextView, recipeTextView;
    private String step;
    private ArrayList<Ingredient> ingredients;
    private boolean visible, isUsed;
    private Button useButton;
    private int mid;

    public StepPagerFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, final ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        final View view = inflater.inflate(R.layout.fragment_step_pager, container, false);
        viewPager = view.findViewById(R.id.vp_ingredient_steps);
        imageView = view.findViewById(R.id.iv_recipe_step_background);
        stepTextView = view.findViewById(R.id.tv_step_number);
        recipeTextView = view.findViewById(R.id.tv_recipe_title);
        fab = view.findViewById(R.id.fab_recipe_ingredients);
        useButton = view.findViewById(R.id.btn_use);
        ingredients = new ArrayList<>();
        transition = new Slide(Gravity.START);

        Recipe recipe = ((MainActivity)getActivity()).getRecipe();
        isUsed = recipe.ingredientsUsed == 1;
        mid = (recipe.steps.size() / 2) + 1;

        recipeTextView.setText(recipe.title);
        ((MainActivity)getActivity()).setFloatingSearchViewTitle(null);

        Glide.with(getContext()).load(recipe.media).into(new SimpleTarget<Drawable>() {
            @Override
            public void onResourceReady(Drawable resource, Transition<? super Drawable> transition) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                    imageView.setBackground(resource);
                }
            }
        });
        stepPagerAdapter = new StepPagerAdapter(getActivity().getSupportFragmentManager());
        wormDotsIndicator = view.findViewById(R.id.worm_dots_indicator);

        useButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                IngredientAddFragment.useAllIngredients(getContext(), ingredients);
                isUsed = true;
                TransitionManager.beginDelayedTransition(container, transition);
                useButton.setVisibility(View.GONE);
            }
        });

        for(int i = 0; i < recipe.instructions.size(); i ++) {
            Bundle bundle = new Bundle();
            bundle.putInt("position", i);
            DetailStepFragment detailStepFragment = new DetailStepFragment();
            detailStepFragment.setArguments(bundle);
            stepPagerAdapter.addFragment(detailStepFragment);
        }

        for(int i = 0; i < recipe.ingredients.size(); i++) {
            Ingredient ingredient = new Ingredient();
            ingredient.name = recipe.ingredients.get(i);
            ingredient.category = "Ingredients";
            ingredient.unit = recipe.unit.get(i);
            ingredient.quantity = recipe.quantity.get(i);
            ingredients.add(ingredient);
        }

        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int i, float v, int i1) {

            }

            @Override
            public void onPageSelected(int i) {
                step = "Step " + (i + 1);
                stepTextView.setText(step);
                if(i == mid && !isUsed) {
                    TransitionManager.beginDelayedTransition(container, transition);
                    visible = !visible;
                    useButton.setVisibility(visible ? View.VISIBLE : View.GONE);
                }
            }

            @Override
            public void onPageScrollStateChanged(int i) {

            }
        });

        viewPager.setAdapter(stepPagerAdapter);
        wormDotsIndicator.setViewPager(viewPager);

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                IngredientFragment fragment = new IngredientFragment();
                FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
                fragmentManager.beginTransaction()
                        .addToBackStack(null)
                        .replace(R.id.fl_fragment, fragment)
                        .commit();
            }
        });

        return view;
    }
}
