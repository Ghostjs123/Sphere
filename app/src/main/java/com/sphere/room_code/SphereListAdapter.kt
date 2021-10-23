package com.sphere.room_code

import android.view.ViewGroup
import androidx.recyclerview.widget.ListAdapter
import com.sphere.room_code.SphereViewHolder

class SphereListAdapter : ListAdapter<Sphere, SphereViewHolder>(SpheresComparator()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SphereViewHolder {
        return SphereViewHolder.create(parent)
    }

    override fun onBindViewHolder(holder: SphereViewHolder, position: Int) {
        val current = getItem(position)
        holder.bind(current.name)
    }
}
