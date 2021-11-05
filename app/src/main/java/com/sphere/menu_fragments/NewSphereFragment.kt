package com.sphere.menu_fragments

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RadioButton
import android.widget.Toast
import com.sphere.R
import com.sphere.activity.SphereActivity
import com.sphere.databinding.FragmentNewSphereBinding
import com.sphere.utility.setSelectedSpherePref

private const val TAG = "NewSphereFragment"


// NOTE: callback is for usage from the SphereFragment
class NewSphereFragment(
    private val callback: (sphereName: String, subdivision: Int) -> Unit
) : Fragment() {

    private var _binding: FragmentNewSphereBinding? = null
    private val binding get() = _binding!!

    private var sphereName = ""
    private var subdivision = 0


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
                subdivision == 0 -> {
                    Toast.makeText(
                        requireContext(),
                        "Please choose a subdivision",
                        Toast.LENGTH_LONG
                    ).show()
                }
                else -> {
                    callback(sphereName, subdivision)

                    (requireActivity() as SphereActivity).addNewSphereToViewModel(sphereName, null, subdivision)

                    requireActivity().supportFragmentManager.popBackStack()
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
                    subdivision = 1
                    updateChecks(view)
                }
                binding.radioNew2 -> {
                    subdivision = 2
                    updateChecks(view)
                }
                binding.radioNew3 -> {
                    subdivision = 3
                    updateChecks(view)
                }
                binding.radioNew4 -> {
                    subdivision = 4
                    updateChecks(view)
                }
                binding.radioNew5 -> {
                    subdivision = 5
                    updateChecks(view)
                }
            }
        }
    }
}