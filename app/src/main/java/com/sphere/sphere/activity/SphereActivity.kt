package com.sphere.sphere.activity

import android.app.Activity
import android.os.Bundle
import android.util.Log
import android.view.ViewGroup
import android.widget.Button
import android.widget.LinearLayout
import android.widget.RelativeLayout
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.view.marginBottom
import com.google.android.material.snackbar.Snackbar
import com.sphere.R
import com.sphere.sphere.fragments.OpenGLSurfaceView

private const val TAG = "SphereActivity"

class SphereActivity : Activity() {

    lateinit var glView: OpenGLSurfaceView

    public override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        Log.i(TAG, "onCreate() Started")

        // create GLSurfaceView

        glView = OpenGLSurfaceView(this)

        setContentView(glView)

        // add mutate button

        val button = Button(this).apply {
            text = resources.getString(R.string.Mutate)
            setOnClickListener  {
                glView.renderer.icosphere.mutate()
                glView.requestRender()
            }
        }
        val layoutParams = RelativeLayout.LayoutParams(
            ViewGroup.LayoutParams.WRAP_CONTENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        ).apply {
            addRule(RelativeLayout.ALIGN_PARENT_BOTTOM)
            addRule(RelativeLayout.ALIGN_PARENT_RIGHT)
        }
        addContentView(button, layoutParams)

        // add options button

        // TODO

        Log.i(TAG, "onCreate() Finished")
    }
}