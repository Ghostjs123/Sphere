package com.sphere.menu.fragments

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.sphere.databinding.FragmentNewSphereBinding
import com.google.android.material.snackbar.Snackbar
import com.sphere.sphere.activity.SphereActivity
import com.sphere.utility.readSphereFromFirestore

private const val TAG = "NewSphereFragment"


// NOTE: optional callback is just for usage from the SphereActivity
class NewSphereFragment(
    private val callback: ((sphereName: String) -> Unit)? = null
) : Fragment() {

    private var _binding: FragmentNewSphereBinding? = null
    private val binding get() = _binding!!


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        Log.i(TAG, "onCreateView() Started")

        _binding = FragmentNewSphereBinding.inflate(inflater, container, false)

        Log.i(TAG, "onCreateView() Returning")

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        Log.i(TAG, "onViewCreated() Started")

        binding.createSphereButton.setOnClickListener {
            val sphereName = binding.sphereNameInput.text.toString()

            if (callback == null) {
                // MenuActivity -> SphereActivity
                startActivity(Intent(activity, SphereActivity::class.java).apply {
                    putExtra("ACTION", "NewSphere")
                    putExtra("SPHERE_NAME", sphereName)
                    addFlags(
                        Intent.FLAG_ACTIVITY_NEW_TASK or
                                Intent.FLAG_ACTIVITY_CLEAR_TASK or
                                Intent.FLAG_ACTIVITY_CLEAR_TOP
                    )
                })
            }
            else {
                // SphereActivity -> SphereActivity
                callback.invoke(sphereName)
                requireActivity().supportFragmentManager.popBackStack()
            }
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