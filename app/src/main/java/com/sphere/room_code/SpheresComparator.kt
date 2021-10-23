package com.sphere.room_code

import androidx.recyclerview.widget.DiffUtil
import com.sphere.room_code.Sphere

class SpheresComparator : DiffUtil.ItemCallback<Sphere>() {
    override fun areItemsTheSame(oldItem: Sphere, newItem: Sphere): Boolean {
        return oldItem === newItem
    }

    override fun areContentsTheSame(oldItem: Sphere, newItem: Sphere): Boolean {
        return oldItem.name == newItem.name
    }
}