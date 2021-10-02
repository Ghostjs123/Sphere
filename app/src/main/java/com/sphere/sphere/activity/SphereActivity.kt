package com.sphere.sphere.activity

import android.app.Activity
import android.os.Bundle
import android.opengl.GLSurfaceView
import com.sphere.sphere.OpenGLSurfaceView


class SphereActivity : Activity() {

    private lateinit var gLView: GLSurfaceView

    public override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        gLView = OpenGLSurfaceView(this)
        setContentView(gLView)
    }
}