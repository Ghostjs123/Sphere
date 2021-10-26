package com.sphere.menu_fragments

import android.app.Activity
import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorManager
import android.os.Bundle
import android.widget.Toast
import androidx.preference.Preference
import androidx.preference.PreferenceFragmentCompat
import androidx.preference.SwitchPreference
import com.sphere.R
import kotlinx.android.synthetic.main.fragment_settings_menu.*

//<SwitchPreference
//app:key="gps_enabled"
//app:title="Use GPS Data" />
//
//<SwitchPreference
//app:key="ambient_temp_enabled"
//app:title="Use Ambient Temperature" />
//
//<SwitchPreference
//app:key="ambient_light_enabled"
//app:title="Use Ambient Light" />
//
//<SwitchPreference
//app:key="device_temp_enabled"
//app:title="Use Device Temperature" />

class PreferencesFragment : PreferenceFragmentCompat() {
    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        setPreferencesFromResource(R.xml.preferences, rootKey)

        val sensorManager = requireActivity().getSystemService(Context.SENSOR_SERVICE) as SensorManager

        findPreference<SwitchPreference>("ambient_temp_enabled")!!
            .onPreferenceClickListener = Preference.OnPreferenceClickListener {
                val mTemperature = sensorManager.getDefaultSensor(Sensor.TYPE_AMBIENT_TEMPERATURE)

                if (mTemperature == null) {
                    (it as SwitchPreference).isChecked = false

                    Toast.makeText(
                        requireActivity(),
                        "Device does not support Ambient Temperature",
                        Toast.LENGTH_SHORT
                    ).show()
                }

                return@OnPreferenceClickListener true
            }
    }
}