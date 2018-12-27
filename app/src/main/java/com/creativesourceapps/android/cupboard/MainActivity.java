package com.creativesourceapps.android.cupboard;

import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
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
        fragment = new RecipeFragment();

        floatingSearchView.attachNavigationDrawerToMenuButton(drawerLayout);

        fragmentManager.beginTransaction().add(R.id.fl_fragment, fragment).commit();

        navigationView.setNavigationItemSelectedListener(
                new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                switch (menuItem.getItemId()) {
                    case R.id.id_recipes:
                        fragment = new RecipeFragment();
                        fragmentManager.beginTransaction().replace(R.id.fl_fragment, fragment).commit();
                        break;
                    case R.id.id_cupboard:
                        fragment = new CupboardFragment();
                        fragmentManager.beginTransaction().replace(R.id.fl_fragment, fragment).commit();
                        break;
                    case R.id.id_cookbook:
                        fragment = new CookbookFragment();
                        fragmentManager.beginTransaction().replace(R.id.fl_fragment, fragment).commit();
                        break;
                }
                drawerLayout.closeDrawers();
                return true;
            }
        });
    }


}
