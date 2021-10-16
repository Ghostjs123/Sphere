package com.sphere.sphere.room_code

import androidx.recyclerview.widget.DiffUtil

class SpheresComparator : DiffUtil.ItemCallback<Sphere>() {
    override fun areItemsTheSame(oldItem: Sphere, newItem: Sphere): Boolean {
        return oldItem === newItem
    }

    override fun areContentsTheSame(oldItem: Sphere, newItem: Sphere): Boolean {
        return oldItem.name == newItem.name
    }
}