package com.sphere.room_code

import android.graphics.BitmapFactory
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.res.ResourcesCompat
import androidx.recyclerview.widget.RecyclerView
import com.sphere.R
import com.sphere.utility.getSelectedSpherePref
import java.io.File

private const val TAG = "SphereViewHolder"


class SphereViewHolder(val view: View) : RecyclerView.ViewHolder(view) {
    val textView: TextView = view.findViewById(R.id.MySpheresItemText)
    val imageView: ImageView = view.findViewById(R.id.MySpheresItemImage)

    fun bind(text: String?) {
        val context = view.context

        val fPath = context.getExternalFilesDir(null)?.absolutePath
        val file = File(fPath + File.separator + text)

        // update TextView
        textView.text = text

        // update ImageView if there's an image for it
        if (file.exists()) {
            imageView.setImageBitmap(BitmapFactory.decodeFile(file.absolutePath))
        }

        // update background if item is selected
        if (text == getSelectedSpherePref(context)) {
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