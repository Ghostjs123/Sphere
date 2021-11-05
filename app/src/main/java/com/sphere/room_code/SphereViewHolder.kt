package com.sphere.room_code

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.content.res.ResourcesCompat
import androidx.recyclerview.widget.RecyclerView
import com.sphere.R
import com.sphere.utility.getSelectedSpherePref

private const val TAG = "SphereViewHolder"


class SphereViewHolder(val view: View) : RecyclerView.ViewHolder(view) {
    val textView: TextView = view.findViewById(R.id.MySpheresItemText)

    fun bind(text: String?) {
        textView.text = text

        val context = view.context

        val tmp = getSelectedSpherePref(context)

        if (text == tmp) {
            view.background = ResourcesCompat.getDrawable(
                context.resources,
                R.drawable.blue_border,
                context.theme
            )
        }
    }

    companion object {
        fun create(parent: ViewGroup): SphereViewHolder {
            val view: View = LayoutInflater.from(parent.context)
                .inflate(R.layout.my_spheres_item, parent, false)

            return SphereViewHolder(view)
        }
    }
}