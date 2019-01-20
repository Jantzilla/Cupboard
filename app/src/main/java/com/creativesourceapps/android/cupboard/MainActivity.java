package com.creativesourceapps.android.cupboard;

import android.animation.ValueAnimator;
import android.graphics.Color;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.graphics.drawable.DrawerArrowDrawable;
import android.support.v7.view.menu.MenuItemImpl;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;

import com.arlib.floatingsearchview.FloatingSearchView;

public class MainActivity extends AppCompatActivity {

    private FloatingSearchView floatingSearchView;
    private DrawerLayout drawerLayout;
    private FragmentManager fragmentManager;
    private View view;
    private Fragment fragment;
    private NavigationView navigationView;
    private SearchChangeListener searchChangeListener;
    private Recipe recipe;
    private MenuItemImpl menuElement;
    private FrameLayout recipes, cupboard, cookbook, groceries;
    private ActionBarDrawerToggle mDrawerToggle;

    public interface SearchChangeListener {
        void onSearch(String query);
    }

    public void updateSearchListener(SearchChangeListener searchChangeListener) {
        this.searchChangeListener = searchChangeListener;
    }

    public void setFloatingSearchViewTitle(String title) {
        if(floatingSearchView.getCurrentMenuItems().size() != 0)
            menuElement = floatingSearchView.getCurrentMenuItems().get(0);

        floatingSearchView.setLeftActionMode(FloatingSearchView.LEFT_ACTION_MODE_SHOW_HOME);
        if(title != null)
            floatingSearchView.setSearchBarTitle(title);
        floatingSearchView.getCurrentMenuItems().clear();
        floatingSearchView.setSearchFocused(true);
        floatingSearchView.setSearchFocused(false);
        floatingSearchView.setSearchFocusable(false);
    }

    public void setScrimVisibility(boolean visible){
        if(visible)
            view.setVisibility(View.VISIBLE);
        else
            view.setVisibility(View.GONE);
    }

    public void setFloatingSearchView(String title) {
        floatingSearchView.attachNavigationDrawerToMenuButton(drawerLayout);

        if(menuElement != null) {
            floatingSearchView.getCurrentMenuItems().add(menuElement);
            floatingSearchView.setSearchFocusable(true);
            floatingSearchView.setSearchFocused(true);
            floatingSearchView.setSearchFocused(false);
        }

        floatingSearchView.setSearchBarTitle(title);

        floatingSearchView.setLeftActionMode(FloatingSearchView.LEFT_ACTION_MODE_SHOW_HAMBURGER);

        floatingSearchView.setOnQueryChangeListener(new FloatingSearchView.OnQueryChangeListener() {
            @Override
            public void onSearchTextChanged(String oldQuery, String newQuery) {
                searchChangeListener.onSearch(newQuery);
            }
        });

        floatingSearchView.setOnHomeActionClickListener(new FloatingSearchView.OnHomeActionClickListener() {
            @Override
            public void onHomeClicked() {
                onBackPressed();
            }
        });

        floatingSearchView.setOnMenuItemClickListener(new FloatingSearchView.OnMenuItemClickListener() {
            @Override
            public void onActionMenuItemSelected(MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.id_search:
                        floatingSearchView.setSearchFocused(true);
                        break;
                }
            }
        });
    }

    public void setRecipe(Recipe recipe) {
        this.recipe = recipe;
    }

    public Recipe getRecipe() {
        return recipe;
    }

    public void useIngredients(int index) {
        if(index == -1) {
            for(int i = 0; i < recipe.used.size(); i++) {
                recipe.used.set(i, true);
            }
        } else {
            recipe.used.set(index, true);
            recipe.ingredientsUsed = 1;
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                drawerLayout.openDrawer(GravityCompat.START);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        floatingSearchView = findViewById(R.id.floating_search_view);
        drawerLayout = findViewById(R.id.drawer_layout);
        navigationView = findViewById(R.id.navigation);
        view = findViewById(R.id.transparent_scrim);
        recipes = findViewById(R.id.fl_recipes);
        cupboard = findViewById(R.id.fl_cupboard);
        cookbook = findViewById(R.id.fl_cookbook);
        groceries = findViewById(R.id.fl_groceries);
        fragmentManager = getSupportFragmentManager();
        fragment = new CookbookFragment();                         //TODO: Change back to RecipeFragment()

        setFloatingSearchView("Cookbook");

        fragmentManager.beginTransaction().add(R.id.fl_fragment, fragment).commit();

        final DrawerArrowDrawable drawable = new DrawerArrowDrawable(this);
        drawable.setColor(Color.WHITE);
        final FloatingActionButton floatingActionButton = (FloatingActionButton) findViewById(R.id.fab_close);
        floatingActionButton.setImageDrawable(drawable);

        final ValueAnimator animator = ValueAnimator.ofFloat(0f, 1f);
        animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override public void onAnimationUpdate(ValueAnimator animation) {
                drawable.setProgress((Float)animation.getAnimatedValue());
                floatingActionButton.setTranslationX((Float)animation.getAnimatedValue() * (-700));
            }
        });

        mDrawerToggle = new ActionBarDrawerToggle(
                this,
                drawerLayout,
                null,
                0,
                0
        ) {

            @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP_MR1)
            @Override
            public void onDrawerSlide(View drawerView, float slideOffset) {
                animator.setCurrentFraction(slideOffset);
                super.onDrawerSlide(drawerView, slideOffset);
            }
        };
        drawerLayout.setDrawerListener(mDrawerToggle);

        floatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                drawerLayout.closeDrawers();
            }
        });

        recipes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                fragment = new RecipeFragment();
                fragmentManager.beginTransaction().replace(R.id.fl_fragment, fragment).commit();
                floatingSearchView.setSearchBarTitle("Recipes");
                drawerLayout.closeDrawers();
            }
        });
        cupboard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                fragment = new CupboardFragment();
                fragmentManager.beginTransaction().replace(R.id.fl_fragment, fragment).commit();
                floatingSearchView.setSearchBarTitle("Cupboard");
                drawerLayout.closeDrawers();
            }
        });
        cookbook.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                fragment = new CookbookFragment();
                fragmentManager.beginTransaction().replace(R.id.fl_fragment, fragment).commit();
                floatingSearchView.setSearchBarTitle("Cookbook");
                drawerLayout.closeDrawers();
            }
        });
        groceries.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                fragment = new GroceriesFragment();
                fragmentManager.beginTransaction().replace(R.id.fl_fragment, fragment).commit();
                floatingSearchView.setSearchBarTitle("Groceries");
                drawerLayout.closeDrawers();
            }
        });

    }

}
