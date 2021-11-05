package com.sphere.menu_fragments

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.viewModels
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.viewModels
import com.sphere.SphereViewModel
import com.sphere.SphereViewModelFactory
import com.sphere.databinding.FragmentImportSphereBinding
import com.sphere.activity.SphereActivity
import com.sphere.room_code.SphereApplication
import com.sphere.utility.readSphereFromFirestore
import com.sphere.utility.setSelectedSpherePref

private const val TAG = "ImportSphereFragment"


class ImportSphereFragment(
    private val updateSphereCallback: (sphereName: String, seed: Long?, subdivision: Int) -> Unit
) : Fragment() {

    private var _binding: FragmentImportSphereBinding? = null
    private val binding get() = _binding!!

    private val sphereViewModel: SphereViewModel by viewModels {
        SphereViewModelFactory((requireActivity().application as SphereApplication).repository)
    }

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

            readSphereFromFirestore(requireContext(), sphereName, ::firebaseCallback)
        }

        Log.i(TAG, "onViewCreated() Finished")
    }

    private fun firebaseCallback(sphereName: String, seed: Long?, subdivisions: Int) {
        updateSphereCallback(sphereName, seed, subdivisions)
        setSelectedSpherePref(requireActivity(), sphereName)
        sphereViewModel.addSphere(sphereName, seed, subdivisions)

        requireActivity().supportFragmentManager.popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE)
    }
}
