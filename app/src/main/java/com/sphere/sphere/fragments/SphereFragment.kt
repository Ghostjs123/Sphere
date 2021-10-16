package com.sphere.sphere.fragments

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.location.LocationManager
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.PopupMenu
<<<<<<< HEAD
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.preference.PreferenceManager
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
=======
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
>>>>>>> 2b32a4f6a5146ab1778dcdf20c8d57c20fc395e9
import com.sphere.R
import com.sphere.databinding.FragmentSphereBinding
import com.sphere.menu.fragments.ImportSphereFragment
import com.sphere.menu.fragments.MySpheresFragment
import com.sphere.menu.fragments.NewSphereFragment
import com.sphere.menu.fragments.SettingsMenuFragment
<<<<<<< HEAD
=======
import com.sphere.sphere.SphereViewModel
import kotlin.random.Random
>>>>>>> 2b32a4f6a5146ab1778dcdf20c8d57c20fc395e9


private const val TAG = "SphereFragment"

private const val MY_PERMISSIONS_REQUEST_LOCATION = 99
private const val MY_PERMISSIONS_REQUEST_BACKGROUND_LOCATION = 66


class SphereFragment(action: String, sphereName: String) :
    Fragment(),
    PopupMenu.OnMenuItemClickListener,
    ActivityCompat.OnRequestPermissionsResultCallback,
    SensorEventListener {

    private lateinit var sphereViewModel: SphereViewModel

    private var _binding: FragmentSphereBinding? = null
    private val binding get() = _binding!!

    private var mAction: String = action
    private var mSphereName: String = sphereName

    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private lateinit var locationManager: LocationManager
    private lateinit var sensorManager: SensorManager

    private lateinit var mAmbientTemp: Sensor
    private var ambientTemp: Float = 0f
    private lateinit var mLight: Sensor
    private var illuminance: Float = 0f

    // ========================================================================
    // Lifecycle Management

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        Log.i(TAG, "onCreateView() Started")

        _binding = FragmentSphereBinding.inflate(inflater, container, false)
        sphereViewModel = ViewModelProvider(requireActivity()).get(SphereViewModel::class.java)

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(requireActivity())
        locationManager = requireActivity().getSystemService(Context.LOCATION_SERVICE) as LocationManager
        sensorManager = requireActivity().getSystemService(Context.SENSOR_SERVICE) as SensorManager

        mAmbientTemp = sensorManager.getDefaultSensor(Sensor.TYPE_AMBIENT_TEMPERATURE)
        mLight = sensorManager.getDefaultSensor(Sensor.TYPE_LIGHT)

        Log.i(TAG, "onCreateView() Returning")

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val prefs = PreferenceManager.getDefaultSharedPreferences(requireActivity())
        val gpsEnabled = prefs.getBoolean("gps_enabled", false)
        val ambientTempEnabled = prefs.getBoolean("ambient_temp_enabled", false)
        val ambientLightEnabled = prefs.getBoolean("ambient_light_enabled", false)
        val deviceTempEnabled = prefs.getBoolean("device_temp_enabled", false)

        // mutate button
        binding.mutateButton.setOnClickListener {
            mutateSphere()
        }
        binding.mutateButton.isEnabled = gpsEnabled || ambientTempEnabled || ambientLightEnabled || deviceTempEnabled

        // options Button
        binding.sphereOptionsButton.setOnClickListener {
            showPopup(it)
        }

        when (mAction) {
            "NewSphere" -> {
                createNewSphere()
            }
            "ImportSphere" -> {
                createNewSphereUsingSeed()
            }
            else -> {
                Log.w(TAG, "Received un-handled action: $mAction")
            }
        }
    }

    override fun onResume() {
        super.onResume()

        sensorManager.registerListener(this, mAmbientTemp, SensorManager.SENSOR_DELAY_NORMAL)
        sensorManager.registerListener(this, mLight, SensorManager.SENSOR_DELAY_NORMAL)
    }

    override fun onPause() {
        super.onPause()

        sensorManager.unregisterListener(this)
    }

    override fun onSensorChanged(event: SensorEvent?) {
        when (event?.sensor?.type) {
            Sensor.TYPE_LIGHT -> {
                illuminance = event.values[0]
            }
            Sensor.TYPE_AMBIENT_TEMPERATURE -> {
                ambientTemp = event.values[0]
            }
        }
    }

    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) { }

    // ========================================================================
    // Sphere Management

    private fun createNewSphere() {
        binding.glSurfaceView.createNewSphere(mSphereName)
        binding.sphereName.text = mSphereName
        sphereViewModel.setName(mSphereName)
    }

    private fun createNewSphereUsingSeed() {
        binding.glSurfaceView.createNewSphereUsingSeed(mSphereName, fetchSeedFromFirebase(mSphereName))
        binding.sphereName.text = mSphereName
        sphereViewModel.setName(mSphereName)
    }

    private fun mutateSphere() {
        val seed = fetchSeed()

        if (seed != null) {
            binding.glSurfaceView.mutateSphere(seed)
        }
    }

    private fun fetchSeed(): Long? {
        // TODO: generate seed from device sensors here
<<<<<<< HEAD
        val prefs = PreferenceManager.getDefaultSharedPreferences(requireActivity())
        val gpsEnabled = prefs.getBoolean("gps_enabled", false)
        val ambientTempEnabled = prefs.getBoolean("ambient_temp_enabled", false)
        val ambientLightEnabled = prefs.getBoolean("ambient_light_enabled", false)
        val deviceTempEnabled = prefs.getBoolean("device_temp_enabled", false)

        return if (gpsEnabled && !hasCoarseLocationAccess()) {
            requestLocationPermission()

            null
        } else {
            var seed: Long = 0

            if (gpsEnabled &&
                ContextCompat.checkSelfPermission(
                    requireActivity(),
                    Manifest.permission.ACCESS_COARSE_LOCATION
                ) == PackageManager.PERMISSION_GRANTED)
            {

            }

            if (ambientTempEnabled) {
                seed += ambientTemp.toLong()
            }

            if (ambientLightEnabled) {
                seed += illuminance.toLong()
            }

            seed
        }
=======
        val seed = Random.nextLong(0, 1000)
        sphereViewModel.setSeed(seed)
        return seed
>>>>>>> 2b32a4f6a5146ab1778dcdf20c8d57c20fc395e9
    }

    private fun fetchSeedFromFirebase(sphereName: String): Long {
        // TODO: actually call Firebase here
        // TODO: like in fetchSeed() this should also make a call to sphereViewModel.setSeed(...)
        return 1000
    }

    // ========================================================================
    // Location Permissions

    private fun hasCoarseLocationAccess(): Boolean {
        return ContextCompat.checkSelfPermission(
            requireActivity(),
            Manifest.permission.ACCESS_COARSE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED
    }

    private fun requestLocationPermission() {
        ActivityCompat.requestPermissions(
            requireActivity(),
            arrayOf(Manifest.permission.ACCESS_COARSE_LOCATION),
            0
        )
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        when (requestCode) {
            MY_PERMISSIONS_REQUEST_LOCATION -> {
                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    mutateSphere()
                }
                else {
                    Log.i(TAG, "Location permissions denied")
                    // TODO: handle permission denied case
                }
            }
            else -> {
                Log.i(TAG, "Location permissions denied")
                // TODO: handle permission denied case
            }
        }
    }

    // ========================================================================
    // PopupMenu

    private fun showPopup(v: View) {
        Log.i(TAG, "Showing PopupMenu")

        val popup = PopupMenu(activity, v)
        popup.setOnMenuItemClickListener(this)
        popup.menuInflater.inflate(R.menu.sphere_menu, popup.menu)
        popup.show()
    }

    override fun onMenuItemClick(item: MenuItem): Boolean {
        Log.i(TAG, "Selected Item: " + item.title)

        when (item.itemId) {
            R.id.my_spheres_menu_item -> {
                parentFragmentManager.beginTransaction()
                    .replace(R.id.sphere_fragment_container, MySpheresFragment())
                    .addToBackStack(null)
                    .commit()
                return true
            }
            R.id.import_sphere_menu_item -> {
                parentFragmentManager.beginTransaction()
                    .replace(R.id.sphere_fragment_container, ImportSphereFragment())
                    .addToBackStack(null)
                    .commit()
                return true
            }
            R.id.create_sphere_menu_item -> {
                parentFragmentManager.beginTransaction()
                    .replace(R.id.sphere_fragment_container, NewSphereFragment())
                    .addToBackStack(null)
                    .commit()
                return true
            }
            R.id.settings_menu_item -> {
                parentFragmentManager.beginTransaction()
                    .replace(R.id.sphere_fragment_container, SettingsMenuFragment())
                    .addToBackStack(null)
                    .commit()
                return true
            }
            R.id.export_sphere_menu_item -> {
                // TODO: export sphere dialogue
                return true
            }
        }
        return false
    }
}