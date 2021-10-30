package com.sphere.room_code

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.sphere.R
import kotlinx.android.synthetic.main.recyclerview_item.view.*

val TAG = "SphereViewHolder"

class SphereViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
    private val sphereItemView: TextView = itemView.findViewById(R.id.textView)

    fun bind(text: String?) {
        sphereItemView.text = text
    }

    companion object {
        fun create(parent: ViewGroup): SphereViewHolder {
            val view: View = LayoutInflater.from(parent.context)
                .inflate(R.layout.recyclerview_item, parent, false)
            view.delete_button.setOnClickListener {
                Log.i(TAG, "delete_button was pressed.")
            }
            view.load_button.setOnClickListener {
                Log.i(TAG, "load_button was pressed.")
            }
            return SphereViewHolder(view)
        }
    }
}