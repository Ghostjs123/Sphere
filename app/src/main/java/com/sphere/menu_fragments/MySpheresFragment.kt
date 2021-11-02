package com.sphere.menu_fragments

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.sphere.R
import com.sphere.SphereViewModel
import com.sphere.databinding.FragmentMySpheresBinding
import com.sphere.room_code.Sphere
import com.sphere.room_code.SphereListAdapter


private const val TAG = "MySpheresFragment"

class MySpheresFragment : Fragment() {

    private var _binding: FragmentMySpheresBinding? = null
    private val binding get() = _binding!!
    private val sphereViewModel: SphereViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        Log.i(TAG, "onCreateView() Started")

        _binding = FragmentMySpheresBinding.inflate(inflater, container, false)

        val recyclerView = binding.spheresRecyclerView
        val adapter = SphereListAdapter()

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

        Log.i(TAG, "onViewCreated() Returning")
    }
}