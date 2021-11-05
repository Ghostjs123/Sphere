package com.sphere.menu_fragments

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.activity.viewModels
import androidx.core.content.res.ResourcesCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.get
import androidx.recyclerview.widget.LinearLayoutManager
import com.sphere.R
import com.sphere.SphereViewModel
import com.sphere.SphereViewModelFactory
import com.sphere.databinding.FragmentMySpheresBinding
import com.sphere.room_code.SphereApplication
import com.sphere.room_code.SphereListAdapter
import com.sphere.utility.getSelectedSpherePref
import com.sphere.utility.setSelectedSpherePref

private const val TAG = "MySpheresFragment"


class MySpheresFragment(
    private val callback: (sphereName: String, seed: Long?, subdivision: Int) -> Unit
) : Fragment() {

    private var _binding: FragmentMySpheresBinding? = null
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

        _binding = FragmentMySpheresBinding.inflate(inflater, container, false)

        val recyclerView = binding.MySpheresRecyclerView
        val adapter = SphereListAdapter(::sphereSelected)

        recyclerView.adapter = adapter
        recyclerView.layoutManager = LinearLayoutManager(activity)

        sphereViewModel.allSpheres.observe(viewLifecycleOwner, { spheres ->
            spheres?.let { adapter.submitList(it) }
        })

        Log.i(TAG, "onCreateView() Returning")

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        Log.i(TAG, "onViewCreated() Started")

        // TODO - If delete button is pressed we delete that sphere from the local repository.
        //  HUGE NOTE: We may not want to let users do this if this is the sphere they currently
        //  have open, if we do allow it then it should boot them back to the createNewSphere page afterwards.
//        binding.spheresRecyclerView.delete_button.setOnClickListener {
//            Log.i(TAG, "delete_button was pressed.")
//        }
//        binding.createSphereButton.setOnClickListener {
//            sphereName = binding.sphereNameInput.text.toString()
//
//            when {
//                sphereName == "" -> {
//                    Toast.makeText(
//                        requireContext(),
//                        "Please enter a sphere name",
//                        Toast.LENGTH_LONG
//                    ).show()
//                }
//                subdivision == 0 -> {
//                    Toast.makeText(
//                        requireContext(),
//                        "Please choose a subdivision",
//                        Toast.LENGTH_LONG
//                    ).show()
//                }
//                else -> {
//                    callback(sphereName, subdivision)
//                    requireActivity().supportFragmentManager.popBackStack()
//                }
//            }
//        }

        // TODO - If Load is pressed, we should load that sphere from the local repository into the ViewModel
        //  and then jump back the the sphereFragment and re-render.

        binding.MySpheresTestButton.setOnClickListener {
            updateSelectedBorder()
        }

        Log.i(TAG, "onViewCreated() Returning")
    }

    private fun sphereSelected(sphereName: String) {
        setSelectedSpherePref(requireActivity(), sphereName)
        updateSelectedBorder()

        if (sphereViewModel.loadSphere(sphereName)) {
            callback(sphereName, sphereViewModel.getSeed(), sphereViewModel.getSubdivisions())
        }
        else {
            Log.w(TAG, "ViewModel failed to load sphere with name: $sphereName")
        }
    }

    private fun updateSelectedBorder() {
        for (i in 0 until binding.MySpheresRecyclerView.childCount) {
            val view = binding.MySpheresRecyclerView.getChildAt(i)
            val textView = view.findViewById(R.id.MySpheresItemText) as TextView
            val selectedSphere = getSelectedSpherePref(requireActivity())

            if (textView.text == selectedSphere) {
                view.background = ResourcesCompat.getDrawable(
                    resources,
                    R.drawable.blue_border,
                    requireContext().theme
                )
            }
            else {
                view.background = ResourcesCompat.getDrawable(
                    resources,
                    R.drawable.black_border,
                    requireContext().theme
                )
            }
        }
    }
}
