package com.sphere.sphere.fragments

import android.Manifest
import android.app.AlertDialog
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
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.preference.PreferenceManager
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.sphere.R
import com.sphere.databinding.FragmentSphereBinding
import com.sphere.menu.fragments.ImportSphereFragment
import com.sphere.menu.fragments.MySpheresFragment
import com.sphere.menu.fragments.NewSphereFragment
import com.sphere.menu.fragments.SettingsMenuFragment
import com.sphere.sphere.SphereViewModel
import android.content.DialogInterface
import android.widget.Toast
import com.sphere.utility.addSphereToFirestore
import com.sphere.utility.hasCoarseLocationAccess
import com.sphere.utility.readSphereFromFirestore
import com.sphere.utility.requestLocationPermission


private const val TAG = "SphereFragment"


class SphereFragment(
    action: String,
    sphereName: String
) : Fragment(),
    PopupMenu.OnMenuItemClickListener,
    ActivityCompat.OnRequestPermissionsResultCallback,
    SensorEventListener {

    private lateinit var sphereViewModel: SphereViewModel

    private var _binding: FragmentSphereBinding? = null
    private val binding get() = _binding!!

    private var mAction: String = action
    private var mSphereName: String = sphereName
    private var mSeed: Long? = null

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
        sphereViewModel = ViewModelProvider(requireActivity())[SphereViewModel::class.java]

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(requireActivity())
        locationManager = requireActivity().getSystemService(Context.LOCATION_SERVICE) as LocationManager
        sensorManager = requireActivity().getSystemService(Context.SENSOR_SERVICE) as SensorManager

        mAmbientTemp = sensorManager.getDefaultSensor(Sensor.TYPE_AMBIENT_TEMPERATURE)
        mLight = sensorManager.getDefaultSensor(Sensor.TYPE_LIGHT)

        updateUI()

        Log.i(TAG, "onCreateView() Returning")

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        Log.i(TAG, "onViewCreated() Started")

        // mutate button
        binding.mutateButton.setOnClickListener {
            mutateSphere()
        }

        // options Button
        binding.sphereOptionsButton.setOnClickListener {
            showPopup(it)
        }

        when (mAction) {
            "NewSphere" -> {
                Log.i(TAG, "Handling ACTION NewSphere")
                createNewSphere()
            }
            "ImportSphere" -> {
                Log.i(TAG, "Handling ACTION ImportSphere")
                readSphereFromFirestore(
                    requireContext(),
                    mSphereName,
                    ::createNewSphereUsingSeedAndName
                )
            }
            else -> {
                Log.w(TAG, "Received un-handled action: $mAction")
            }
        }

        Log.i(TAG, "onViewCreated() Returning")
    }

    override fun onResume() {
        super.onResume()
        Log.i(TAG, "onResume()")

        sensorManager.registerListener(this, mAmbientTemp, SensorManager.SENSOR_DELAY_NORMAL)
        sensorManager.registerListener(this, mLight, SensorManager.SENSOR_DELAY_NORMAL)
    }

    override fun onPause() {
        super.onPause()
        Log.i(TAG, "onPause()")

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
        updateUI()  // TODO: debug only
    }

    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) { }

    // ========================================================================
    // Sphere Management

    private fun updateUI() {
        // NOTE/TODO: this is only around for debugging
        binding.sensorValues.text = "$ambientTemp\n$illuminance\nseed: $mSeed"

        binding.sphereName.text = mSphereName
    }

    private fun createNewSphere() {
        binding.glSurfaceView.createNewSphere(mSphereName)
        sphereViewModel.setName(mSphereName)

        updateUI()
    }

    private fun createNewSphereWithName(sphereName: String) {
        mSphereName = sphereName
        binding.glSurfaceView.createNewSphere(mSphereName)
        sphereViewModel.setName(mSphereName)

        updateUI()
    }

    private fun createNewSphereUsingSeedAndName(seed: Long?, sphereName: String) {
        mSphereName = sphereName
        mSeed = seed
        binding.glSurfaceView.createNewSphereUsingSeed(mSphereName, mSeed)
        sphereViewModel.setName(mSphereName)

        updateUI()
    }

    private fun mutateSphere() {
        val prefs = PreferenceManager.getDefaultSharedPreferences(requireActivity())
        val gpsEnabled = prefs.getBoolean("gps_enabled", false)
        val ambientTempEnabled = prefs.getBoolean("ambient_temp_enabled", false)
        val ambientLightEnabled = prefs.getBoolean("ambient_light_enabled", false)
        val deviceTempEnabled = prefs.getBoolean("device_temp_enabled", false)

        if (!(gpsEnabled || ambientTempEnabled || ambientLightEnabled || deviceTempEnabled)) {
            Toast.makeText(
                requireContext(),
                "Enable at least one sensor in settings before mutating",
                Toast.LENGTH_SHORT
            ).show()
            return
        }

        mSeed = fetchSeed()
        sphereViewModel.setSeed(mSeed)

        binding.glSurfaceView.mutateSphere(mSeed)

        updateUI()
    }

    private fun mutateCallback() {

    }

    private fun fetchSeed(): Long? {
        // TODO: generate seed from device sensors here
        val prefs = PreferenceManager.getDefaultSharedPreferences(requireActivity())
        val gpsEnabled = prefs.getBoolean("gps_enabled", false)
        val ambientTempEnabled = prefs.getBoolean("ambient_temp_enabled", false)
        val ambientLightEnabled = prefs.getBoolean("ambient_light_enabled", false)
        val deviceTempEnabled = prefs.getBoolean("device_temp_enabled", false)

        var seed: Long? = null

        if (gpsEnabled && !hasCoarseLocationAccess(requireContext())) {
            requestLocationPermission(requireActivity())
        } else {
            seed = 0

            if (gpsEnabled && hasCoarseLocationAccess(requireContext()))
            {
                Toast.makeText(requireContext(), "Trying to use location data", Toast.LENGTH_SHORT).show()
            }

            if (ambientTempEnabled) {
                seed += ambientTemp.toLong()
            }

            if (ambientLightEnabled) {
                seed += illuminance.toLong()
            }

        }

        return seed
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
                    .replace(R.id.sphere_menu_fragment_container, MySpheresFragment())
                    .addToBackStack(null)
                    .commit()
                return true
            }
            R.id.import_sphere_menu_item -> {
                parentFragmentManager.beginTransaction()
                    .replace(R.id.sphere_menu_fragment_container, ImportSphereFragment(::createNewSphereUsingSeedAndName))
                    .addToBackStack(null)
                    .commit()
                return true
            }
            R.id.create_sphere_menu_item -> {
                parentFragmentManager.beginTransaction()
                    .replace(R.id.sphere_menu_fragment_container, NewSphereFragment(::createNewSphereWithName))
                    .addToBackStack(null)
                    .commit()
                return true
            }
            R.id.settings_menu_item -> {
                parentFragmentManager.beginTransaction()
                    .replace(R.id.sphere_menu_fragment_container, SettingsMenuFragment())
                    .addToBackStack(null)
                    .commit()
                return true
            }
            R.id.export_sphere_menu_item -> {
                exportDialog()
                return true
            }
        }
        return false
    }

    // ========================================================================
    // Export Dialogue

    private fun exportDialog() {
        val dialogClickListener =
            DialogInterface.OnClickListener { _, which ->
                when (which) {
                    DialogInterface.BUTTON_POSITIVE -> {
                        addSphereToFirestore(requireContext(), mSphereName, mSeed)
                    }
                    DialogInterface.BUTTON_NEGATIVE -> {

                    }
                }
            }

        val builder = AlertDialog.Builder(requireContext())
        builder.setMessage("Export Sphere $mSphereName")
            .setPositiveButton("Yes", dialogClickListener)
            .setNegativeButton("No", dialogClickListener)
            .show()
    }
}