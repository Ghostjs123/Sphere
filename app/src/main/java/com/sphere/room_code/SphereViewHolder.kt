package com.sphere.room_code

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.sphere.R


private const val TAG = "SphereViewHolder"

class SphereViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
    private val sphereItemView: TextView = itemView.findViewById(R.id.MySpheresItemText)

    fun bind(text: String?) {
        sphereItemView.text = text

        sphereItemView.setOnClickListener { Log.i(TAG, "Clicked: " + sphereItemView.text as String) }
    }

    companion object {
        fun create(parent: ViewGroup): SphereViewHolder {
            val view: View = LayoutInflater.from(parent.context)
                .inflate(R.layout.my_spheres_item, parent, false)

            return SphereViewHolder(view)
        }
    }
}