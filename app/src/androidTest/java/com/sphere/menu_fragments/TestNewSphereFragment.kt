package com.sphere.menu_fragments

import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.ext.junit.rules.ActivityScenarioRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.sphere.R
import com.sphere.TestFragmentActivity
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import android.app.Activity
import androidx.test.core.app.ActivityScenario
import androidx.test.espresso.Espresso.closeSoftKeyboard
import androidx.test.espresso.action.ViewActions.typeText
import java.util.concurrent.atomic.AtomicReference


@RunWith(AndroidJUnit4::class)
class TestNewSphereFragment {

    private lateinit var mActivity: ActivityScenario<TestFragmentActivity>

    @Before
    fun setup() {
        mActivity = ActivityScenario.launch(TestFragmentActivity::class.java)
    }

    private fun <T : Activity?> getActivity(activityScenarioRule: ActivityScenarioRule<T>): T {
        val activityRef: AtomicReference<T> = AtomicReference()
        activityScenarioRule.scenario.onActivity(activityRef::set)
        return activityRef.get()
    }

    @Test
    fun newSphereFragment_TestRadioButtons() {
        mActivity.onActivity { it.replaceFragment(NewSphereFragment()) }

        // simulate press
        onView(withId(R.id.radio_new_1)).perform(click())

        // assert
        onView(withId(R.id.radio_new_1)).check(matches(isChecked()))
        onView(withId(R.id.radio_new_2)).check(matches(isNotChecked()))
        onView(withId(R.id.radio_new_3)).check(matches(isNotChecked()))
        onView(withId(R.id.radio_new_4)).check(matches(isNotChecked()))
        onView(withId(R.id.radio_new_5)).check(matches(isNotChecked()))
    }

    @Test
    fun newSphereFragment_TestCreateSphereButton_DoesNotAcceptEmptyString() {
        var before = 0

        mActivity.onActivity {
            it.replaceFragment(NewSphereFragment())
            before = it.supportFragmentManager.backStackEntryCount
        }

        onView(withId(R.id.radio_new_1)).perform(click())

        // simulate press
        onView(withId(R.id.create_sphere_button)).perform(click())

        // assert
        mActivity.onActivity { assert(it.supportFragmentManager.backStackEntryCount == before) }

    }

    @Test
    fun newSphereFragment_TestCreateSphereButton_DoesNotAcceptNoSubdivisions() {
        var before = 0

        mActivity.onActivity {
            it.replaceFragment(NewSphereFragment())
            before = it.supportFragmentManager.backStackEntryCount
        }

        onView(withId(R.id.sphere_name_input)).perform(typeText("asd"))
        closeSoftKeyboard()

        // simulate press
        onView(withId(R.id.create_sphere_button)).perform(click())

        // assert
        mActivity.onActivity { assert(it.supportFragmentManager.backStackEntryCount == before) }
    }
}