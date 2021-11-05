package com.sphere.room_code

import android.view.ViewGroup
import androidx.recyclerview.widget.ListAdapter

private const val TAG = "SphereListAdapter"

class SphereListAdapter(
    private val callback: (sphereName: String) -> Unit
) : ListAdapter<Sphere, SphereViewHolder>(SpheresComparator()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SphereViewHolder {
        val viewHolder = SphereViewHolder.create(parent)

        viewHolder.view.setOnClickListener {
            callback(viewHolder.textView.text as String)
        }

        return viewHolder
    }

    override fun onBindViewHolder(holder: SphereViewHolder, position: Int) {
        val current = getItem(position)

        holder.bind(current.name)
    }
}
