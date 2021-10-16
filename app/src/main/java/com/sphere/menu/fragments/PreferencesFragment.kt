package com.sphere.menu.fragments

import android.os.Bundle
import androidx.preference.PreferenceFragmentCompat
import com.sphere.R


class PreferencesFragment : PreferenceFragmentCompat() {
    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        setPreferencesFromResource(R.xml.preferences, rootKey)
    }
}