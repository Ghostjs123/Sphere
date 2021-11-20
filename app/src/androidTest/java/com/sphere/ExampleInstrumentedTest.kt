package com.sphere

import android.os.Bundle
import androidx.core.os.bundleOf
import androidx.fragment.app.testing.FragmentScenario
import androidx.fragment.app.testing.launchFragmentInContainer
import androidx.fragment.app.testing.withFragment
import androidx.lifecycle.Lifecycle
import androidx.test.core.app.ActivityScenario
import androidx.test.platform.app.InstrumentationRegistry
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.sphere.menu_fragments.NewSphereFragment
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.Assert.*
import org.junit.Before
import org.mockito.Mock


@RunWith(AndroidJUnit4::class)
class ExampleInstrumentedTest {

    private lateinit var scenario: FragmentScenario<NewSphereFragment>

    private fun updateSphereCallback(sphereName: String, seed: Long?, subdivision: Int) {

    }

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
//        val appContext = InstrumentationRegistry.getInstrumentation().targetContext

        scenario.onFragment { fragment ->
            // setup state
            fragment.binding.radioNew2.isChecked = false
            fragment.binding.radioNew3.isChecked = true
            fragment.binding.radioNew4.isChecked = false
            fragment.binding.radioNew5.isChecked = true

            // simulate press
            fragment.binding.radioNew1.isChecked = true
            fragment.onRadioButtonClicked(fragment.binding.radioNew1)

            assert(fragment.binding.radioNew1.isChecked)
            assert(!fragment.binding.radioNew2.isChecked)
            assert(!fragment.binding.radioNew3.isChecked)
            assert(!fragment.binding.radioNew4.isChecked)
            assert(!fragment.binding.radioNew5.isChecked)
        }
    }
}