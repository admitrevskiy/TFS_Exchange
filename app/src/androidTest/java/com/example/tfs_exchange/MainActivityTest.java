package com.example.tfs_exchange;

import android.os.RemoteException;
import android.support.test.espresso.contrib.RecyclerViewActions;
import android.support.test.rule.ActivityTestRule;
import android.support.test.uiautomator.UiDevice;
import android.util.Log;

import org.junit.Rule;
import org.junit.Test;

import static android.support.test.InstrumentationRegistry.getInstrumentation;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.action.ViewActions.pressBack;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.Espresso.onView;
import static org.junit.Assert.*;

/**
 * Created by pusya on 11.12.17.
 */
public class MainActivityTest {

    private static final String TAG = "MainActivityTest";

    UiDevice device = UiDevice.getInstance(getInstrumentation());

    @Rule
    public ActivityTestRule<MainActivity> activityTestRule = new ActivityTestRule<>(MainActivity.class);

    @Test
    public void currencyRecyclerTest() {
        try {
            Thread.sleep(200);
            onView(withId(R.id.currency_recycler_view)).check(matches(isDisplayed()));
        } catch (InterruptedException e) {
            Log.d(TAG, e.getMessage());
        }
    }

    @Test
    public void bottomMenuTest() {
        try {
            Thread.sleep(200);
            onView(withId(R.id.bottom_navigation)).check(matches(isDisplayed()));
        } catch (InterruptedException e) {
            Log.d(TAG, e.getMessage());
        }
    }

    @Test
    public void historyTest() {
        try {
            Thread.sleep(200);
            onView(withId(R.id.history)).perform(click());
            onView(withId(R.id.history_recycler_view)).check(matches(isDisplayed()));
            onView(withId(R.id.choose_filter)).check(matches(isDisplayed()));
            onView(withId(R.id.filter_text_view)).check(matches(isDisplayed()));
        } catch (InterruptedException e) {
            Log.d(TAG, e.getMessage());
        }
    }

    @Test
    public void historyFilterTest() {
        try {
            Thread.sleep(200);
            onView(withId(R.id.history)).perform(click());
            onView(withId(R.id.choose_filter)).perform(click());
            onView(withId(R.id.period_spinner)).check(matches(isDisplayed()));
            onView(withId(R.id.period_text_view)).check(matches(isDisplayed()));
            onView(withId(R.id.filter_recycler_view)).check(matches(isDisplayed()));
        } catch (InterruptedException e) {
            Log.d(TAG, e.getMessage());
        }
    }

    @Test
    public void analyticsTest() {
        try {
            Thread.sleep(200);
            onView(withId(R.id.analytics)).perform(click());
            onView(withId(R.id.graph)).check(matches(isDisplayed()));
            onView(withId(R.id.analytics_recycler)).check(matches(isDisplayed()));
            onView(withId(R.id.choose_period)).check(matches(isDisplayed()));
        } catch (InterruptedException e) {
            Log.d(TAG, e.getMessage());
        }
    }

    @Test
    public void exchangeTest() {
        try {
            Thread.sleep(200);
            onView(withId(R.id.exchange)).perform(click());
            onView(withId(R.id.currency_recycler_view)).perform(RecyclerViewActions.actionOnItemAtPosition(2, click()));
            onView(withId(R.id.currency_from_name)).check(matches(isDisplayed()));
            onView(withId(R.id.currency_to_name)).check(matches(isDisplayed()));
            onView(withId(R.id.currency_from_edit)).check(matches(isDisplayed()));
            onView(withId(R.id.currency_to_edit)).check(matches(isDisplayed()));
        } catch (InterruptedException e) {
            Log.d(TAG, e.getMessage());
        }
    }

    @Test
    public void exchangeBackPressTest() {
        try {
            Thread.sleep(200);
            onView(withId(R.id.exchange)).perform(click());
            onView(withId(R.id.currency_recycler_view)).perform(RecyclerViewActions.actionOnItemAtPosition(2, click()));
            pressBack();
            onView(withId(R.id.bottom_navigation)).check(matches(isDisplayed()));
        } catch (InterruptedException e) {
            Log.d(TAG, e.getMessage());
        }
    }

    @Test
    public void historyFilterBackPressTest() {
        try {
            Thread.sleep(200);
            onView(withId(R.id.history)).perform(click());
            onView(withId(R.id.choose_filter)).perform(click());
            pressBack();
            onView(withId(R.id.history)).perform(click());
            onView(withId(R.id.history_recycler_view)).check(matches(isDisplayed()));
            onView(withId(R.id.choose_filter)).check(matches(isDisplayed()));
            onView(withId(R.id.filter_text_view)).check(matches(isDisplayed()));
        } catch (InterruptedException e) {
            Log.d(TAG, e.getMessage());
        }
    }

    @Test
    public void historyRotationTest() {
        try {
            onView(withId(R.id.history)).perform(click());
            device.setOrientationLeft();
            onView(withId(R.id.history_recycler_view)).check(matches(isDisplayed()));
            onView(withId(R.id.choose_filter)).check(matches(isDisplayed()));
            onView(withId(R.id.filter_text_view)).check(matches(isDisplayed()));
            device.setOrientationNatural();
        } catch (RemoteException e) {
            Log.d(TAG, e.getMessage());
        }
    }

    @Test
    public void exchangeDoubleRotationTest() {
        try {
            onView(withId(R.id.exchange)).perform(click());
            onView(withId(R.id.currency_recycler_view)).perform(RecyclerViewActions.actionOnItemAtPosition(2, click()));
            device.setOrientationRight();
            onView(withId(R.id.currency_from_name)).check(matches(isDisplayed()));
            onView(withId(R.id.currency_to_name)).check(matches(isDisplayed()));
            onView(withId(R.id.currency_from_edit)).check(matches(isDisplayed()));
            onView(withId(R.id.currency_to_edit)).check(matches(isDisplayed()));
            device.setOrientationNatural();
            onView(withId(R.id.currency_from_name)).check(matches(isDisplayed()));
            onView(withId(R.id.currency_to_name)).check(matches(isDisplayed()));
            onView(withId(R.id.currency_from_edit)).check(matches(isDisplayed()));
            onView(withId(R.id.currency_to_edit)).check(matches(isDisplayed()));
        } catch (RemoteException e) {
            Log.d(TAG, e.getMessage());
        }
    }

}