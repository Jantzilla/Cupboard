package com.creativesourceapps.android.cupboard;

import android.animation.ValueAnimator;
import android.app.Activity;
import android.graphics.Color;
import android.graphics.drawable.TransitionDrawable;
import android.os.Build;
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
import android.util.Log;
import android.util.TypedValue;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.FrameLayout;

import com.arlib.floatingsearchview.FloatingSearchView;
import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.security.ProviderInstaller;

public class MainActivity extends AppCompatActivity {

    private FloatingSearchView floatingSearchView;
    private DrawerLayout drawerLayout;
    private FragmentManager fragmentManager;
    private View view, navigation, cupboardView, cookbookView, recipesView, groceriesView;
    private Fragment fragment;
    public static Fragment restoreFragment;
    private NavigationView navigationView;
    private SearchChangeListener searchChangeListener;
    public static Recipe recipe;
    private MenuItemImpl menuElement;
    private FrameLayout recipes, cupboard, cookbook, groceries;
    private ActionBarDrawerToggle mDrawerToggle;
    private TransitionDrawable transition;
    public static int availableCount;

    public interface SearchChangeListener {
        void onSearch(String query);
    }

    public void updateSearchListener(SearchChangeListener searchChangeListener) {
        this.searchChangeListener = searchChangeListener;
    }

    public void setFloatingSearchViewTitle(String title) {
        if(floatingSearchView.getCurrentMenuItems() != null) {
            if (floatingSearchView.getCurrentMenuItems().size() != 0)
                menuElement = floatingSearchView.getCurrentMenuItems().get(0);

            floatingSearchView.setLeftActionMode(FloatingSearchView.LEFT_ACTION_MODE_SHOW_HOME);
            if (title != null)
                floatingSearchView.setSearchBarTitle(title);
            floatingSearchView.getCurrentMenuItems().clear();
            floatingSearchView.setSearchFocused(true);
            floatingSearchView.setSearchFocused(false);
            floatingSearchView.setSearchFocusable(false);
        }
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

    public void clearSearchFocus() {
        if(floatingSearchView.isSearchBarFocused())
            floatingSearchView.clearSearchFocus();
    }

    public void setRecipe(Recipe recipe) {
        MainActivity.recipe = recipe;
        availableCount = 0;
        for (int index : RecipeUtils.checkAvailable(this, recipe.ingredients, recipe.quantity, recipe.unit)) {
            MainActivity.recipe.available.set(index, true);
            availableCount++;
        }
    }

    public Recipe getRecipe() {
        return recipe;
    }

    public void useIngredients(int index) {
        recipe.used.set(index, true);
        availableCount--;

        if(availableCount == 0)
            MainActivity.recipe.ingredientsUsed = 1;
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

    private void updateAndroidSecurityProvider(Activity callingActivity) {
        try {
            ProviderInstaller.installIfNeeded(this);
        } catch (GooglePlayServicesRepairableException e) {
            // Thrown when Google Play Services is not installed, up-to-date, or enabled
            // Show dialog to allow users to install, update, or otherwise enable Google Play services.
            GooglePlayServicesUtil.getErrorDialog(e.getConnectionStatusCode(), callingActivity, 0);
        } catch (GooglePlayServicesNotAvailableException e) {
            Log.e("SecurityException", "Google Play Services not available.");
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        floatingSearchView = findViewById(R.id.floating_search_view);
        drawerLayout = findViewById(R.id.drawer_layout);
        navigationView = findViewById(R.id.navigation);
        cupboardView = findViewById(R.id.view_cupboard);
        cookbookView = findViewById(R.id.view_cookbook);
        recipesView = findViewById(R.id.view_recipes);
        groceriesView = findViewById(R.id.view_groceries);
        view = findViewById(R.id.transparent_scrim);
        recipes = findViewById(R.id.fl_recipes);
        cupboard = findViewById(R.id.fl_cupboard);
        cookbook = findViewById(R.id.fl_cookbook);
        groceries = findViewById(R.id.fl_groceries);
        fragmentManager = getSupportFragmentManager();
        fragment = new RecipeFragment();

        updateAndroidSecurityProvider(this);

        if (savedInstanceState != null) {
            //Restore the fragment's instance
            setFloatingSearchView("");
            animateNavigation(findViewById(savedInstanceState.getInt("navigationId")));

            if(restoreFragment != null) {
                fragment = restoreFragment;
                fragmentManager.popBackStackImmediate();
                fragmentManager.beginTransaction()
                        .replace(R.id.fl_fragment, fragment)
                        .addToBackStack(null)
                        .commit();
            }

        } else {
            setFloatingSearchView(getString(R.string.recipes));
            animateNavigation(recipesView);
            fragmentManager.beginTransaction().add(R.id.fl_fragment, fragment).commit();
        }

        final DrawerArrowDrawable drawable = new DrawerArrowDrawable(this);
        drawable.setColor(Color.WHITE);
        final FloatingActionButton floatingActionButton = (FloatingActionButton) findViewById(R.id.fab_close);
        floatingActionButton.setImageDrawable(drawable);

        final int value = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, getResources().getInteger(R.integer.button_translate),
                (this).getResources().getDisplayMetrics());

        final ValueAnimator animator = ValueAnimator.ofFloat(0f, 1f);
        animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override public void onAnimationUpdate(ValueAnimator animation) {
                drawable.setProgress((Float)animation.getAnimatedValue());
                floatingActionButton.setTranslationX((Float)animation.getAnimatedValue() * -(value));
            }
        });

        mDrawerToggle = new ActionBarDrawerToggle(
                this,
                drawerLayout,
                null,
                0,
                0
        ) {

            @Override
            public void onDrawerSlide(View drawerView, float slideOffset) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP_MR1) {
                    animator.setCurrentFraction(slideOffset);
                }
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
                animateNavigation(recipesView);
                drawerLayout.closeDrawers();
                fragment = new RecipeFragment();
                MainActivity.restoreFragment = fragment;
                fragmentManager.beginTransaction().replace(R.id.fl_fragment, fragment).commit();
                floatingSearchView.setSearchBarTitle(getString(R.string.recipes));
            }
        });
        cupboard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                animateNavigation(cupboardView);
                drawerLayout.closeDrawers();
                fragment = new CupboardFragment();
                MainActivity.restoreFragment = fragment;
                fragmentManager.beginTransaction().replace(R.id.fl_fragment, fragment).commit();
                floatingSearchView.setSearchBarTitle(getString(R.string.app_name));
            }
        });
        cookbook.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                animateNavigation(cookbookView);
                drawerLayout.closeDrawers();
                fragment = new CookbookFragment();
                MainActivity.restoreFragment = fragment;
                fragmentManager.beginTransaction().replace(R.id.fl_fragment, fragment).commit();
                floatingSearchView.setSearchBarTitle(getString(R.string.cookbook));
            }
        });
        groceries.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                animateNavigation(groceriesView);
                drawerLayout.closeDrawers();
                fragment = new GroceriesFragment();
                MainActivity.restoreFragment = fragment;
                fragmentManager.beginTransaction().replace(R.id.fl_fragment, fragment).commit();
                floatingSearchView.setSearchBarTitle(getString(R.string.groceries));
            }
        });

    }

    private void animateNavigation(View view) {

        if(navigation != null && !view.equals(navigation)) {
            transition = (TransitionDrawable) navigation.getBackground();
            transition.reverseTransition(300);
        }

        if(!view.equals(navigation)) {
            transition = (TransitionDrawable) view.getBackground();
            transition.startTransition(300);
        }

        navigation = view;
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt("navigationId", navigation.getId());
    }
}
