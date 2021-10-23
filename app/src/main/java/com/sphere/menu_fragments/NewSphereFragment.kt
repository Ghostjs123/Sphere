package com.sphere.menu_fragments

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.sphere.databinding.FragmentNewSphereBinding
import com.sphere.activity.SphereActivity

private const val TAG = "NewSphereFragment"


// NOTE: optional callback is just for usage from the SphereActivity
class NewSphereFragment(
    private val callback: (sphereName: String) -> Unit
) : Fragment() {

    private var _binding: FragmentNewSphereBinding? = null
    private val binding get() = _binding!!


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        Log.i(TAG, "onCreateView() Started")

        _binding = FragmentNewSphereBinding.inflate(inflater, container, false)

        Log.i(TAG, "onCreateView() Returning")

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        Log.i(TAG, "onViewCreated() Started")

        binding.createSphereButton.setOnClickListener {
            val sphereName = binding.sphereNameInput.text.toString()

            callback(sphereName)
            requireActivity().supportFragmentManager.popBackStack()
        }

        Log.i(TAG, "onViewCreated() Finished")
    }

    override fun onDestroyView() {
        super.onDestroyView()

        Log.i(TAG, "onDestroyView() Started")

        _binding = null

        Log.i(TAG, "onDestroyView() Finished")
    }
}