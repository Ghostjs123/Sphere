package com.sphere.menu.fragments

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.fragment.app.FragmentManager
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

        Log.i(TAG, "onViewCreated() Returning")
    }

    private fun deleteOnDeviceData() {
        // TODO: clear the ViewModel and Sqlite database (Room)

        // clears backstack
        parentFragmentManager.popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE)

        parentFragmentManager.beginTransaction()
        .replace(R.id.fragment_container, NoSphereFragment())
        .addToBackStack(null)
        .commit()
    }
}