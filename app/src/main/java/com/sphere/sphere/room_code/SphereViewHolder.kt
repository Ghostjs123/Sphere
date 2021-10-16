package com.sphere.sphere.room_code

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.sphere.R

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