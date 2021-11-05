package com.sphere.menu_fragments

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RadioButton
import android.widget.Toast
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.viewModels
import com.sphere.SphereViewModel
import com.sphere.SphereViewModelFactory
import com.sphere.databinding.FragmentNewSphereBinding
import com.sphere.room_code.SphereApplication
import com.sphere.utility.setSelectedSpherePref

private const val TAG = "NewSphereFragment"


class NewSphereFragment(
    private val updateSphereCallback: (sphereName: String, seed: Long?, subdivision: Int) -> Unit
) : Fragment() {

    private var _binding: FragmentNewSphereBinding? = null
    private val binding get() = _binding!!

    private val sphereViewModel: SphereViewModel by viewModels {
        SphereViewModelFactory((requireActivity().application as SphereApplication).repository)
    }

    private var sphereName = ""
    private var subdivisions = 0

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        Log.i(TAG, "onCreateView() Started")

        _binding = FragmentNewSphereBinding.inflate(inflater, container, false)

        Log.i(TAG, "onCreateView() Returning")

        binding.createSphereButton.visibility = View.VISIBLE

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        Log.i(TAG, "onViewCreated() Started")

        binding.createSphereButton.setOnClickListener {
            sphereName = binding.sphereNameInput.text.toString()

            when {
                sphereName == "" -> {
                    Toast.makeText(
                        requireContext(),
                        "Please enter a sphere name",
                        Toast.LENGTH_LONG
                    ).show()
                }
                subdivisions == 0 -> {
                    Toast.makeText(
                        requireContext(),
                        "Please choose a subdivision",
                        Toast.LENGTH_LONG
                    ).show()
                }
                else -> {
                    updateSphereCallback(sphereName, null, subdivisions)
                    setSelectedSpherePref(requireActivity(), sphereName)
                    sphereViewModel.addSphere(sphereName, null, subdivisions)

                    requireActivity().supportFragmentManager.popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE)
                }
            }
        }

        Log.i(TAG, "onViewCreated() Finished")
    }

    private fun updateChecks(rb: RadioButton) {
        binding.radioNew1.isChecked = false
        binding.radioNew2.isChecked = false
        binding.radioNew3.isChecked = false
        binding.radioNew4.isChecked = false
        binding.radioNew5.isChecked = false

        rb.isChecked = true
    }

    fun onRadioButtonClicked(view: View) {
        if (view is RadioButton && view.isChecked) {
            when (view) {
                binding.radioNew1 -> {
                    subdivisions = 1
                    updateChecks(view)
                }
                binding.radioNew2 -> {
                    subdivisions = 2
                    updateChecks(view)
                }
                binding.radioNew3 -> {
                    subdivisions = 3
                    updateChecks(view)
                }
                binding.radioNew4 -> {
                    subdivisions = 4
                    updateChecks(view)
                }
                binding.radioNew5 -> {
                    subdivisions = 5
                    updateChecks(view)
                }
            }
        }
    }
}
