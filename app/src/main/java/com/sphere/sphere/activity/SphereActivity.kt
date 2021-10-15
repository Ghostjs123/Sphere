package com.sphere.sphere.activity

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.*
import android.widget.*
import com.sphere.R
import com.sphere.menu.activity.MenuActivity
import com.sphere.sphere.fragments.OpenGLSurfaceView

private const val TAG = "SphereActivity"

class SphereActivity : Activity(), PopupMenu.OnMenuItemClickListener {

    private lateinit var glView: OpenGLSurfaceView
    private lateinit var mButton: Button
    private lateinit var mImageButton: ImageButton
    private lateinit var mSphereName: TextView

    public override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        Log.i(TAG, "onCreate() Started")

        setContentView(R.layout.activity_sphere)

        // GLSurfaceView
        glView = findViewById(R.id.gl_surface_view)

        // sphere name text
        mSphereName = findViewById(R.id.sphere_name)

        // mutate Button
        mButton = findViewById(R.id.mutate_button)
        mButton.setOnClickListener {
            glView.mutateSphere()
        }

        // options Button
        mImageButton = findViewById(R.id.sphere_options_button)
        mImageButton.setOnClickListener {
            showPopup(it)
        }

        // handle intent
        when (val intentAction = intent.getStringExtra("ACTION")) {
            "CreateNew" -> {
                val sphereName = intent.getStringExtra("SPHERE_NAME")
                glView.createNewSphere(sphereName!!)
                mSphereName.text = sphereName
            }
            else -> {
                Log.i(TAG, "Un-handled intent action: $intentAction")
            }
        }

        Log.i(TAG, "onCreate() Finished")
    }

    private fun showPopup(v: View) {
        Log.i(TAG, "Showing PopupMenu")

        val popup = PopupMenu(this, v)
        popup.setOnMenuItemClickListener(this)
        popup.menuInflater.inflate(R.menu.sphere_menu, popup.menu)
        popup.show()
    }

    private fun startMenuActivityWithExtra(id: Int) {
        startActivity(Intent(this, MenuActivity::class.java).apply {
            putExtra("EXTRA_MESSAGE", id)
        })
    }

    override fun onMenuItemClick(item: MenuItem): Boolean {
        Log.i(TAG, "Selected Item: " + item.title)

        when (item.itemId) {
            R.id.my_spheres_menu_item -> {
                startMenuActivityWithExtra(item.itemId)
                return true
            }
            R.id.import_sphere_menu_item -> {
                startMenuActivityWithExtra(item.itemId)
                return true
            }
            R.id.create_sphere_menu_item -> {
                startMenuActivityWithExtra(item.itemId)
                return true
            }
            R.id.settings_menu_item -> {
                startMenuActivityWithExtra(item.itemId)
                return true
            }
            R.id.export_sphere_menu_item -> {
                startMenuActivityWithExtra(item.itemId)
                return true
            }
        }

        return false
    }
}