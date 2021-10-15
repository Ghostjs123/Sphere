package com.sphere.menu.fragments

import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.sphere.R
import com.sphere.databinding.FragmentMySpheresBinding
import com.sphere.databinding.FragmentNewSphereBinding
import com.sphere.sphere.SphereListAdapter
import com.sphere.sphere.SphereViewModel

private const val TAG = "MySpheresFragment"

class MySpheresFragment : Fragment() {

    private var _binding: FragmentMySpheresBinding? = null
    private val binding get() = _binding!!
    private lateinit var sphereViewModel: SphereViewModel

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        Log.i(TAG, "onCreateView() Started")

        _binding = FragmentMySpheresBinding.inflate(inflater, container, false)

        Log.i(TAG, "onCreateView() Returning")

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        Log.i(TAG, "onViewCreated() Started")

        val recyclerView = binding.spheresRecyclerView
        val adapter = SphereListAdapter()
        recyclerView.adapter = adapter
        recyclerView.layoutManager = LinearLayoutManager(activity)

        sphereViewModel = ViewModelProvider(this)[SphereViewModel::class.java]

        sphereViewModel.allSpheres.observe(viewLifecycleOwner, { spheres ->
            spheres?.let { adapter.submitList(it) }
        })

        Log.i(TAG, "onViewCreated() Returning")

    }

}