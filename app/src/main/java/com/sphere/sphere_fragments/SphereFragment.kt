package com.sphere.sphere_fragments

import android.Manifest
import android.app.Activity
import android.app.AlertDialog
import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.PopupMenu
import androidx.core.app.ActivityCompat
import androidx.preference.PreferenceManager
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.sphere.R
import com.sphere.databinding.FragmentSphereBinding
import com.sphere.SphereViewModel
import android.content.DialogInterface
import android.content.pm.PackageManager
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import com.google.android.gms.location.*
import com.sphere.menu_fragments.*
import com.sphere.utility.addSphereToFirestore

private const val TAG = "SphereFragment"


fun checkLocationPermission(activity: Activity): Boolean {
    if (ActivityCompat.checkSelfPermission(activity, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
        return true
    }
    if (ActivityCompat.checkSelfPermission(activity, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
        return true
    }
    return false
}


class SphereFragment :
    Fragment(),
    PopupMenu.OnMenuItemClickListener,
    ActivityCompat.OnRequestPermissionsResultCallback,
    SensorEventListener
{
    private lateinit var sphereViewModel: SphereViewModel

    private var _binding: FragmentSphereBinding? = null
    private val binding get() = _binding!!

    private var mSphereName: String = ""
    private var mSeed: Long? = null

    private var fusedLocationClient: FusedLocationProviderClient? = null
    private lateinit var sensorManager: SensorManager
    private lateinit var mAmbientTemp: Sensor
    private var ambientTemp: Float = 0f
    private lateinit var mLight: Sensor
    private var illuminance: Float = 0f
    private var latitude: Double = 0.0
    private var longitude: Double = 0.0

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

        sensorManager = requireActivity().getSystemService(Context.SENSOR_SERVICE) as SensorManager
        mAmbientTemp = sensorManager.getDefaultSensor(Sensor.TYPE_AMBIENT_TEMPERATURE)
        mLight = sensorManager.getDefaultSensor(Sensor.TYPE_LIGHT)

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(requireActivity())

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

        // TODO: use the view model + shared preferences
        // TODO: navigate to NoSphereFragment instead of SphereFragment if no spheres in the view model
//        parentFragmentManager.beginTransaction()
//            .replace(R.id.sphere_menu_fragment_container, NoSphereFragment(
//                ::createNewSphereWithName,
//                ::createNewSphereWithSeedAndName
//            ))
//            .addToBackStack(getString(R.string.SphereFragmentName))
//            .commit()
        createNewSphereWithName("asd")  // TODO: delete this line once ^^ is done

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
        binding.sensorValues.text = "$ambientTemp\n$illuminance\n$latitude\n$longitude\nseed: $mSeed"

        binding.sphereName.text = mSphereName
    }

    private fun createNewSphereWithName(sphereName: String) {
        mSphereName = sphereName
        binding.glSurfaceView.createNewSphere(mSphereName)
        sphereViewModel.setName(mSphereName)

        updateUI()
    }

    private fun createNewSphereWithSeedAndName(seed: Long?, sphereName: String) {
        mSphereName = sphereName
        mSeed = seed
        binding.glSurfaceView.createNewSphereUsingSeed(mSphereName, mSeed)
        sphereViewModel.setName(mSphereName)

        updateUI()
    }

    // NOTE: this has to be declared at the top level or onCreate()
    private val locationPermissionRequest = registerForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        when {
            permissions.getOrDefault(Manifest.permission.ACCESS_FINE_LOCATION, false) -> {
                mutateSphere()
            }
            permissions.getOrDefault(Manifest.permission.ACCESS_COARSE_LOCATION, false) -> {
                mutateSphere()
            } else -> {
                Toast.makeText(
                    activity,
                    "Give Location Permissions to use GPS data or disable 'Use GPS data' in settings",
                    Toast.LENGTH_LONG
                ).show()
            }
        }
    }
    private fun mutateSphere() {
        val prefs = PreferenceManager.getDefaultSharedPreferences(activity)
        val gpsEnabled = prefs.getBoolean("gps_enabled", false)
        val ambientTempEnabled = prefs.getBoolean("ambient_temp_enabled", false)
        val ambientLightEnabled = prefs.getBoolean("ambient_light_enabled", false)
        val deviceTempEnabled = prefs.getBoolean("device_temp_enabled", false)

        if (!(gpsEnabled || ambientTempEnabled || ambientLightEnabled || deviceTempEnabled)) {
            Toast.makeText(
                requireContext(),
                "Enable at least one sensor in settings before mutating",
                Toast.LENGTH_LONG
            ).show()
            return
        }

        if (gpsEnabled) {
            if (!checkLocationPermission(requireActivity())) {
                locationPermissionRequest.launch(arrayOf(
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION))
                return
            }
            else {
                Log.i(TAG, "Requesting lastLocation")
                fusedLocationClient?.lastLocation?.addOnCompleteListener {
                    if (it.result != null) {
                        latitude = it.result.latitude
                        longitude = it.result.longitude
                        Log.i(TAG, "Received lastLocation of $latitude $longitude")
                    }
                    else {
                        Log.w(TAG, "Received null lastLocation")
                    }

                    finishMutate()
                }
            }
        }
        else {
            finishMutate()
        }
    }

    private fun finishMutate() {
        mSeed = fetchSeed()
        sphereViewModel.setSeed(mSeed)
        binding.glSurfaceView.mutateSphere(mSeed)
        updateUI()
    }

    private fun fetchSeed(): Long {
        val prefs = PreferenceManager.getDefaultSharedPreferences(activity)
        val gpsEnabled = prefs.getBoolean("gps_enabled", false)
        val ambientTempEnabled = prefs.getBoolean("ambient_temp_enabled", false)
        val ambientLightEnabled = prefs.getBoolean("ambient_light_enabled", false)
        val deviceTempEnabled = prefs.getBoolean("device_temp_enabled", false)

        var seed: Long = 0

        if (gpsEnabled) {
            seed += latitude.toLong()
            seed += longitude.toLong()
        }

        if (ambientTempEnabled) {
            seed += ambientTemp.toLong()
        }

        if (ambientLightEnabled) {
            seed += illuminance.toLong()
        }

        Log.i(TAG, "fetchSeed() returning $seed")
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
                    .add(R.id.sphere_fragment_container, MySpheresFragment())
                    .addToBackStack(null)
                    .commit()
                return true
            }
            R.id.import_sphere_menu_item -> {
                parentFragmentManager.beginTransaction()
                    .add(R.id.sphere_fragment_container, ImportSphereFragment(::createNewSphereWithSeedAndName))
                    .addToBackStack(null)
                    .commit()
                return true
            }
            R.id.create_sphere_menu_item -> {
                parentFragmentManager.beginTransaction()
                    .add(R.id.sphere_fragment_container, NewSphereFragment(::createNewSphereWithName))
                    .addToBackStack(null)
                    .commit()
                return true
            }
            R.id.settings_menu_item -> {
                parentFragmentManager.beginTransaction()
                    .add(R.id.sphere_fragment_container, SettingsMenuFragment())
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