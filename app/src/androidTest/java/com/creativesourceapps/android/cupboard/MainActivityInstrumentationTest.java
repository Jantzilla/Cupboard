package com.creativesourceapps.android.cupboard;

import androidx.test.rule.ActivityTestRule;

import com.creativesourceapps.android.cupboard.ui.view.MainActivity;

import org.junit.Rule;
import org.junit.Test;

import static androidx.test.espresso.Espresso.onData;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static org.hamcrest.CoreMatchers.anything;

// Tests for MainActivity
public class MainActivityInstrumentationTest {

    // Preferred JUnit 4 mechanism of specifying the activity to be launched before each test
    @Rule
    public ActivityTestRule<MainActivity> activityTestRule =
            new ActivityTestRule<>(MainActivity.class);

    @Test
    public void clickOnItemAtPositionTwo() {

        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        // Directly specify the position in the adapter to click on
        onData(anything()) // We are using the position so don't need to specify a data matcher
                .inAdapterView(withId(R.id.recipes_grid_view)) // Specify the explicit id of the GridView
                .atPosition(1) // Explicitly specify the adapter item to use
                .perform(click()); // Standard ViewAction
    }

    @Test
    public void clickOnItemAtPositionThree() {

        // Directly specify the position in the adapter to click on
        onData(anything()) // We are using the position so don't need to specify a data matcher
                .inAdapterView(withId(R.id.recipes_grid_view)) // Specify the explicit id of the GridView
                .atPosition(2) // Explicitly specify the adapter item to use
                .perform(click()); // Standard ViewAction
    }
}
