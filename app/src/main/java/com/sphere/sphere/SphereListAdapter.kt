package com.sphere.sphere

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.sphere.R

class SphereListAdapter : ListAdapter<Sphere, SphereListAdapter.SphereViewHolder>(SpheresComparator()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SphereViewHolder {
        return SphereViewHolder.create(parent)
    }

    override fun onBindViewHolder(holder: SphereViewHolder, position: Int) {
        val current = getItem(position)
        holder.bind(current.name)
    }

    class SphereViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val sphereItemView: TextView = itemView.findViewById(R.id.textView)

        fun bind(text: String?) {
            sphereItemView.text = text
        }

        companion object {
            fun create(parent: ViewGroup): SphereViewHolder {
                val view: View = LayoutInflater.from(parent.context)
                    .inflate(R.layout.recyclerview_item, parent, false)
                return SphereViewHolder(view)
            }
        }
    }

    class SpheresComparator : DiffUtil.ItemCallback<Sphere>() {
        override fun areItemsTheSame(oldItem: Sphere, newItem: Sphere): Boolean {
            return oldItem === newItem
        }

        override fun areContentsTheSame(oldItem: Sphere, newItem: Sphere): Boolean {
            return oldItem.name == newItem.name
        }
    }
}
