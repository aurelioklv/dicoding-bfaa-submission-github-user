package com.aurelioklv.githubuser

import androidx.test.core.app.ActivityScenario
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.isDisplayed
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.internal.runner.junit4.AndroidJUnit4ClassRunner
import com.aurelioklv.githubuser.ui.main.MainActivity
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4ClassRunner::class)
class MainActivityTest {
    @Before
    fun setup() {
        ActivityScenario.launch(MainActivity::class.java)
    }

    @Test
    fun assertViewDisplayed() {
        onView(withId(R.id.searchBar)).perform(click())
        onView(withId(R.id.searchView)).check(matches(isDisplayed()))

        onView(withId(R.id.menu_theme)).check(matches(isDisplayed()))
        onView(withId(R.id.menu_favorite)).check(matches(isDisplayed()))
    }
}