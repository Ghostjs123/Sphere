package com.sphere.activity

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.FragmentManager
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.sphere.R
import com.sphere.SphereViewModel
import com.sphere.SphereViewModelFactory
import com.sphere.menu_fragments.NoSphereFragment
import com.sphere.sphere_fragments.SphereFragment
import com.sphere.room_code.SphereApplication
import com.sphere.room_code.SphereListAdapter

private const val TAG = "SphereActivity"


class SphereActivity : AppCompatActivity() {

    private lateinit var fusedLocationClient: FusedLocationProviderClient

    private val sphereViewModel: SphereViewModel by viewModels {
        SphereViewModelFactory((application as SphereApplication).repository)
    }

    public override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        Log.i(TAG, "onCreate() Started")

        setContentView(R.layout.activity_sphere)

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)

        val adapter = SphereListAdapter()
        sphereViewModel.allSpheres.observe(this) { spheres ->
            // Update the cached copy of the words in the adapter.
            spheres.let { adapter.submitList(it) }
        }

        supportFragmentManager.beginTransaction()
            .replace(R.id.sphere_fragment_container, SphereFragment())
            .commit()

        Log.i(TAG, "onCreate() Finished")
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        val sphereFragment = supportFragmentManager.findFragmentById(R.id.sphere_fragment_container) as SphereFragment

//        when (permissions) {
//            permissions.getOrDefault(Manifest.permission.ACCESS_FINE_LOCATION, false) -> {
//                // Precise location access granted.
//            }
//            permissions.getOrDefault(Manifest.permission.ACCESS_COARSE_LOCATION, false) -> {
//                // Only approximate location access granted.
//            } else -> {
//            // No location access granted.
//            }
//        }

        Log.i(TAG, "Location permissions denied")
        disableLocationPermission()

//        fusedLocationClient.getCurrentLocation()
//            .addOnSuccessListener { mutateSphere() }
    }

    private fun disableLocationPermission() {
        Toast.makeText(
            this,
            "Use GPS Data disabled",
            Toast.LENGTH_SHORT
        ).show()

        val sharedPref = getPreferences(Context.MODE_PRIVATE)
        with (sharedPref.edit()) {
            putBoolean("gps_enabled", false)
            apply()
        }
    }
}