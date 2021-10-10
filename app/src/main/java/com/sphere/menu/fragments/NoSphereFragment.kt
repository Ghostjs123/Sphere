package com.sphere.menu.fragments

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.sphere.R
import com.sphere.databinding.FragmentNoSphereBinding
import com.sphere.menu.MenuViewModel

private const val TAG = "NoSphereFragment"


class NoSphereFragment : Fragment() {

    private var _binding: FragmentNoSphereBinding? = null
    private val binding get() = _binding!!
    private val model: MenuViewModel by activityViewModels()

    override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {

        Log.i(TAG, "onCreateView() Started")

        _binding = FragmentNoSphereBinding.inflate(inflater, container, false)

        Log.i(TAG, "onCreateView() Returning")

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        Log.i(TAG, "onViewCreated() Started")

        // TODO : Can use ViewModel from here

        binding.createNewSphereText.setOnClickListener {
            findNavController().navigate(R.id.action_NoSphereFragment_to_NewSphereFragment)
        }
        binding.importSphereText.setOnClickListener {
            findNavController().navigate(R.id.action_NoSphereFragment_to_ImportSphereFragment)
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