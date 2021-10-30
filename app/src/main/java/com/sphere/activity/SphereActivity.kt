package com.sphere.activity

import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.sphere.R
import com.sphere.SphereViewModel
import com.sphere.SphereViewModelFactory
import com.sphere.menu_fragments.NewSphereFragment
import com.sphere.sphere_fragments.SphereFragment
import com.sphere.room_code.SphereApplication
import com.sphere.room_code.SphereListAdapter

private const val TAG = "SphereActivity"


class SphereActivity : AppCompatActivity() {

    private val sphereViewModel: SphereViewModel by viewModels {
        SphereViewModelFactory((application as SphereApplication).repository)
    }

    private lateinit var sphereFragment: SphereFragment

    public override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        Log.i(TAG, "onCreate() Started")

        setContentView(R.layout.activity_sphere)

        val adapter = SphereListAdapter()
        sphereViewModel.allSpheres.observe(this) { spheres ->
            // Update the cached copy of the words in the adapter.
            spheres.let { adapter.submitList(it) }
        }

        sphereFragment = SphereFragment()
        supportFragmentManager.beginTransaction()
            .replace(R.id.sphere_fragment_container, sphereFragment)
            .commit()

        Log.i(TAG, "onCreate() Finished")
    }

    fun onRadioButtonClicked(view: View) {
        val f = supportFragmentManager.findFragmentById(R.id.sphere_fragment_container)

        if (f is NewSphereFragment) f.onRadioButtonClicked(view)
        else Log.w(TAG, "onRadioButtonClicked() occurred on a fragment that was not a NewSphereFragment")
    }
}