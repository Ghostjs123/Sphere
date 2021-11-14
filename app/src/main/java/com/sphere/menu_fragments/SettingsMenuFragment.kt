package com.sphere.menu_fragments

import android.app.ActivityManager
import android.app.AlertDialog
import android.content.Context
import android.content.DialogInterface
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.sphere.R
import com.sphere.databinding.FragmentSettingsMenuBinding

private const val TAG = "SettingsMenuFragment"

class SettingsMenuFragment : Fragment() {

    private var _binding: FragmentSettingsMenuBinding? = null
    private val binding get() = _binding!!


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        Log.i(TAG, "onCreateView() Started")

        _binding = FragmentSettingsMenuBinding.inflate(inflater, container, false)

        Log.i(TAG, "onCreateView() Returning")

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        Log.i(TAG, "onViewCreated() Started")

        binding.deleteButton.setOnClickListener { deleteOnDeviceData() }

        // NOTE: intentionally not adding this to BackStack
        childFragmentManager.beginTransaction()
            .replace(R.id.settings_menu_preferences, PreferencesFragment())
            .commit()

        binding.settingsToolbar.setNavigationIcon(R.drawable.abc_ic_ab_back_material)
        binding.settingsToolbar.setNavigationOnClickListener {
            requireActivity().supportFragmentManager.popBackStack()
        }

        Log.i(TAG, "onViewCreated() Returning")
    }

    private fun deleteOnDeviceData() {
        val dialogClickListener =
            DialogInterface.OnClickListener { _, which ->
                when (which) {
                    DialogInterface.BUTTON_POSITIVE -> {
                        Log.w(TAG, "Deleting all on device data - this will close the app")
                        (requireActivity().getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager).clearApplicationUserData()
                    }
                    DialogInterface.BUTTON_NEGATIVE -> { }
                }
            }

        AlertDialog.Builder(requireContext())
            .setMessage("Delete all on device data? - This will close the app")
            .setPositiveButton("Yes", dialogClickListener)
            .setNegativeButton("No", dialogClickListener)
            .show()
    }
}