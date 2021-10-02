package com.example.cse5236_sphere.menu.activity

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import com.example.cse5236_sphere.R
import com.example.cse5236_sphere.databinding.FragmentNoSphereBinding

/**
 * A simple [Fragment] subclass as the default destination in the navigation.
 */
class NoSphereFragment : Fragment() {

    private var _binding: FragmentNoSphereBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {

        _binding = FragmentNoSphereBinding.inflate(inflater, container, false)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.createNewSphereText.setOnClickListener {
            findNavController().navigate(R.id.action_NoSphereFragment_to_NewSphereFragment)
        }
        binding.importSphereText.setOnClickListener {
            findNavController().navigate(R.id.action_NoSphereFragment_to_ImportSphereFragment)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}