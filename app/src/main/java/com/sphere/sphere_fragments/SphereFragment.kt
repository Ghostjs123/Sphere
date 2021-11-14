package com.sphere.sphere_fragments

import android.Manifest
import android.app.Activity
import android.app.AlertDialog
import android.content.*
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.os.BatteryManager
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.PopupMenu
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.preference.PreferenceManager
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.sphere.R
import com.sphere.SphereViewModel
import com.sphere.databinding.FragmentSphereBinding
import com.sphere.menu_fragments.ImportSphereFragment
import com.sphere.menu_fragments.MySpheresFragment
import com.sphere.menu_fragments.NewSphereFragment
import com.sphere.menu_fragments.SettingsMenuFragment
import com.sphere.utility.addSphereToFirestore
import com.sphere.utility.saveSphereBitmap

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
    private var _binding: FragmentSphereBinding? = null
    private val binding get() = _binding!!

    private val sphereViewModel: SphereViewModel by activityViewModels()

    private var mSphereName: String = ""
    private var mSeed: Long? = null
    private var mSubdivision: Int = 0

    private lateinit var sensorManager: SensorManager

    private var mAmbientTemp: Sensor? = null
    private var ambientTemp: Float = 0f

    private var mLight: Sensor? = null
    private var illuminance: Float = 0f

    private var fusedLocationClient: FusedLocationProviderClient? = null
    private var latitude: Double = 0.0
    private var longitude: Double = 0.0

    private var temperature: Float = 0f

    // ========================================================================
    // Lifecycle Management

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        Log.i(TAG, "onCreateView() Started")

        _binding = FragmentSphereBinding.inflate(inflater, container, false)

        (activity as AppCompatActivity).supportActionBar?.hide()

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

        Log.i(TAG, "onViewCreated() Returning")
    }

    override fun onResume() {
        super.onResume()
        Log.i(TAG, "onResume()")

        activity?.registerReceiver(receiver, IntentFilter(Intent.ACTION_BATTERY_CHANGED))
        sensorManager.registerListener(this, mAmbientTemp, SensorManager.SENSOR_DELAY_NORMAL)
        sensorManager.registerListener(this, mLight, SensorManager.SENSOR_DELAY_NORMAL)
    }

    override fun onPause() {
        super.onPause()
        Log.i(TAG, "onPause()")

        activity?.unregisterReceiver(receiver)
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

    private val receiver: BroadcastReceiver = object: BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            intent?.apply {
                temperature = getIntExtra(
                    BatteryManager.EXTRA_TEMPERATURE, 0
                ) / 10f
            }
            updateUI()
        }
    }

    // ========================================================================
    // Sphere Management

    private fun updateUI() {
        // NOTE/TODO: this is only around for debugging
        binding.sensorValues.text = "seed: $mSeed\nsubdivisions: $mSubdivision"

        binding.sphereName.text = mSphereName
    }

    fun updateSphere(sphereName: String, seed: Long?, subdivision: Int) {
        mSphereName = sphereName
        mSeed = seed
        mSubdivision = subdivision
        binding.glSurfaceView.createNewSphere(mSeed, subdivision)

        updateUI()
    }

    fun renameSphere(newSphereName: String) {
        mSphereName = newSphereName
        updateUI()
    }

    fun getUpdateSphereCallback(): (String, Long?, Int) -> Unit {
        return ::updateSphere
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
            }
            else -> {
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

        binding.glSurfaceView.takeScreenshot(::screenshotCallback)

        updateUI()
    }

    private fun screenshotCallback(bitmap: Bitmap?) {
        if (bitmap == null) {
            Log.w(TAG, "Received null bitmap in screenshotCallback")
            return
        }

        saveSphereBitmap(requireContext(), mSphereName, bitmap)
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

        if (deviceTempEnabled) {
            seed += temperature.toLong()
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
                    .replace(R.id.sphere_menu_fragment_container, MySpheresFragment(::updateSphere, ::renameSphere))
                    .addToBackStack(null)
                    .commit()
                return true
            }
            R.id.import_sphere_menu_item -> {
                parentFragmentManager.beginTransaction()
                    .replace(R.id.sphere_menu_fragment_container, ImportSphereFragment(::updateSphere))
                    .addToBackStack(null)
                    .commit()
                return true
            }
            R.id.create_sphere_menu_item -> {
                parentFragmentManager.beginTransaction()
                    .replace(R.id.sphere_menu_fragment_container, NewSphereFragment(::updateSphere))
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
                        addSphereToFirestore(requireContext(), mSphereName, mSeed, mSubdivision)
                    }
                    DialogInterface.BUTTON_NEGATIVE -> { }
                }
            }

        val builder = AlertDialog.Builder(requireContext())
        builder.setMessage("Export Sphere $mSphereName")
            .setPositiveButton("Yes", dialogClickListener)
            .setNegativeButton("No", dialogClickListener)
            .show()
    }
}