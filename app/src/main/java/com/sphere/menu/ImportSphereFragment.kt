package com.sphere.menu

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.sphere.databinding.FragmentImportSphereBinding
import com.google.android.material.snackbar.Snackbar


/**
 * A simple [Fragment] subclass.
 */
class ImportSphereFragment : Fragment() {

    private var _binding: FragmentImportSphereBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        _binding = FragmentImportSphereBinding.inflate(inflater, container, false)
        return binding.root

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.importSphereButton.setOnClickListener { view ->
            val sphereName = binding.sphereNameInput.text.toString()

            Snackbar.make(view, "Importing $sphereName", Snackbar.LENGTH_LONG)
                .setAction("Action", null).show()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}