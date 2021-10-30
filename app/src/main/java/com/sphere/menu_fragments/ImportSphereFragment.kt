package com.sphere.menu_fragments

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.FragmentManager
import com.sphere.databinding.FragmentImportSphereBinding
import com.sphere.activity.SphereActivity
import com.sphere.utility.readSphereFromFirestore

private const val TAG = "ImportSphereFragment"


// NOTE: optional callback is just for usage from the SphereActivity
class ImportSphereFragment(
    private val callback: (sphereName: String, seed: Long?, subdivision: Int) -> Unit
) : Fragment() {

    private var _binding: FragmentImportSphereBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        Log.i(TAG, "onCreateView() Started")

        _binding = FragmentImportSphereBinding.inflate(inflater, container, false)

        Log.i(TAG, "onCreateView() Returning")

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        Log.i(TAG, "onViewCreated() Started")

        binding.importSphereButton.setOnClickListener {
            val sphereName: String = binding.sphereNameInput.text.toString()

            readSphereFromFirestore(requireContext(), sphereName, callback)
            requireActivity().supportFragmentManager.popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE)
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