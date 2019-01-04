package com.creativesourceapps.android.cupboard;

import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.view.menu.MenuItemImpl;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import com.arlib.floatingsearchview.FloatingSearchView;

public class MainActivity extends AppCompatActivity {

    private FloatingSearchView floatingSearchView;
    private DrawerLayout drawerLayout;
    private FragmentManager fragmentManager;
    private Fragment fragment;
    private NavigationView navigationView;
    private SearchChangeListener searchChangeListener;
    private Recipe recipe;
    private MenuItemImpl menuElement;

    public interface SearchChangeListener {
        void onSearch(String query);
    }

    public void updateSearchListener(SearchChangeListener searchChangeListener) {
        this.searchChangeListener = searchChangeListener;
    }

    public void setFloatingSearchViewTitle(String title) {
        menuElement = floatingSearchView.getCurrentMenuItems().get(0);
        floatingSearchView.setLeftActionMode(FloatingSearchView.LEFT_ACTION_MODE_SHOW_HOME);
        floatingSearchView.setSearchBarTitle(title);
        floatingSearchView.getCurrentMenuItems().clear();
        floatingSearchView.setSearchFocused(true);
        floatingSearchView.setSearchFocused(false);
        floatingSearchView.setSearchFocusable(false);
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
        fragmentManager = getSupportFragmentManager();
        fragment = new CookbookFragment();                         //TODO: Change back to RecipeFragment()

        setFloatingSearchView("Cookbook");

        fragmentManager.beginTransaction().add(R.id.fl_fragment, fragment).commit();

        navigationView.setCheckedItem(R.id.id_recipes);

        navigationView.setNavigationItemSelectedListener(
                new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                switch (menuItem.getItemId()) {
                    case R.id.id_recipes:
                        fragment = new RecipeFragment();
                        fragmentManager.beginTransaction().replace(R.id.fl_fragment, fragment).commit();
                        floatingSearchView.setSearchBarTitle("Recipes");
                        break;
                    case R.id.id_cupboard:
                        fragment = new CupboardFragment();
                        fragmentManager.beginTransaction().replace(R.id.fl_fragment, fragment).commit();
                        floatingSearchView.setSearchBarTitle("Cupboard");
                        break;
                    case R.id.id_cookbook:
                        fragment = new CookbookFragment();
                        fragmentManager.beginTransaction().replace(R.id.fl_fragment, fragment).commit();
                        floatingSearchView.setSearchBarTitle("Cookbook");
                        break;
                    case R.id.id_groceries:
                        fragment = new GroceriesFragment();
                        fragmentManager.beginTransaction().replace(R.id.fl_fragment, fragment).commit();
                        floatingSearchView.setSearchBarTitle("Groceries");
                        break;
                    case R.id.id_settings:
                        fragment = new SettingsFragment();
                        fragmentManager.beginTransaction().replace(R.id.fl_fragment, fragment).commit();
                        floatingSearchView.setSearchBarTitle("Settings");
                }
                drawerLayout.closeDrawers();
                return true;
            }
        });
    }


}
