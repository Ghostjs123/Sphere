package com.sphere.menu_fragments

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.sphere.R
import com.sphere.SphereViewModel
import com.sphere.databinding.FragmentMySpheresBinding
import com.sphere.room_code.Sphere
import com.sphere.room_code.SphereListAdapter


private const val TAG = "MySpheresFragment"

class MySpheresFragment : Fragment() {

    private var _binding: FragmentMySpheresBinding? = null
    private val binding get() = _binding!!
    private val sphereViewModel: SphereViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        Log.i(TAG, "onCreateView() Started")

        _binding = FragmentMySpheresBinding.inflate(inflater, container, false)

        val recyclerView = binding.spheresRecyclerView
        val adapter = SphereListAdapter()

        recyclerView.adapter = adapter
        recyclerView.layoutManager = LinearLayoutManager(activity)

        sphereViewModel.allSpheres.observe(viewLifecycleOwner, { spheres ->
            spheres?.let { adapter.submitList(it) }
        })

        Log.i(TAG, "onCreateView() Returning")

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        Log.i(TAG, "onViewCreated() Started")

        Log.i(TAG, "onViewCreated() Returning")
    }
}

class SphereAdapter(
    private val context: Context,
    private val list: List<Sphere>
) : RecyclerView.Adapter<SphereAdapter.ViewHolder>() {

    class ViewHolder(view: View): RecyclerView.ViewHolder(view) {
        val sphereName: TextView = view.findViewById(R.id.MySpheresItemText)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.my_spheres_item,parent, false)

        return ViewHolder(view)
    }

    override fun getItemCount(): Int {
        return list.count()
    }

    override fun onBindViewHolder(holder: SphereAdapter.ViewHolder, position: Int) {
        val data = list[position]
        holder.sphereName.text = data.name
    }
}
