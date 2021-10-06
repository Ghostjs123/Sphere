package com.sphere.sphere.activity

import android.app.Activity
import android.os.Bundle
import android.opengl.GLSurfaceView
import android.util.Log
import androidx.lifecycle.ViewModelProvider
import com.sphere.sphere.OpenGLSurfaceView
import com.sphere.sphere.SphereViewModel

private const val TAG = "SphereActivity"

class SphereActivity : Activity() {

    private lateinit var gLView: GLSurfaceView

    public override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        Log.i(TAG, "onCreate() Started")

        gLView = OpenGLSurfaceView(this)
        setContentView(gLView)

        Log.i(TAG, "onCreate() Finished")
    }
}