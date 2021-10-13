package com.sphere.menu.fragments

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import com.sphere.databinding.FragmentNewSphereBinding
import com.google.android.material.snackbar.Snackbar
import com.sphere.menu.MenuViewModel

private const val TAG = "NewSphereFragment"


class NewSphereFragment : Fragment() {

    private var _binding: FragmentNewSphereBinding? = null
    private val binding get() = _binding!!
    private val model: MenuViewModel by activityViewModels()


    override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {

        Log.i(TAG, "onCreateView() Started")

        _binding = FragmentNewSphereBinding.inflate(inflater, container, false)

        Log.i(TAG, "onCreateView() Returning")

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        Log.i(TAG, "onViewCreated() Started")

        // TODO : Can use modelView from here

        binding.createSphereButton.setOnClickListener { view ->
            val sphereName = binding.sphereNameInput.text.toString()

            Snackbar.make(view, "Creating $sphereName", Snackbar.LENGTH_LONG)
                .setAction("Action", null).show()
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