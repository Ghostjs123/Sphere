package com.sphere.menu_fragments

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.sphere.R
import com.sphere.activity.SphereActivity
import com.sphere.databinding.FragmentNoSphereBinding

private const val TAG = "NoSphereFragment"


class NoSphereFragment: Fragment() {

    private var _binding: FragmentNoSphereBinding? = null
    private val binding get() = _binding!!

    companion object {
        private lateinit var updateSphereCallback: (sphereName: String, seed: Long?, subdivision: Int) -> Unit

        fun newInstance(
            updateSphereCallback: (sphereName: String, seed: Long?, subdivision: Int) -> Unit
        ): NoSphereFragment {
            val fragment = NoSphereFragment()
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

        _binding = FragmentNoSphereBinding.inflate(inflater, container, false)

        Log.i(TAG, "onCreateView() Returning")

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        Log.i(TAG, "onViewCreated() Started")

        binding.createNewSphereText.setOnClickListener {
            parentFragmentManager.beginTransaction()
            .add(R.id.sphere_menu_fragment_container, NewSphereFragment.newInstance(updateSphereCallback))
            .addToBackStack(null)
            .commit()
        }
        binding.importSphereText.setOnClickListener {
            parentFragmentManager.beginTransaction()
            .add(R.id.sphere_menu_fragment_container, ImportSphereFragment.newInstance(updateSphereCallback))
            .addToBackStack(null)
            .commit()
        }

        binding.noSphereToolbar.setNavigationIcon(R.drawable.abc_ic_ab_back_material)
        binding.noSphereToolbar.setNavigationOnClickListener {
            requireActivity().supportFragmentManager.popBackStack()
        }

        Log.i(TAG, "onViewCreated() Finished")
    }

    override fun onViewStateRestored(savedInstanceState: Bundle?) {
        super.onViewStateRestored(savedInstanceState)

        updateSphereCallback = (activity as SphereActivity).getUpdateSphereCallback()
    }

    override fun onDestroyView() {
        super.onDestroyView()

        Log.i(TAG, "onDestroyView() Started")

        _binding = null

        Log.i(TAG, "onDestroyView() Finished")
    }
}