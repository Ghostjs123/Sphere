package com.sphere.sphere.activity

import android.app.Activity
import android.os.Bundle
import android.opengl.GLSurfaceView
import androidx.lifecycle.ViewModelProvider
import com.sphere.sphere.OpenGLSurfaceView
import com.sphere.sphere.SphereViewModel


class SphereActivity : Activity() {

    private lateinit var gLView: GLSurfaceView

    public override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)



        gLView = OpenGLSurfaceView(this)
        setContentView(gLView)
    }
}