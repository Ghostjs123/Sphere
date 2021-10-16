package com.sphere.sphere.activity

import android.os.Bundle
import android.util.Log
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.sphere.R
import com.sphere.sphere.SphereViewModel
import com.sphere.sphere.SphereViewModelFactory
import com.sphere.sphere.fragments.SphereFragment
import com.sphere.sphere.room_code.SphereApplication
import com.sphere.sphere.room_code.SphereListAdapter

private const val TAG = "SphereActivity"

class SphereActivity : AppCompatActivity() {

    private val sphereViewModel: SphereViewModel by viewModels {
        SphereViewModelFactory((application as SphereApplication).repository)
    }

    public override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        Log.i(TAG, "onCreate() Started")

        val adapter = SphereListAdapter()
        setContentView(R.layout.activity_sphere)

        sphereViewModel.allSpheres.observe(this) { spheres ->
            // Update the cached copy of the words in the adapter.
            spheres.let { adapter.submitList(it) }
        }

        val intentAction = intent.getStringExtra("ACTION")
        val sphereName = intent.getStringExtra("SPHERE_NAME")

        Log.i(TAG, "Received '$intentAction' Intent")

        supportFragmentManager.beginTransaction()
            .replace(
                R.id.sphere_fragment_container,
                SphereFragment(intentAction!!, sphereName!!)
            )
            .addToBackStack(null)
            .commit()

        Log.i(TAG, "onCreate() Finished")
    }
}