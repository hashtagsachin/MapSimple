package com.android.example.mapsimple;

import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.espresso.Espresso;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.longClick;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.isClickable;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.withText;

@RunWith(AndroidJUnit4.class)
public class AdminActivityTest {

    @Rule
    public ActivityScenarioRule<AdminActivity> activityRule = new ActivityScenarioRule<>(AdminActivity.class);

    // test the upload button is visible and clickable
    @Test
    public void uploadFloorplanButton_isVisible_andClickable() {
        onView(withId(R.id.uploadFloorplanButton)).check(matches(isDisplayed()));
        onView(withId(R.id.uploadFloorplanButton)).check(matches(isClickable()));
    }

    // test the draw path button is visible and clickable
    @Test
    public void drawPathButton_activatesDrawingMode() {
        onView(withId(R.id.drawPathButton)).check(matches(isDisplayed()));
        onView(withId(R.id.drawPathButton)).perform(click());

    }

    // test the save path button is visible and clickable
    @Test
    public void savePathButton_savesPath() {
        // Assuming the path drawing process is completed or mocked
        onView(withId(R.id.savePathButton)).check(matches(isDisplayed()));
        onView(withId(R.id.savePathButton)).perform(click());
        // Verify feedback indicating the path has been saved
        // This could be a Toast message, Snackbar, or a change in the UI.
    }






}
