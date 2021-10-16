package com.sphere.sphere.fragments

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.PopupMenu
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.sphere.R
import com.sphere.databinding.FragmentSphereBinding
import com.sphere.menu.fragments.ImportSphereFragment
import com.sphere.menu.fragments.MySpheresFragment
import com.sphere.menu.fragments.NewSphereFragment
import com.sphere.menu.fragments.SettingsMenuFragment
import com.sphere.sphere.SphereViewModel
import kotlin.random.Random


private const val TAG = "SphereFragment"


class SphereFragment(action: String, sphereName: String) : Fragment(), PopupMenu.OnMenuItemClickListener {

    private lateinit var sphereViewModel: SphereViewModel

    private var _binding: FragmentSphereBinding? = null
    private val binding get() = _binding!!

    private var mAction: String = action
    private var mSphereName: String = sphereName

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        Log.i(TAG, "onCreateView() Started")

        _binding = FragmentSphereBinding.inflate(inflater, container, false)
        sphereViewModel = ViewModelProvider(requireActivity()).get(SphereViewModel::class.java)

        Log.i(TAG, "onCreateView() Returning")

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // mutate button
        binding.mutateButton.setOnClickListener {
            mutateSphere()
        }

        // options Button
        binding.sphereOptionsButton.setOnClickListener {
            showPopup(it)
        }

        handleAction()
    }

    private fun handleAction() {
        when (mAction) {
            "NewSphere" -> {
                createNewSphere()
            }
            "ImportSphere" -> {
                createNewSphereUsingSeed()
            }
            else -> {
                Log.w(TAG, "Recieved un-handled action: $mAction")
            }
        }
    }

    // ========================================================================
    // Sphere Management

    private fun createNewSphere() {
        binding.glSurfaceView.createNewSphere(mSphereName)
        binding.sphereName.text = mSphereName
    }

    private fun createNewSphereUsingSeed() {
        binding.glSurfaceView.createNewSphereUsingSeed(mSphereName, fetchSeedFromFirebase(mSphereName))
        binding.sphereName.text = mSphereName
    }

    private fun mutateSphere() {
        binding.glSurfaceView.mutateSphere(fetchSeed())
    }

    private fun fetchSeed(): Long {
        // TODO: generate seed from device sensors here
        val seed = Random.nextLong(0, 1000)
        sphereViewModel.setSeed(seed)
        return seed
    }

    private fun fetchSeedFromFirebase(sphereName: String): Long {
        // TODO: actually call Firebase here
        return 1000
    }

    // ========================================================================
    // PopupMenu

    private fun showPopup(v: View) {
        Log.i(TAG, "Showing PopupMenu")

        val popup = PopupMenu(activity, v)
        popup.setOnMenuItemClickListener(this)
        popup.menuInflater.inflate(R.menu.sphere_menu, popup.menu)
        popup.show()
    }

    override fun onMenuItemClick(item: MenuItem): Boolean {
        Log.i(TAG, "Selected Item: " + item.title)

        when (item.itemId) {
            R.id.my_spheres_menu_item -> {
                parentFragmentManager.beginTransaction()
                    .replace(R.id.sphere_fragment_container, MySpheresFragment())
                    .addToBackStack(null)
                    .commit()
                return true
            }
            R.id.import_sphere_menu_item -> {
                parentFragmentManager.beginTransaction()
                    .replace(R.id.sphere_fragment_container, ImportSphereFragment())
                    .addToBackStack(null)
                    .commit()
                return true
            }
            R.id.create_sphere_menu_item -> {
                parentFragmentManager.beginTransaction()
                    .replace(R.id.sphere_fragment_container, NewSphereFragment())
                    .addToBackStack(null)
                    .commit()
                return true
            }
            R.id.settings_menu_item -> {
                parentFragmentManager.beginTransaction()
                    .replace(R.id.sphere_fragment_container, SettingsMenuFragment())
                    .addToBackStack(null)
                    .commit()
                return true
            }
            R.id.export_sphere_menu_item -> {
                // TODO: export sphere dialogue
                return true
            }
        }
        return false
    }

}