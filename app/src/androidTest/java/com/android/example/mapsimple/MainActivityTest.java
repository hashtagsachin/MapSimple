package com.android.example.mapsimple;


//import androidx.test.espresso.contrib.DrawerActions;
import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.espresso.Espresso;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static androidx.test.espresso.Espresso.onData;
import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isClickable;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withSpinnerText;
import static org.hamcrest.CoreMatchers.anything;
import static org.hamcrest.CoreMatchers.containsString;

@RunWith(AndroidJUnit4.class)
public class MainActivityTest {

    @Rule
    public ActivityScenarioRule<MainActivity> activityRule = new ActivityScenarioRule<>(MainActivity.class);

    //check if the navigate button is visible and clickable
    @Test
    public void navigateButton_isVisible_andClickable() {
        onView(withId(R.id.navigateButton)).check(matches(isDisplayed()));
        onView(withId(R.id.navigateButton)).check(matches(isClickable()));
    }

    //check if the startSpinner is visible and clickable
    @Test
    public void startLocationSpinner_displaysOptions() {
        onView(withId(R.id.startSpinner)).perform(click());
        onData(anything()).atPosition(0).perform(click());
      //  onView(withId(R.id.startSpinner)).check(matches(withSpinnerText(containsString("YourFirstLocation"))));
    }


    @Test
    public void endLocationSpinner_displaysOptions() {
        onView(withId(R.id.endSpinner)).perform(click());
        onData(anything()).atPosition(0).perform(click());

    }

    //check if the floorplan view is visible
    @Test
    public void floorplanView_isVisible() {
        onView(withId(R.id.floorplanView)).check(matches(isDisplayed()));
    }

//    @Test
//    public void navigationDrawer_opensCorrectly() {
//        onView(withId(R.id.drawer_layout)).perform(DrawerActions.open());
//        onView(withId(R.id.nav_view)).check(matches(isDisplayed()));
//    }



}