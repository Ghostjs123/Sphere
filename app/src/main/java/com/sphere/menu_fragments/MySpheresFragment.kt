package com.sphere.menu_fragments

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.sphere.SphereViewModel
import com.sphere.databinding.FragmentMySpheresBinding
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

        Log.i(TAG, "onViewCreated() Returning")
    }

}