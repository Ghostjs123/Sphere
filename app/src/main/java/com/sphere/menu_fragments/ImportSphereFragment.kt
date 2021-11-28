package com.sphere.menu_fragments

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.res.ResourcesCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.activityViewModels
import com.google.firebase.firestore.FirebaseFirestore
import com.sphere.R
import com.sphere.SphereViewModel
import com.sphere.activity.SphereActivity
import com.sphere.databinding.FragmentImportSphereBinding
import com.sphere.utility.readSphereFromFirestore
import com.sphere.utility.setSelectedSpherePref

private const val TAG = "ImportSphereFragment"


class ImportSphereFragment: Fragment() {

    private var _binding: FragmentImportSphereBinding? = null
    private val binding get() = _binding!!

    private val sphereViewModel: SphereViewModel by activityViewModels()

    companion object {
        private lateinit var updateSphereCallback: (sphereName: String, seed: Long?, subdivision: Int) -> Unit

        fun newInstance(
            updateSphereCallback: (sphereName: String, seed: Long?, subdivision: Int) -> Unit
        ): ImportSphereFragment {
            val fragment = ImportSphereFragment()
            this.updateSphereCallback = updateSphereCallback

            return fragment
        }
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

            readSphereFromFirestore(
                FirebaseFirestore.getInstance(),
                requireContext(),
                sphereName,
                ::firebaseCallback
            )
        }

        binding.importSphereToolbar.setNavigationIcon(R.drawable.abc_ic_ab_back_material)
        binding.importSphereToolbar.setNavigationOnClickListener {
            requireActivity().supportFragmentManager.popBackStack()
        }

        Log.i(TAG, "onViewCreated() Finished")
    }

    override fun onViewStateRestored(savedInstanceState: Bundle?) {
        super.onViewStateRestored(savedInstanceState)

        updateSphereCallback = (activity as SphereActivity).getUpdateSphereCallback()
    }

    private fun firebaseCallback(sphereName: String, seed: Long?, subdivisions: Int) {
        updateSphereCallback(sphereName, seed, subdivisions)
        setSelectedSpherePref(requireActivity(), sphereName)
        sphereViewModel.addSphere(sphereName, seed, subdivisions)

        requireActivity().supportFragmentManager.popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE)
    }
}
