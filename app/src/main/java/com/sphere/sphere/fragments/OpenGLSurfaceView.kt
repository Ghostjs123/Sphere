package com.sphere.sphere.fragments

import android.content.Context
import android.opengl.GLSurfaceView
import android.util.AttributeSet
import android.util.DisplayMetrics
import android.util.Log
import android.view.MotionEvent
import android.view.View
import com.sphere.sphere.Icosphere
import com.sphere.sphere.fragments.OpenGLRenderer
import kotlin.math.abs

private const val TAG = "OpenGLSurfaceView"

private const val TOUCH_SCALE_FACTOR: Float = 0.2f

class OpenGLSurfaceView(context: Context, attrs: AttributeSet) : GLSurfaceView(context, attrs) {

    private val renderer: OpenGLRenderer

    private var prevX: Float = 0f
    private var prevY: Float = 0f

    init {
        setEGLContextClientVersion(1)

        renderer = OpenGLRenderer()
        setRenderer(renderer)
        renderMode = RENDERMODE_WHEN_DIRTY
    }

    fun createNewSphere(sphere_name: String) {
        Log.i(TAG, "Creating Sphere named: $sphere_name")
        renderer.icosphere = Icosphere(sphere_name)
        requestRender()
    }

    fun mutateSphere() {
        Log.i(TAG, "Attempting to mutate Sphere")
        renderer.icosphere?.mutate()
        requestRender()
    }

    override fun onTouchEvent(e: MotionEvent): Boolean {
        val x: Float = e.x
        val y: Float = e.y

        when (e.action) {
            MotionEvent.ACTION_MOVE -> {
                renderer.cameraAngleX += (y - prevY) * TOUCH_SCALE_FACTOR

                val directionHandler = abs((renderer.cameraAngleX.toInt() + 90) / 180) % 2

                if (directionHandler != 0)
                    renderer.cameraAngleY -= (x - prevX) * TOUCH_SCALE_FACTOR
                else
                    renderer.cameraAngleY += (x - prevX) * TOUCH_SCALE_FACTOR

                requestRender()
            }
        }

        prevX = x
        prevY = y

        return true
    }
}