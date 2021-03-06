package com.sphere.menu_fragments

import android.app.AlertDialog
import android.content.DialogInterface
import android.os.Bundle
import android.text.InputType
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.core.content.res.ResourcesCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentContainerView
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.sphere.R
import com.sphere.SphereViewModel
import com.sphere.activity.SphereActivity
import com.sphere.databinding.FragmentMySpheresBinding
import com.sphere.room_code.SphereListAdapter
import com.sphere.sphere_fragments.SphereFragment
import com.sphere.utility.deleteSphereBitmap
import com.sphere.utility.getSelectedSpherePref
import com.sphere.utility.renameSphereBitmap
import com.sphere.utility.setSelectedSpherePref

private const val TAG = "MySpheresFragment"


class MySpheresFragment: Fragment() {

    private var _binding: FragmentMySpheresBinding? = null
    private val binding get() = _binding!!

    private val sphereViewModel: SphereViewModel by activityViewModels()

    private lateinit var initialSphere: String

    companion object {
        private lateinit var updateSphereCallback: (sphereName: String, seed: Long?, subdivision: Int) -> Unit
        private lateinit var renameSphereCallback: (sphereName: String) -> Unit

        fun newInstance(
            updateSphereCallback: (sphereName: String, seed: Long?, subdivision: Int) -> Unit,
            renameSphereCallback: (sphereName: String) -> Unit
        ): MySpheresFragment {
            val fragment = MySpheresFragment()
            this.updateSphereCallback = updateSphereCallback
            this.renameSphereCallback = renameSphereCallback

            return fragment
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        Log.i(TAG, "onCreateView() Started")

        _binding = FragmentMySpheresBinding.inflate(inflater, container, false)

        val recyclerView = binding.MySpheresRecyclerView
        val adapter = SphereListAdapter(::sphereSelected)

        recyclerView.adapter = adapter
        recyclerView.layoutManager = LinearLayoutManager(activity)

        sphereViewModel.allSpheres.observe(viewLifecycleOwner, { spheres ->
            spheres?.let { adapter.submitList(it) }
        })

        initialSphere = getSelectedSpherePref(requireActivity())!!

        Log.i(TAG, "onCreateView() Returning")

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        Log.i(TAG, "onViewCreated() Started")

        binding.MySpheresDeleteButton.setOnClickListener {
            showDeleteDialogue()
        }

        binding.MySpheresRenameButton.setOnClickListener {
            showRenameDialogue()
        }

        binding.mySpheresToolbar.setNavigationIcon(R.drawable.abc_ic_ab_back_material)
        binding.mySpheresToolbar.setNavigationOnClickListener {
            requireActivity().supportFragmentManager.popBackStack()
        }

        Log.i(TAG, "onViewCreated() Returning")
    }

    override fun onViewStateRestored(savedInstanceState: Bundle?) {
        super.onViewStateRestored(savedInstanceState)

        updateSphereCallback = (activity as SphereActivity).getUpdateSphereCallback()
        renameSphereCallback = (activity as SphereActivity).getRenameSphereCallback()
    }

    override fun onDestroyView() {
        super.onDestroyView()

        Log.i(TAG, "onDestroyView() Started")

        val selectedSphere = getSelectedSpherePref(requireActivity())

        // Refresh the ViewModel before exit
        sphereViewModel.loadSphere(selectedSphere!!)

        // If user selected a new sphere, load that different sphere on exit
        if (initialSphere != selectedSphere) {

            updateSphereCallback.invoke(selectedSphere, sphereViewModel.getSeed(), sphereViewModel.getSubdivisions())


            Log.i(TAG, "onDestroyView() Updated the sphere before returning")
        }

        Log.i(TAG, "onDestroyView() Returning")
    }

    private fun sphereSelected(sphereName: String) {
        setSelectedSpherePref(requireActivity(), sphereName)
        updateSelectedBorder()

        sphereViewModel.loadSphere(sphereName)
    }

    private fun updateSelectedBorder() {
        for (i in 0 until binding.MySpheresRecyclerView.childCount) {
            val view = binding.MySpheresRecyclerView.getChildAt(i)
            val textView = view.findViewById(R.id.MySpheresItemText) as TextView
            val selectedSphere = getSelectedSpherePref(requireActivity())

            if (textView.text == selectedSphere) {
                view.background = ResourcesCompat.getDrawable(
                    resources,
                    R.drawable.blue_border,
                    requireContext().theme
                )
            }
            else {
                view.background = ResourcesCompat.getDrawable(
                    resources,
                    R.drawable.black_border,
                    requireContext().theme
                )
            }
        }
    }

    // ================================================================================
    // Delete / Rename dialogues

    private fun showDeleteDialogue() {
        val selected = getSelectedSpherePref(requireActivity())

        val dialogClickListener =
            DialogInterface.OnClickListener { _, which ->
                when (which) {
                    DialogInterface.BUTTON_POSITIVE -> {
                        val sphereCount = sphereViewModel.getSphereCount()
                        if (sphereCount == 1) {
                            Toast.makeText(
                                requireContext(),
                                R.string.delete_warning,
                                Toast.LENGTH_LONG
                            ).show()
                        } else {
                            sphereViewModel.loadNeighbor()
                            setSelectedSpherePref(requireActivity(), sphereViewModel.getName())
                            sphereViewModel.delete(selected)
                            deleteSphereBitmap(requireContext(), selected!!)
                        }
                    }
                    DialogInterface.BUTTON_NEGATIVE -> { }
                }
            }

        val builder = AlertDialog.Builder(requireContext())
        builder.setMessage(getString(R.string.delete_sphere) + " $selected?")
            .setPositiveButton(R.string.button_yes, dialogClickListener)
            .setNegativeButton(R.string.button_no, dialogClickListener)
            .show()
    }

    private fun showRenameDialogue() {
        val selected = getSelectedSpherePref(requireActivity())
        val editText = EditText(requireActivity())
        editText.inputType = InputType.TYPE_CLASS_TEXT

        val dialogClickListener =
            DialogInterface.OnClickListener { _, which ->
                when (which) {
                    DialogInterface.BUTTON_POSITIVE -> {
                        val newName = editText.text.toString()
                        sphereViewModel.setName(newName)
                        renameSphereCallback?.invoke(newName)
                        setSelectedSpherePref(requireActivity(), sphereViewModel.getName())
                        renameSphereBitmap(requireContext(), selected!!, newName)
                    }
                    DialogInterface.BUTTON_NEGATIVE -> { }
                }
            }

        val builder = AlertDialog.Builder(requireContext())
        builder.setMessage(getString(R.string.rename_sphere) + " $selected?")
            .setView(editText)
            .setPositiveButton(R.string.rename, dialogClickListener)
            .setNegativeButton(R.string.button_back, dialogClickListener)
            .show()
    }
}
