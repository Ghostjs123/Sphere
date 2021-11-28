package com.sphere.menu_fragments

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


class PreferencesFragment : PreferenceFragmentCompat() {
    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        setPreferencesFromResource(R.xml.preferences, rootKey)

        val sensorManager = requireActivity().getSystemService(Context.SENSOR_SERVICE) as SensorManager

        findPreference<SwitchPreference>("ambient_temp_enabled")!!
            .onPreferenceClickListener = Preference.OnPreferenceClickListener {
                // NOTE: this will turn off ambient temp if the device does not support it
                val mTemperature = sensorManager.getDefaultSensor(Sensor.TYPE_AMBIENT_TEMPERATURE)

                if (mTemperature == null) {
                    (it as SwitchPreference).isChecked = false

                    Toast.makeText(
                        requireActivity(),
                        R.string.ambient_temp_unsupported,
                        Toast.LENGTH_SHORT
                    ).show()
                }

                return@OnPreferenceClickListener true
            }
    }
}