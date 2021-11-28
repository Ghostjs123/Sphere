package com.sphere.menu_fragments

import androidx.core.os.bundleOf
import androidx.fragment.app.testing.FragmentScenario
import androidx.fragment.app.testing.launchFragmentInContainer
import androidx.lifecycle.Lifecycle
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.action.ViewActions.typeText
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.RootMatchers.withDecorView
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.ext.junit.rules.ActivityScenarioRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.sphere.R
import com.sphere.ToastMatcher
import org.hamcrest.CoreMatchers.`is`
import org.hamcrest.Matchers.not
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.Before
import org.junit.Rule


@RunWith(AndroidJUnit4::class)
class TestNewSphereFragment {

    private lateinit var scenario: FragmentScenario<NewSphereFragment>

    private fun updateSphereCallback(sphereName: String, seed: Long?, subdivision: Int) {}

    @Before
    fun setup() {
        val args = bundleOf(
            "callback" to ::updateSphereCallback
        )

        scenario = launchFragmentInContainer(
            fragmentArgs = args,
            initialState = Lifecycle.State.RESUMED,
            factory = null
        )
    }

    @Test
    fun newSphereFragment_TestRadioButtons() {
        scenario.onFragment { fragment ->
            // setup state
            fragment.binding.radioNew2.isChecked = false
            fragment.binding.radioNew3.isChecked = true
            fragment.binding.radioNew4.isChecked = false
            fragment.binding.radioNew5.isChecked = true

            // simulate press
            fragment.binding.radioNew1.isChecked = true
            fragment.onRadioButtonClicked(fragment.binding.radioNew1)

            // assert
            assert(fragment.binding.radioNew1.isChecked)
            assert(!fragment.binding.radioNew2.isChecked)
            assert(!fragment.binding.radioNew3.isChecked)
            assert(!fragment.binding.radioNew4.isChecked)
            assert(!fragment.binding.radioNew5.isChecked)
        }
    }

    @Test
    fun newSphereFragment_TestCreateSphereButton_DoesNotAcceptEmptyString() {
        scenario.onFragment { fragment ->
            // setup state
            fragment.onRadioButtonClicked(fragment.binding.radioNew1)

            // simulate press
            fragment.binding.createSphereButton.performClick()

            // assert
            onView(withText(R.string.enter_name))
                .inRoot(ToastMatcher())
                .check(matches(isDisplayed()))
        }
    }

    @Test
    fun newSphereFragment_TestCreateSphereButton_DoesNotAcceptNoSubdivisions() {
        scenario.onFragment { fragment ->
            // setup state
            onView(withId(R.id.enter_sphere_name_text)).perform(typeText("asd"))

            // simulate press
            fragment.binding.createSphereButton.performClick()

            // assert
            onView(withText(R.string.enter_subs)).inRoot(withDecorView(not(`is`(fragment.activity?.window?.decorView)))).check(matches(isDisplayed()))
        }
    }
}